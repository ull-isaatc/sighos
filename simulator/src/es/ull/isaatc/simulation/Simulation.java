/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation;

import java.util.*;

import mjr.heap.Heapable;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.sync.Lock;
import es.ull.isaatc.util.*;

/**
 * Main simulation class. A simulation needs a model (introduced by means of the
 * <code>createModel()</code> method) to create several structures: activity managers, 
 * logical processes...<p>
 * A simulation use <b>InfoListeners</b> to show results. The "listeners" can be added
 * by invoking the <code>addListener()</code> method.
 * When the <code>endTs</code> is reached, it stores its state. This state can be 
 * obtained by using the <code>getState</code> method.  
 * @author Iván Castilla Rodríguez
 */
public abstract class Simulation implements Printable,
	RecoverableState<SimulationState> {
    /** A short text describing this simulation. */
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
    /** List of info listeners */
    private ArrayList<SimulationListener> listeners;
    /** List of active elements */
    private OrderedList<Element> activeElementList;
    
    /** 
     * Creates a new instance of Simulation 
     * @param description A short text describing this simulation.
     * @param startTs Timestamp of simulation's start.
     * @param endTs Timestamp of Simulation's end.
     * @param out Output for printing debug messages.
     */
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
		simLock = new Lock();
		// MOD 29/06/06
		listeners = new ArrayList<SimulationListener>();
		activeElementList = new OrderedList<Element>();
    }

    /** 
     * Creates a new instance of Simulation 
     * @param description A short text describing this simulation.
     * @param startTs Timestamp of simulation's start.
     * @param endTs Timestamp of Simulation's end.
     */
    public Simulation(String description, double startTs, double endTs) {
    	this(description, startTs, endTs, new Output());
    }

    /**
     * Simulation initialization. It creates and starts all the necessary structures.<p>
     * If a state is indicated, sets the state of this simulation.
     * @param state A previous stored state. <code>null</code> if no previous state is
     * going to be used. 
     */
    protected void init(SimulationState state) {
		createModel();
		print(Output.MessageType.DEBUG, "SIMULATION MODEL CREATED");
	    createActivityManagers();
	    createSimulation();
		if (state != null) {
		    setState(state);
		    // Elements from a previous simulation don't need to be started, but they need a default LP
		    for (Element elem : activeElementList)
		    	if (elem.getDefLP() == null)
		    		elem.setDefLP(getDefaultLogicalProcess());
		}
		notifyListeners(new SimulationStartInfo(this, System.currentTimeMillis(), Generator.getElemCounter()));
		// FIXME: Debería hacer un reparto más inteligente tanto de generadores como de recursos
		// Starts all the generators
		for (Generator gen : generatorList)
		    gen.start(getDefaultLogicalProcess());
		// Starts all the resources
		for (Resource res : resourceList)
		    res.start(getDefaultLogicalProcess());
    }

    /**
     * Listener adapter. Adds a new listener to the listener list.
     * @param listener A simulation's listener
     */
    public void addListener(SimulationListener listener) {
    	listeners.add(listener);
    }

    /**
     * Informs the simulation's listeners of a new event. 
     * @param info An event that contains simulation information.
     */
    public synchronized void notifyListeners(SimulationObjectInfo info) {
    	for (SimulationListener il : listeners)
    		il.infoEmited(info);
    }

    /**
     * Informs the simulation's listeners of a new event. 
     * @param info An event that contains simulation information.
     */
    public synchronized void notifyListeners(SimulationStartInfo info) {
    	for (SimulationListener il : listeners)
    		il.infoEmited(info);
    }

    /**
     * Informs the simulation's listeners of a new event. 
     * @param info An event that contains simulation information.
     */
    public synchronized void notifyListeners(SimulationEndInfo info) {
    	for (SimulationListener il : listeners)
    		il.infoEmited(info);
    }

    /**
     * Informs the simulation's listeners of a new event. 
     * @param info An event that contains simulation information.
     */
    public synchronized void notifyListeners(TimeChangeInfo info) {
    	for (SimulationListener il : listeners)
    		il.infoEmited(info);
    }

    /**
     * Contains the specifications of the model. All the components of the
     * model must be declared here.
     * <p>
     * The components are added simply by invoking their constructors. For
     * example:
     * <code>
     * Activity a1 = new Activity(0, this, "Act1");
     * ResourceType rt1 = new ResourceType(0, this, "RT1");
     * </code>
     */
    protected abstract void createModel();

    /**
     * Makes a depth first search on a graph.
     * @param graph Graph to be searched.
     * @param current Current node being searching.
     * @param marks Mark array that's used for determining the partition of each node.
     */
    private void dfs(HashSet<Integer>[] graph, int current, int[] marks) {
    	for (Integer i : graph[current]) {
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
    private HashSet<Integer>[] createGraph() {
		int ind1 = -1, ind2 = -1;
		HashSet<Integer>[] graph = new HashSet[resourceTypeList.size()];
	
		for (int i = 0; i < resourceTypeList.size(); i++)
		    graph[i] = new HashSet<Integer>();
		for (Activity a : activityList) {
		    Iterator<WorkGroup> iter = a.iterator();
		    // Looks for the first RTT that contains at least one resource type        	
		    int firstWG = 1;
		    while (iter.hasNext()) {
				WorkGroup wg = iter.next();
				if (wg.size() > 0) {
				    if (firstWG == 1)
				    	ind1 = resourceTypeList.indexOf(wg.getResourceType(0));
				    for (; firstWG < wg.size(); firstWG++) {
				    	ind2 = resourceTypeList.indexOf(wg.getResourceType(firstWG));
				    	graph[ind1].add(new Integer(ind2));
				    	graph[ind2].add(new Integer(ind1));
				    	ind1 = ind2;
				    }
				    firstWG = 0;
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
		HashSet<Integer>[] graph = createGraph();
		int[] marks = new int[resourceTypeList.size()];
		for (int i = 0; i < resourceTypeList.size(); i++)
		    marks[i] = -1; // Not-visited mark
	
		// Now the DFS
		int nManagers = 0; // This counter lets us mark each partition
		for (int i = 0; i < resourceTypeList.size(); i++)
		    if (marks[i] == -1) {
				marks[i] = nManagers;
				dfs(graph, i, marks);
				nManagers++;
		    }
		// The activity managers are created
		for (int i = 0; i < nManagers; i++)
		    new ActivityManager(this);
		// The activities are associated to the activity managers
		for (Activity a : activityList) {
		    Iterator<WorkGroup> iter = a.iterator();
		    // This step is for non-resource-types activities
		    boolean found = false;
		    while (iter.hasNext() && !found) {
				WorkGroup wg = iter.next();
				if (wg.size() > 0) {
				    int ind = resourceTypeList.indexOf(wg.getResourceType(0));
				    a.setManager(activityManagerList.get(marks[ind]));
				    found = true;
				}
		    }
		    if (!found) {
				nManagers++;
				a.setManager(new ActivityManager(this));
		    }
		}
		for (int i = 0; i < resourceTypeList.size(); i++)
		    resourceTypeList.get(i).setManager(activityManagerList.get(marks[i]));
	
		debugPrintActManager();
    }

    /**
     * Creates the logical process needed for carrying out a simulation.
     */
    private void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[activityManagerList.size() + 1];
		for (int i = 0; i < activityManagerList.size(); i++)
		    logicalProcessList[i] = new LogicalProcess(this, startTs, endTs);
		// Creo el último proceso lógico, que servirá de "cajón de sastre"
		logicalProcessList[activityManagerList.size()] = new LogicalProcess(this, startTs, endTs);
    }

    /**
     * Creates the estructures needed to carry out a simulation. These estructures are
     * the logical processes. The activity managers are also linked to the logical processes.
     */
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
     * Starts the simulation execution. Initializes all the structures, and
     * starts the logical processes. This method blocks until all the logical
     * processes have finished their execution.
     * @param state A previously stored state of the simulation. 
     */
    public void start(SimulationState state) {
		init(state);
		for (int i = 0; i < logicalProcessList.length; i++)
		    logicalProcessList[i].start();
		waitEnd();
		notifyListeners(new SimulationEndInfo(this, System.currentTimeMillis(),	Generator.getElemCounter()));
    }

    /**
     * Starts the simulation execution. Initializes all the structures, and
     * starts the logical processes. This method blocks until all the logical
     * processes have finished their execution.<p>
     * This method is invoked when there isn't a previous state to restore.
     */
    public void start() {
    	start(null);
    }

    /**
     * Adds an identified object to the model. The allowed id. objects are:
     * {@link Activity}, {@link ResourceType}, {@link ElementType}. Any other object 
     * is ignored. These objects are automatically added from their constructor.
     * @param obj Identified object that's added to the model.
     * @return True is the insertion was succesful. False if there already was  
     * an object with the same description in the list.
     */
    protected boolean add(DescSimulationObject obj) {
		boolean resul = false;
		if (obj instanceof ResourceType)
		    resul = resourceTypeList.add((ResourceType) obj);
		else if (obj instanceof Activity)
		    resul = activityList.add((Activity) obj);
		else if (obj instanceof ElementType)
		    resul = elementTypeList.add((ElementType) obj);
		else
		    print(Output.MessageType.ERROR, "Trying to add an unidentified object to the Model");
		return resul;
    }

    /**
     * Adds an activity manager to the simulation. The activity managers are automatically
     * added from their constructor. 
     * @param am Activity manager.
     */
    protected void add(ActivityManager am) {
    	activityManagerList.add(am);
    }

    /**
     * Adds a generator to the simulation. The generators are automatically added from 
     * their constructor. 
     * @param gen Generator.
     */
    protected void add(Generator gen) {
    	generatorList.add(gen);
    }

    /**
     * Adds a resoruce to the simulation. The resources are automatically added from 
     * their constructor. 
     * @param res Resource.
     */
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
     * Returns a list of the element types of the model.
     * @return element types of the model.
     */
    public OrderedList<ElementType> getElementTypeList() {
    	return elementTypeList;
    }

    /**
     * Returns a list of the activity managers of the model.
     * @return Work activity managers of the model.
     */
    public ArrayList<ActivityManager> getActivityManagerList() {
    	return activityManagerList;
    }

    /**
     * Returns the logical process at the specified position.
     * @param ind The position of the logical process
     * @return The logical process at the specified position.
     */
    public LogicalProcess getLogicalProcess(int ind) {
    	return logicalProcessList[ind];
    }

    /**
     * Number of Logical processes that this simulation contains.
     * @return The size of the LPs list.
     */
    public int getLPSize() {
    	return logicalProcessList.length;
    }

    /**
     * Returns the logical process that can be used as a default LP.<p> 
     * The default LP is useful for any simulation object which don't have a 
     * direct relation to an activity or resource type.
     * @return This simulation's default logical process.
     */
    public LogicalProcess getDefaultLogicalProcess() {
    	return logicalProcessList[logicalProcessList.length - 1];
    }

    /**
     * Adds an element when it starts its execution.
     * @param elem An element that starts its execution.
     */
    public synchronized void addActiveElement(Element elem) {
    	activeElementList.add(elem);
    }

    /**
     * Removes an element when it finishes its execution.
     * @param elem An element that finishes its execution.
     */
    public synchronized void removeActiveElement(Element elem) {
    	activeElementList.remove(elem);
    }

    /**
     * Returns the element with the specified identifier.
     * @param id The element's identifier.
     * @return The element with the specified identifier.
     */
    public Element getActiveElement(int id) {
    	return activeElementList.get(new Integer(id));
    }

    /**
     * Returns the simulation end timestamp.
     * @return Value of property endTs.
     */
    public double getEndTs() {
    	return endTs;
    }

    /**
     * Returns the simulation start timestamp.
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
     * Notifies the end of a logical process. 
     */
    protected void notifyEnd() {
    	simLock.unlock();
    }

    @Override
    public String toString() {
    	return description;
    }

    /**
     * Prints a debug/error message.
     * @param type Message type: Debug/Error
     * @param shortDescription A short message
     * @param longDescription An extended message
     */
    public void print(Output.MessageType type, String shortDescription, String longDescription) {
    	out.print(type, shortDescription, longDescription);
    }

    /**
     * Prints a debug/error message. Uses the same text for short and long description.
     * @param type Message type: Debug/Error
     * @param Description The content of the message
     */
    public void print(Output.MessageType type, String description) {
    	out.print(type, description);
    }

    /**
     * Returns the state of this simulation. The state of a simulation consists on the state
     * of its logical processes, elements and resources.
     * @return The state of this simulation.
     */
    public SimulationState getState() {
    	SimulationState simState = new SimulationState(Generator.getElemCounter(), SingleFlow.getCounter(), endTs);
    	for (LogicalProcess lp : logicalProcessList) {
    		Iterator<Heapable> it = lp.waitQueue.iterator();
    		while(it.hasNext()) {
    			BasicElement.DiscreteEvent ev = (BasicElement.DiscreteEvent)it.next();
    			if (ev instanceof Element.FinalizeActivityEvent) {
    				Element.FinalizeActivityEvent fev = (Element.FinalizeActivityEvent)ev;
    				simState.add(SimulationState.EventType.FINALIZEACT, fev.getElement().getIdentifier(), fev.getTs(), fev.getFlow().getIdentifier());
    			}
    			else if (ev instanceof Resource.RoleOffEvent) {
    				Resource.RoleOffEvent rev = (Resource.RoleOffEvent)ev;
    				simState.add(SimulationState.EventType.ROLOFF, rev.getElement().getIdentifier(), rev.getTs(), rev.getRole().getIdentifier());
    			}				
    		}			
    	}
        for (Activity act : activityList)
        	simState.add(act.getState());
        for (ResourceType rt : resourceTypeList)
        	simState.add(rt.getState());	
		for (Element elem : activeElementList)
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
		// Elements. 
		for (ElementState eState : state.getElemStates()) {
		    Element elem = new Element(eState.getElemId(), this,
			    elementTypeList.get(new Integer(eState.getElemTypeId())));
		    elem.setState(eState);
		    activeElementList.add(elem);
		}
		// Single flow's counter. This value is established here because the element's state set 
		// modifies its value. 
		SingleFlow.setCounter(state.getLastSFId());
		// Resources
		for (ResourceState rState : state.getResStates())
		    resourceList.get(new Integer(rState.getResId())).setState(rState);

		// Activities	
		for (ActivityState aState : state.getAStates()) {
			Activity act = getActivity(aState.getActId());
			act.setState(aState);			
		}
		
		// Resource Types
		for (ResourceTypeState rtState : state.getRtStates()) {
			ResourceType rt = getResourceType(rtState.getRtId());
			rt.setState(rtState);
		}
		
		// Creates a null cycle to non-iterative cycles
		Cycle c = new Cycle(0.0, new Fixed(1), -1.0);
		// Events
		for (SimulationState.EventEntry entry : state.getWaitQueue()) {
			if (entry.getType() == SimulationState.EventType.FINALIZEACT) {
				Element elem = getActiveElement(entry.getId());
				// The element has not been started yet, so it hasn't got a default logical process...
				elem.setDefLP(getDefaultLogicalProcess());
				// ... however, the event starts immediately
				elem.addEvent(elem.new FinalizeActivityEvent(entry.getTs(), elem.searchSingleFlow(entry.getValue())));
			}
			else if (entry.getType() == SimulationState.EventType.ROLOFF) {
				Resource res = resourceList.get(new Integer(entry.getId()));				
				// The resource has not been started yet, so it hasn't got a default logical process
				res.setDefLP(getDefaultLogicalProcess());
				// ... however, the event starts immediately
				res.addEvent(res.new RoleOffEvent(entry.getTs(), getResourceType(entry.getValue()), c.iterator(0.0, 0.0), 0.0));
			}
		}
	
		// Element's counter of the generators
		Generator.setElemCounter(state.getLastElemId());
    }

    /**
     * Prints a graph, where the resource types are nodes and the activities are
     * the links.
     * @param graph The graph to print.
     */
    protected void debugPrintGraph(HashSet[] graph) {
	StringBuffer str = new StringBuffer();
	// Pinto el graph para chequeo
	for (int i = 0; i < resourceTypeList.size(); i++) {
	    ResourceType rt = resourceTypeList.get(i);
	    str.append("Resource Type (" + i + "): " + rt.getDescription()
		    + "\r\n");
	    str.append("\tNeighbours: ");
	    Iterator it = graph[i].iterator();
	    while (it.hasNext()) {
		Integer nodo = (Integer) it.next();
		str.append(nodo + "\t");
	    }
	    str.append("\r\n");
	}
	print(Output.MessageType.DEBUG, "Graph created", str.toString());
    }

    /**
     * Prints the contents of the activity managers created.
     */
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
