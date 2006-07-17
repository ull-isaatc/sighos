/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.*;

import es.ull.isaatc.simulation.info.InfoListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.sync.Lock;
import es.ull.isaatc.util.*;

/**
 * Main simulation class. It creates all the structures needed to carry out a
 * simulation: activity managers, logical processes...<p>
 * It implements the Runnable interface in order to allow both a sequencial 
 * (by invoking the <code>run</code> method) and a threaded execution (<code>start</code>).
 * It generates a set of simulation results that can be obtained by using the 
 * <code>getResults</code> method.  
 * @author Iván Castilla Rodríguez
 */
public abstract class Simulation implements Printable, RecoverableState<SimulationState> {
    /** A brief description of the model. */
    String description;
	/** List of resources present in the simulation. */
	protected OrderedList<Resource> resourceList;
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
    /** Output for printing messages */
    protected Output out;
    /** End-of-simulation control */
    private Lock simLock;
	/** Default element type for non presential elements */
	private ElementType npElementType;
	/** List of info listeners */
	private ArrayList<InfoListener> listeners;
	/** List of active elements */
	private OrderedList<Element> activeElementList;
    
    /** Creates a new instance of Simulation */
    public Simulation(String description, double startTs, double endTs, Output out) {
        activityList = new OrderedList<Activity>();
        resourceTypeList = new OrderedList<ResourceType>();
        elementTypeList = new OrderedList<ElementType>();
        activityManagerList = new ArrayList<ActivityManager>();
        resourceList = new OrderedList<Resource>();
        generatorList = new ArrayList<Generator>();
        
    	this.description = description;
        this.startTs = startTs;
        this.endTs = endTs;
        this.out = out;
        npElementType = new ElementType(-1, this, "Non presential element type");
        simLock = new Lock();
        // MOD 29/06/06
        listeners = new ArrayList<InfoListener>();
        activeElementList = new OrderedList<Element>();
    }
    
    /** Creates a new instance of Simulation */
    public Simulation(String description, double startTs, double endTs) {
    	this(description, startTs, endTs, new Output());
    }
    
    /**
     * Simulation initialization. It creates and starts all the necessary structures.
     */
    protected void init(SimulationState state) {
        createModel();
        print(Output.MessageType.DEBUG, "SIMULATION MODEL CREATED");
        if (state == null) {
	        createActivityManagers();
	        createSimulation();
        }
        else {
        	setState(state);
            // Elements from a previous simulation don't need to be started, but they need a default LP
        	for (Element elem : activeElementList)
        		if (elem.getDefLP() == null)
        			elem.setDefLP(getDefaultLogicalProcess());
        }
        notifyListeners(new SimulationStartInfo(this, System.currentTimeMillis(), Generator.getElemCounter()));
        // FIXME: Debería hacer un reparto más inteligente tanto de generadores como de recursos
//        createGenerators();
        // Starts all the generators
        for (Generator gen : generatorList)
        	gen.start(getDefaultLogicalProcess());
//        createResources();
        // Starts all the resources
        for (Resource res : resourceList)
            res.start(getDefaultLogicalProcess());
    }
    
    // Listener adapter
    public void addListener(InfoListener listener) {
    	listeners.add(listener);
    }
    
    public synchronized void notifyListeners(SimulationInfo info) {
    	for (InfoListener il : listeners)
    		il.infoEmited(info);
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
        for (int i = 0; i < nManagers; i++)
            new ActivityManager(this);
        for (Activity a : activityList) {
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
                nManagers++;
                a.setManager(ga);
            }
        }
        for (int i = 0; i < resourceTypeList.size(); i++)
            resourceTypeList.get(i).setManager(activityManagerList.get(marks[i]));

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
	public void start(SimulationState state) {
		init(state);
        for (int i = 0; i < logicalProcessList.length; i++)
            logicalProcessList[i].start();
        waitEnd();
        notifyListeners(new SimulationEndInfo(this, System.currentTimeMillis(), Generator.getElemCounter()));
    }
	
	public void start() {
		start(null);
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

    protected void add(ActivityManager am) {
    	activityManagerList.add(am);
    }
    
    protected void add(Generator gen) {
    	generatorList.add(gen);
    }
    
    protected void add(Resource res) {
    	resourceList.add(res);
    }
    
	/**
     * Returns a list of the resources of the model.
     * @return Resources of the model.
     */ 
	public OrderedList<Resource> getResourceList() {
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
    
    public synchronized void addActiveElement(Element elem) {
    	activeElementList.add(elem);
    }
    
    public synchronized void removeActiveElement(Element elem) {
    	activeElementList.remove(elem);
    }
    
    public Element getActiveElement(int id) {
    	return activeElementList.get(new Integer(id));
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

	public SimulationState getState() {
		SimulationState simState = new SimulationState(Generator.getElemCounter(), NPElement.getCounter(), SingleFlow.getCounter(), endTs);
		for(LogicalProcess lp : logicalProcessList)
			simState.add(lp.getState());
		for(Element elem : activeElementList)
			simState.add(elem.getState());
		for (Resource res : resourceList)
			simState.add(res.getState());
		return simState;
	}
    
    /**
     * Fills up the simulation with data from a previous simulation. The model is supposed to be
     * previously created.
     * @param state Previous simulation data
     */
	public void setState(SimulationState state) {
		// FIXME: ¿Debería hacer startTs = state.getEndTs()?
		// Elements. Inverted order to ensure that presential elements are created before the
		// non-presential ones.
		for (int i = state.getElemStates().size() - 1; i >= 0; i--) {
			ElementState eState = state.getElemStates().get(i);
			Element elem = null;
			if (eState instanceof NPElementState)
				elem = new NPElement(eState.getElemId(), activeElementList.get(new Integer(((NPElementState)eState).getParentElemId())));
			else
				elem = new Element(eState.getElemId(), this, elementTypeList.get(new Integer(eState.getElemTypeId())));
    		elem.setState(eState);
			activeElementList.add(elem);
		}
//		for (ElementState eState : state.getElemStates()) {
//    		Element elem = new Element(eState.getElemId(), this, elementTypeList.get(new Integer(eState.getElemTypeId())));
//    		elem.setState(eState);
//			activeElementList.add(elem);
//		}
		//NPElements' counter 
		NPElement.setCounter(state.getLastNPElemId());
		// Single flow's counter. This value is established here because the set of the state 
		// of the elements modifies its value. 
		SingleFlow.setCounter(state.getLastSFId());
		// Resources
		for (ResourceState rState : state.getResStates())
			resourceList.get(new Integer(rState.getResId())).setState(rState);
		// Rest of components
		ArrayList<LogicalProcessState> lpStates = state.getLpStates();
        logicalProcessList = new LogicalProcess[lpStates.size()];
		for (int i = 0; i < lpStates.size(); i++) {
	        logicalProcessList[i] = new LogicalProcess(this, startTs, endTs);
	        logicalProcessList[i].setState(lpStates.get(i));
		}
		// Element's counter of the generators
		Generator.setElemCounter(state.getLastElemId());
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
