/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.cyc.simulation;

import java.util.*;

import es.ull.cyc.simulation.results.ActivityStatistics;
import es.ull.cyc.simulation.results.ElementStatistics;
import es.ull.cyc.simulation.results.PendingFlowStatistics;
import es.ull.cyc.simulation.results.SimulationResults;
import es.ull.cyc.simulation.results.StatisticData;
import es.ull.cyc.sync.Lock;
import es.ull.cyc.util.*;

/**
 * Main simulation class. It creates all the structures needed to carry out a
 * simulation: activity managers, logical processes...<p>
 * It implements the Runnable interface in order to allow both a sequencial 
 * (by invoking the <code>run</code> method) and a threaded execution (<code>start</code>).
 * It generates a set of simulation results that can be obtained by using the 
 * <code>getResults</code> method.  
 * @author Iván Castilla Rodríguez
 */
public abstract class Simulation implements Printable, Runnable {
    /** A brief description of the model. */
    String description;
	/** List of resources present in the simulation. */
	protected ArrayList<Resource> resourceList;
	/** List of element generators of the simulation. */
	protected ArrayList<Generator> generatorList;
	/** List of activities present in the simulation. */
	protected OrderedList<Activity> activityList;
    /** List of resource types present in the simulation. */
    protected OrderedList<ResourceType> resourceTypeList;
    /** List of resource types present in the simulation. */
    protected OrderedList<ElementType> elementTypeList;
    /** List of activity managers that partition the simulation. */
    protected ArrayList<ActivityManager> activityManagerList;    
    /** Logical Process list */
    protected LogicalProcess[] logicalProcessList;
    /** Timestamp of simulation's start */
    protected double startTs;
    /** Timestamp of Simulation's end */
    protected double endTs;
    /** Total amount of Elements */
    protected int elemMeter;
    /** Output for printing messages */
    protected Output out;
    /** Simulation Results */
    protected SimulationResults results = null;
    /** End-of-simulation control */
    private Lock simLock;
    /** Thread for threaded-execution of the simulation */
    private Thread simThread = null;
	/** Default element type for non presential elements */
	private ElementType npElementType;
    
    /** Creates a new instance of Simulation */
    public Simulation(String description, double startTs, double endTs, Output out) {
        activityList = new OrderedList<Activity>();
        resourceTypeList = new OrderedList<ResourceType>();
        elementTypeList = new OrderedList<ElementType>();
        activityManagerList = new ArrayList<ActivityManager>();
    	this.description = description;
        this.startTs = startTs;
        this.endTs = endTs;
        this.elemMeter = 0;
        this.out = out;
        npElementType = new ElementType(-1, this, "Non presential element type");
        simLock = new Lock();
        // MOD 28/03/05 Para poder recuperar una simulación lo hago en el init
//        this.results = new SimulationResults();
    }
    
    /** Creates a new instance of Simulation */
    public Simulation(String description, double startTs, double endTs) {
    	this(description, startTs, endTs, new Output());
    }
    
    // MOD 28/03/05 Para poder recuperar una simulación
    /** Creates a new instance of Simulation which continues a previous simulation. */
    public Simulation(String description, double endTs, Output out, SimulationResults previous) {
    	this(description, previous.getSimEnd(), endTs, out);
        this.results = previous;
    }
    
    // MOD 28/03/05 Para poder recuperar una simulación
    /** Creates a new instance of Simulation which continues a previous simulation. */
    public Simulation(String description, double endTs, SimulationResults previous) {
    	this(description, endTs, new Output(), previous);
    }
    
    /**
     * Simulation initialization. It creates all the neccesary structures.
     */
    protected void init() {
        createModel();
        createActivityManagers();
        createSimulation();
        // MOD 28/03/05 Para poder recuperar una simulación
        if (results != null) {
        	SimulationResults previous = results;
            this.results = new SimulationResults();
        	setState(previous);
        }
        else
            this.results = new SimulationResults();
        results.saveSimulationStructure(this);
        // FIXME: Debería hacer un reparto más inteligente tanto de generadores como de recursos
        generatorList = createGenerators();
        // Starts all the generators
        for (Generator gen : generatorList)
        	gen.start(getDefaultLogicalProcess());
        resourceList = createResources();
        // Starts all the resources
        for (Resource res : resourceList)
            res.start(getDefaultLogicalProcess());
    }
    
    class FlowStatComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			PendingFlowStatistics p1 = (PendingFlowStatistics)o1;
			PendingFlowStatistics p2 = (PendingFlowStatistics)o2;
			if (p1.getElemId() < p2.getElemId())
				return -1;
			if (p1.getElemId() > p2.getElemId())
				return 1;
			return 0;
		}
    }
    
    private void buildElementFlow(Element elem, Flow parent, Iterator itFlow, HashMap<Integer, Flow> sflowMap) {
		PendingFlowStatistics pFlow = (PendingFlowStatistics)itFlow.next();
		Flow f = null;
		switch(pFlow.getType()) {
			case PendingFlowStatistics.SECFLOW: 
				f = new SequenceFlow((SimultaneousFlow)parent, elem);
				for (int i = 0; i < pFlow.getValue(); i++)
					buildElementFlow(elem, f, itFlow, sflowMap);
				break;
			case PendingFlowStatistics.SIMFLOW:
				f = new SimultaneousFlow((SequenceFlow)parent, elem);
				for (int i = 0; i < pFlow.getValue(); i++)
					buildElementFlow(elem, f, itFlow, sflowMap);
				break;
			case PendingFlowStatistics.SINFLOW:
				int flowId = pFlow.getValue();
				pFlow = (PendingFlowStatistics)itFlow.next();
				if (pFlow.getType() != PendingFlowStatistics.ACTFLOW)
					print(Output.MessageType.ERROR, "Expected activity description in pending flow statistics.");
				Activity act = activityList.get(new Integer(pFlow.getValue()));
				// FIXME: Qué pasa con las No presenciales?
				// Respuesta: NO deberían aparecer, puesto que se deberían
				// haber considerado terminadas => ¿Ocurre esto?
				f = new SingleFlow((GroupFlow)parent, elem, act);
				((SingleFlow)f).setId(flowId);
				sflowMap.put(new Integer(flowId), f);
				break;
			default:
				print(Output.MessageType.ERROR, "Unexpected type in pending flow statistics.");					
				break;
		}
    }
    /**
     * Fills up the simulation with data from a previous simulation.
     * @param previous Previous simulation data
     */
    protected void setState(SimulationResults previous) {
    	ArrayList actStat = previous.getActivityStatistics();
    	Collections.sort(previous.getPendingFlowStatistics(), new FlowStatComparator());
    	Iterator itFlow = previous.getPendingFlowStatistics().iterator();
    	HashMap<Integer, InterruptedElement> elemMap = new HashMap<Integer, InterruptedElement>();
    	HashMap<Integer, Flow> sflowMap = new HashMap<Integer, Flow>();
    	// The pending elements are recovered
    	while (itFlow.hasNext()) {
    		PendingFlowStatistics pFlow = (PendingFlowStatistics)itFlow.next();
    		InterruptedElement elem = new InterruptedElement(pFlow.getElemId(), this, pFlow.getElemType());
			elemMap.put(new Integer(pFlow.getElemId()), elem);
			Flow root = null;
			switch(pFlow.getType()) {
				case PendingFlowStatistics.SECFLOW: 
					root = new SequenceFlow(elem);
					for (int i = 0; i < pFlow.getValue(); i++)
						buildElementFlow(elem, root, itFlow, sflowMap);
					break;
				case PendingFlowStatistics.SIMFLOW:
					root = new SimultaneousFlow(elem);
					for (int i = 0; i < pFlow.getValue(); i++)
						buildElementFlow(elem, root, itFlow, sflowMap);
					break;
				case PendingFlowStatistics.SINFLOW:
					int flowId = pFlow.getValue();
					pFlow = (PendingFlowStatistics)itFlow.next();
					if (pFlow.getType() != PendingFlowStatistics.ACTFLOW)
						print(Output.MessageType.ERROR, "Expected activity description in pending flow statistics.");
					Activity act = activityList.get(new Integer(pFlow.getValue()));
					root = new SingleFlow(elem, act);
					((SingleFlow)root).setId(flowId);
					sflowMap.put(new Integer(flowId), root);
					break;
				default:
					print(Output.MessageType.ERROR, "Unexpected type in pending flow statistics.");					
					break;
			}
			elem.setFlow(root);
    	}
    	// The activity queues are filled
    	for (int i = 0; i < actStat.size(); i++) {
    		ActivityStatistics aStat = (ActivityStatistics)actStat.get(i);
			Activity act = activityList.get(new Integer(aStat.getActId()));
			Element elem = elemMap.get(new Integer(aStat.getElemId()));
			SingleFlow sf = (SingleFlow)sflowMap.get(new Integer(aStat.getFlowId()));
			act.addElement(sf);
			elem.incRequested(sf);
			results.add(new ElementStatistics(elem.getIdentifier(), ElementStatistics.REQACT, startTs, act.getIdentifier()));
    	}
    	// The elements are started
    	Iterator<InterruptedElement> it = elemMap.values().iterator();
    	while (it.hasNext()) {
    		InterruptedElement elem = it.next();
    		elem.start(getDefaultLogicalProcess());
    	}
    	Integer maxFlowId = Collections.max(sflowMap.keySet());
    	SingleFlow.setCounter(maxFlowId.intValue() + 1);
    	// The ids of the new generated elements can't be in the previous simulation  
    	Generator.setElemCounter(previous.getLastElementId());
    	// Por si acaso, para el recolector de basura
    	sflowMap = null;
    	elemMap = null;
    }
    
    /**
     * Contains the specifications of the model. All the components of the
     * model must be declared here.
     * <p>
     * The components are added simply by invoking their constructors. For
     * example:
     * <code>
     * Activity a1 = new Activity(this, "Act1");
     * ResourceType rt1 = new ResourceType(this, "RT1");
     * </code>
     */    
    protected abstract void createModel();

    /**
     * Creates the list of element generators of the simulation.
     * @return A list of element generators
     */
    protected abstract ArrayList<Generator> createGenerators();
    
    /**
     * Creates the list of resources of the simulation. 
     * @return A list of resources
     */
    protected abstract ArrayList<Resource> createResources();
    
    /**
     * Makes a depth first search on a graph.
     * @param graph Graph to be searched.
     * @param current Current node being searching.
     * @param marks Mark array that's used for determining the partition of each node.
     */
    private void dfs(HashSet<Integer> []graph, int current, int []marks) {
    	// This line subtitutes the other three ones
    	for (Integer i : graph[current]) {
//        Iterator<Integer> it = graph[current].iterator();
//        while (it.hasNext()) {
//            Integer i = it.next();
            if (marks[i.intValue()] == -1) {
                marks[i.intValue()] = marks[current];
                // Para acelerar un poco el algoritmo se elimina la arista simétrica
                // FIXME ¿Se podría eliminar tb la propia arista?
                graph[i.intValue()].remove(new Integer(current));
                dfs(graph, i.intValue(), marks);
            }
        }
    }

    /**
     * Creates a graph by using the activities and resource types of the model.
     * The created graph G=(V, E) is created as follows: each vertex is a resource
     * type and each edge is an activity that is associated with the resource types
     * represented by the connected vertex.
     * @return The constructed graph.
     */    
    private HashSet<Integer> []createGraph() {
        int ind1 = -1, ind2 = -1;
        HashSet<Integer> []graph = new HashSet[resourceTypeList.size()];
        
        for (int i = 0; i < resourceTypeList.size(); i++)
            graph[i] = new HashSet<Integer>();
        for (int i = 0; i < activityList.size(); i++) {
            Activity a = activityList.get(i);
            Prioritizable []wgList = a.getWorkGroupTable();
            // Looks for the first RTT that contains at least one resource type
            int j;
            for (j = 0; j < wgList.length; j++)
                if (((WorkGroup)wgList[j]).size() > 0)
                    break;
            // Only if I get one valid object
            if (j < wgList.length) {
                WorkGroup wg = (WorkGroup)wgList[j];
                ind1 = resourceTypeList.indexOf(wg.getResourceType(0));
                for (int k = 1; k < wg.size(); k++) {
                    ind2 = resourceTypeList.indexOf(wg.getResourceType(k));
                    graph[ind1].add(new Integer(ind2));
                    graph[ind2].add(new Integer(ind1));
                    ind1 = ind2;                    
                }
                // The rest of the objects
                for (; j < wgList.length; j++) {
                    wg = (WorkGroup)wgList[j];
                    if (wg.size() > 0) {
                        for (int k = 0; k < wg.size(); k++) {
                            ind2 = resourceTypeList.indexOf(wg.getResourceType(k));
                            graph[ind1].add(new Integer(ind2));
                            graph[ind2].add(new Integer(ind1));
                            ind1 = ind2;
                        }
                    }                
                }
            }
        }
        debugPrintGraph(graph);
        return graph;
    }
    
    /**
     * Creates the activity managers that partition the model. This is equivalent
     * to finding the connected components of a graph G=(V, E) where each vertex is
     * a resource type and each edge is an activity that is associated with the
     * resource types represented by the connected vertex.
     */
    private void createActivityManagers() {        
        // The graph is an array consisting on sets of resource types
        HashSet<Integer> []graph = createGraph();
        int []marks = new int[resourceTypeList.size()];
        for (int i = 0; i < resourceTypeList.size(); i++)
            marks[i] = -1; // Not-visited mark

        // Now the DFS
        int nManagers = 0;  // This counter lets us mark each partition
        for (int i = 0; i < resourceTypeList.size(); i++)
            if (marks[i] == -1) {
                marks[i] = nManagers;                
                dfs(graph, i, marks);
                nManagers++;
            }
        // The activity managers are created
        for (int i = 0; i < nManagers; i++) {
            ActivityManager ga = new ActivityManager(this);
            activityManagerList.add(ga);
        }
        for (int i = 0; i < activityList.size(); i++) {
            Activity a = activityList.get(i);
            // This step is for non-resource-types activities
            Prioritizable []wgList = a.getWorkGroupTable();
            // Looks for the first RTT that contains at least one resource type
            int j;
            for (j = 0; j < wgList.length; j++)
                if (((WorkGroup)wgList[j]).size() > 0)
                    break;
            if (j < wgList.length) {
                int ind = resourceTypeList.indexOf(((WorkGroup)wgList[j]).getResourceType(0));
                ActivityManager ga = activityManagerList.get(marks[ind]);
                a.setManager(ga);
            }
            else {
                ActivityManager ga = new ActivityManager(this);
                activityManagerList.add(ga);
                nManagers++;
                a.setManager(ga);
            }
        }
        for (int i = 0; i < resourceTypeList.size(); i++) {
            ActivityManager ga = activityManagerList.get(marks[i]);
            resourceTypeList.get(i).setManager(ga);
        }

        debugPrintActManager();
    }
        

    private void createLogicalProcesses() {
        logicalProcessList = new LogicalProcess[activityManagerList.size() + 1];
        for (int i = 0; i < activityManagerList.size(); i++)
            logicalProcessList[i] = new LogicalProcess(this, startTs, endTs);
        // Creo el último proceso lógico, que servirá de "cajón de sastre"
        logicalProcessList[activityManagerList.size()] = new LogicalProcess(this, startTs, endTs);
    }
    
    private void createSimulation() {
        //createLogicalProcesses();
        // FIXME De momento sólo voy a utilizar un PL
        logicalProcessList = new LogicalProcess[1];
        logicalProcessList[0] = new LogicalProcess(this, startTs, endTs);
        for (ActivityManager am : activityManagerList) {
            // FIXME
    		am.setLp(logicalProcessList[0]);
        }
    }
    
    /**
     * Starts the simulation execution.
     */    
	public void run() {
		init();
        this.results.setIniT(System.currentTimeMillis());
        for (int i = 0; i < logicalProcessList.length; i++)
            logicalProcessList[i].start();
        waitEnd();
        this.results.setEndT(System.currentTimeMillis());
        this.results.setLastElementId(Generator.getElemCounter());
    }
	
    /**
     * Starts the simulation execution by creating a new thread.
     */    
	public void start() {
		if (simThread == null)  {
			simThread = new Thread(this);
			simThread.run();
		}
	}

    /**
     * Adds an identified object to the model. The allowed id. objects are:
     * {@link Activity}, {@link ResourceType}, {@link ElementType} and
     * {@link WorkGroup}. Any other object is ignored.
     * @param obj Identified object that's added to the model.
     * @return True is the insertion was succesful. False if there already was  
     * an object with the same description in the list.
     */
    protected boolean add(DescSimulationObject obj) {
        boolean resul = false;
        if (obj instanceof ResourceType)
            resul = resourceTypeList.add((ResourceType)obj);
        else if (obj instanceof Activity)
            resul = activityList.add((Activity)obj);
        else if (obj instanceof ElementType)
            resul = elementTypeList.add((ElementType)obj);
        else
        	print(Output.MessageType.ERROR, "Trying to add an unidentified object to the Model");
        return resul;
    }
   
	/**
     * Returns a list of the resources of the model.
     * @return Resources of the model.
     */ 
	public ArrayList<Resource> getResourceList() {
		return resourceList;
	}

	/**
     * Returns a list of the activities of the model.
     * @return Activities of the model.
     */ 
	public OrderedList<Activity> getActivityList() {
		return activityList;
	}
    
    /**
     * Returns the activity with the corresponding identifier.
     * @param id Activity identifier.
     * @return An activity with the indicated identifier.
     */
    public Activity getActivity(int id) {
        return activityList.get(new Integer(id));
    }
    
	/**
     * Returns a list of the resource types of the model.
     * @return Resource types of the model.
     */ 
	public OrderedList<ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}
    
    /**
     * Returns the resource type with the corresponding identifier.
     * @param id Resource type identifier.
     * @return A resource type with the indicated identifier.
     */
    public ResourceType getResourceType(int id) {
        return resourceTypeList.get(new Integer(id));
    }

    /**
     * Returns the element type with the corresponding identifier.
     * @param id element type identifier.
     * @return An element type with the indicated identifier.
     */
    public ElementType getElementType(int id) {
        return elementTypeList.get(new Integer(id));
    }
    
	/**
	 * @return the npElementType
	 */
	public ElementType getNPElementType() {
		return npElementType;
	}

	/**
     * Returns a list of the activity managers of the model.
     * @return Work activity managers of the model.
     */ 
    public ArrayList<ActivityManager> getActivityManagerList() {
        return activityManagerList;
    }
    
    // TEMPORAL
    public LogicalProcess getLogicalProcess(int ind) {
        return logicalProcessList[ind];
    }
    
    public int getLPSize() {
    	return logicalProcessList.length;
    }
    
    public LogicalProcess getDefaultLogicalProcess() {
        return logicalProcessList[logicalProcessList.length - 1];
    }
    
    public synchronized void incElements() {
        elemMeter++;
    }
    
    public synchronized void decElements() {
        elemMeter--;
        // There are no more elements
        if (elemMeter == 0) {
            for (int i = 0; i < logicalProcessList.length; i++)
                logicalProcessList[i].unlock();            
        }
    }
    
    public synchronized int getElements() {
        return elemMeter;
    }
    
    /**
     * Getter for property endTs.
     * @return Value of property endTs.
     */
    public double getEndTs() {
        return endTs;
    }

	/**
	 * @return Returns the startTs.
	 */
	public double getStartTs() {
		return startTs;
	}

	/** 
	 * Waits for the end of the simulation process.
	 */
	protected void waitEnd() {
		try {
		    for (int i = 0; i < logicalProcessList.length; i++)
				simLock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print(Output.MessageType.DEBUG, "SIMULATION COMPLETELY FINISHED");
	}
	
	/** 
	 * Notifies the end of a process 
	 */
	protected void notifyEnd() {
        simLock.unlock();
	}
	
	public String toString() {
		return description;
	}
	
	public void print(Output.MessageType type, String shortDescription, String longDescription) {
		out.print(type, shortDescription, longDescription);
	}

	public void print(Output.MessageType type, String description) {
		out.print(type, description);
	}

	public void addStatistic(StatisticData data) {
		results.add(data);
	}

	/**
	 * @return Returns the results.
	 */
	public SimulationResults getResults() {
		return results;
	}

	protected void debugPrintGraph(HashSet []graph) {
		StringBuffer str = new StringBuffer(); 
        // Pinto el graph para chequeo
        for (int i = 0; i < resourceTypeList.size(); i++) {
            ResourceType rt = resourceTypeList.get(i);
            str.append("Resource Type (" + i + "): " + rt.getDescription() + "\r\n");
            str.append("\tNeighbours: ");
            Iterator it = graph[i].iterator();
            while (it.hasNext()) {
                Integer nodo = (Integer)it.next();
                str.append(nodo + "\t");
            }
            str.append("\r\n");
        }
        print(Output.MessageType.DEBUG, "Graph created", str.toString());
	}
	
	protected void debugPrintActManager() {
		StringBuffer str = new StringBuffer("Activity Managers:");
        for (int i = 0; i < activityManagerList.size(); i++)
            str.append("\t" + activityManagerList.get(i));            
		StringBuffer str1 = new StringBuffer("Activity Managers:\r\n");
        for (int i = 0; i < activityManagerList.size(); i++)
            str1.append((activityManagerList.get(i)).getDescription() + "\r\n");            
		print(Output.MessageType.DEBUG, str.toString(), str1.toString());
	}
}
