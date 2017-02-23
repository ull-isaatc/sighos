package es.ull.iis.simulation.parallel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.bonn.cs.net.jbarrier.barrier.AbstractBarrier;
import edu.bonn.cs.net.jbarrier.barrier.TournamentBarrier;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.parallel.flow.Flow;
import es.ull.iis.util.Output;

/**
 * Main parallel discrete event simulation class. A simulation uses all kind of 
 * {@link SimulationObject Simulation Objects} to define a model which will be executed.<p>
 * Two important simulation objects are {@link Activity activities} and {@link ResourceType 
 * resource types}. Both are grouped in different {@link ActivityManager activity managers}, 
 * which serve as an initial partition for parallelism.<p>
 * The simulation is feed with {@link EventSourceEngine.DiscreteEvent discrete events} produced by 
 * {@link EventSourceEngine Basic elements}.
 * @author Iván Castilla Rodríguez
 */
public class Simulation extends es.ull.iis.simulation.model.SimulationEngine {
	/** The identifier to be assigned to the next resource */ 
	protected int nextResourceId = 0;
	/** The identifier to be assigned to the next activity */ 
	protected int nextActivityId = 0;
	/** The identifier to be assigned to the next resource type */ 
	protected int nextResourceTypeId = 0;
	/** The identifier to be assigned to the next element type */ 
	protected int nextElementTypeId = 0;
	/** The identifier to be assigned to the next flow */ 
	protected AtomicInteger nextFlowId = new AtomicInteger(0);
	/** The identifier to be assigned to the next element */ 
	protected AtomicInteger nextElementId = new AtomicInteger(0);

	/** List of resources present in the simulation. */
	protected final TreeMap<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();
	/** List of element generators of the simulation. */
	protected final ArrayList<Generator> generatorList = new ArrayList<Generator>();
	/** List of activities present in the simulation. */
	protected final TreeMap<Integer, Activity> activityList = new TreeMap<Integer, Activity>();
	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();
	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();
	/** List of activity managers that partition the simulation. */
	protected final ArrayList<ActivityManager> activityManagerList = new ArrayList<ActivityManager>();	
	/** List of flows present in the simulation */
	protected final TreeMap<Integer, Flow> flowList = new TreeMap<Integer, Flow>();
	/** List of active elements */
	private final Map<Integer, Element> activeElementList = Collections.synchronizedMap(new TreeMap<Integer, Element>());
	/** A definition of how to create the AMs */
	private ActivityManagerCreator amCreator = null;
	/** Local virtual time. Represents the current simulation time */
	private volatile long lvt;
    /** A counter to know how many events are in execution */
    private AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. Events are grouped according 
	 * to their timestamps. */
	private final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> futureEventList  = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
	private ArrayList<BasicElement.DiscreteEvent> currentEvents;
	/** The slave event executors */
    private SlaveEventExecutor [] executor;
    /** The barrier to control the phases of simulation */
	private AbstractBarrier barrier;
	/**
	 * Creates a new Simulation which starts at <code>startTs</code> and finishes at <code>endTs</code>.
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation
	 * @param unit Time unit used to define the simulation time
	 * @param startTs Timestamp of simulation's start
	 * @param endTs Timestamp of simulation's end
	 */
	public Simulation(int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		super(id, description, unit, startTs, endTs);
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
	}
	
	/**
	 * Creates a new Simulation which starts at <code>startTs</code> and finishes at <code>endTs</code>.
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation
	 * @param unit Time unit used to define the simulation time
	 * @param startTs Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs Simulation's end timestamp expresed in Simulation Time Units
	 */
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
	}
	
	/**
	 * Starts the execution of the simulation. It creates and initializes all the necessary 
	 * structures.<p> The following checks and initializations are performed within this method:
	 * <ol>
	 * <li>If no customized {@link Output debug output} has been defined, the default one is 
	 * used.</li>
	 * <li>If no customized {@link ActivityManagerCreator AM creator} has been defined, the 
	 * {@link StandardActivityManagerCreator default one} is used.</li>
	 * <li>As many SlaveEventExecutor event executors as threads set by using 
	 * {@link #setNThreads(int)} are created.</li>
	 * <li>The AMs are equally distributed among the available executors</li> 
	 * <li>The user defined method {@link #init()} is invoked.</li>
	 * <li>{@link Resource Resources} and {@link Generator generators} are started.</li>
	 * <li>The main simulation loop is run</li>
	 * <li>The user defined method {@link #end()} is invoked.</li>
	 * </ol>
	 * The execution loop consists on waiting for the elements which are in execution, then the 
	 * waiting events are executed (@see #execWaitingElements()} 
     * advanced and a new set of events is executed.<br> 
	 * Also checks if a valid output for debug messages has been declared. Note that no 
	 * debug messages can be printed before this method is declared unless <code>setOutput</code>
	 * had been invoked. 
	 */
	public void run() {
		if (out == null)
			out = new Output();
		debug("SIMULATION MODEL CREATED");
		
		initializeEngine();
        // The user defined method for initialization is invoked
		init();

		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationStartInfo(this, System.nanoTime(), this.internalStartTs));
		
		// Starts all the generators
		for (Generator gen : generatorList)
			addWait(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.getStartEvent(internalStartTs));		

		// Adds the event to control end of simulation
		addWait(new SimulationElement().getStartEvent(internalEndTs));

		advanceSimulationClock();

        for (int i = 0; i < nThreads; i++) {
			executor[i].start();
		}

		try {
			for (int i = 0; i < nThreads; i++) {
				executor[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Simulation has finished
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
        // The user defined method for finalization is invoked
		end();
		
		infoHandler.notifyInfo(new es.ull.iis.simulation.info.SimulationEndInfo(this, System.nanoTime(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

    /**
     * Indicates if the simulation clock has reached the simulation end.
     * @return True if the simulation clock is higher or equal to the simulation end. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= internalEndTs);
    }

    /**
     * Returns the current simulation time
     * @return The current simulation time
     */
	public long getTs() {
		return lvt;
	}
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is higher than the simulation time.
     * @param e Event to be added
     */
	public void addWait(BasicElement.DiscreteEvent e) {
		ArrayList<BasicElement.DiscreteEvent> list = futureEventList.get(e.getTs());
		if (list == null) {
			list = new ArrayList<BasicElement.DiscreteEvent>();
			list.add(e);
			futureEventList.put(e.getTs(), list);
		}
		else
			list.add(e);
	}

    /**
     * Removes a specific event from the waiting queue. This function can be used to cancel an event
     * FIXME: Not tested!!!
     * @param e Event to be removed
     * @return True if the queue contained the event; false otherwise
     */
    protected boolean removeWait(BasicElement.DiscreteEvent e) {
        return (futureEventList.remove(e) != null);
    }
    
	/**
	 * @return the nextResourceId
	 */
	protected int getNextResourceId() {
		return nextResourceId++;
	}


	/**
	 * @return the nextActivityId
	 */
	protected int getNextActivityId() {
		return nextActivityId++;
	}


	/**
	 * @return the nextResourceTypeId
	 */
	protected int getNextResourceTypeId() {
		return nextResourceTypeId++;
	}


	/**
	 * @return the nextElementTypeId
	 */
	protected int getNextElementTypeId() {
		return nextElementTypeId++;
	}


	/**
	 * @return the nextFlowId
	 */
	public int getNextFlowId() {
		return nextFlowId.getAndIncrement();
	}


	/**
	 * @return the nextElementId
	 */
	protected int getNextElementId() {
		return nextElementId.getAndIncrement();
	}

	private class BarrierAction implements Runnable {
		@Override
		public void run() {
			// Updates the future event list with the events produced by the executor threads
	    	for (SlaveEventExecutor ee : executor) {
	    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
	    		while (!list.isEmpty()) {
	    			BasicElement.DiscreteEvent e = list.pop();
	    			addWait(e);
	    		}    		
	    	}
	    	advanceSimulationClock();
			
		}
	}
	
	private void advanceSimulationClock() {
        beforeClockTick();

        // Advances the simulation clock
        lvt = futureEventList.firstKey();
        infoHandler.notifyInfo(new TimeChangeInfo(this, lvt));
        afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);

        if (!isSimulationEnd()) {
        	currentEvents = futureEventList.pollFirstEntry().getValue();
        	// Distributes the events with the same timestamp among the executors
    		executingEvents.addAndGet(currentEvents.size());
        }
        
	}
	
	/**
	 * A basic element which facilitates the control of the end of the simulation. It simply
	 * schedules an event at <code>endTs</code>, so there's always at least one event in 
	 * the simulation. 
	 * @author Iván Castilla Rodríguez
	 */
    class SimulationElement extends BasicElement {

    	/**
    	 * Creates a very simple element to control the simulation end.
    	 */
		public SimulationElement() {
			super(0, Simulation.this);
		}

		@Override
		protected void init() {
		}
		
		@Override
		protected void end() {
		}
    }
    
	/**
	 * Prints the current state of the simulation for debug purposes. Prints the current local 
	 * time, the contents of the future event list and the execution queue. 
	 */
	protected void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(futureEventList.size() + " waiting elements: ");
	        for (ArrayList<BasicElement.DiscreteEvent> ad : futureEventList.values())
	        	for (BasicElement.DiscreteEvent e : ad)
	        		strLong.append(e + " ");
	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	/**
	 * An event executor used by the simulation. A simulation normally declares as many "slaves" as available
	 * cores in the computer.<p>
	 * Event's execution is divided into two phases. First, the events assigned by the main simulation thread  
	 * (by using {@link #notifyEvents(List)} are executed. If the execution of the events produces new events, 
	 * they are added to the local buffers of the executor. <p>Once the execution of all the current events has
	 * finished, the second phase is started, and the AM events are executed.  
	 * @author Iván Castilla Rodríguez
	 *
	 */
	final class SlaveEventExecutor extends Thread implements EventExecutor {
		private final int threadId;
		/** Execution local buffer */
		private final ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		/** Future event local buffer */
		private final ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		/** The list of AMs tackled by this executor */
		private final ArrayDeque<ActivityManager> amList = new ArrayDeque<ActivityManager>();
		
		/**
		 * Creates a new slave executor
		 * @param id Executor's identifier 
		 */
		public SlaveEventExecutor(int id) {
			super("LPExec-" + id);
			threadId = id;
		}

		/**
		 * Assigns an AM to this executor. The events of this AM are executed from this thread.
		 * @param am Activity Manager
		 */
		public void assignActivityManager(ActivityManager am) {
			amList.add(am);
		}
		
		@Override
		public void addEvent(BasicElement.DiscreteEvent event) {
	    	final long evTs = event.getTs();
	        if (evTs == lvt) {
				executingEvents.incrementAndGet();
				extraEvents.push(event);
	        }
	        else if (evTs > lvt)
				extraWaitingEvents.push(event);
	        else
	        	error("Causal restriction broken\t" + lvt + "\t" + event);
			
		}

		/**
		 * Returns the contents of the local future event buffer.
		 * @return The contents of the local future event buffer
		 */
		public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		@Override
		public void run() {
			while (lvt < internalEndTs) {
	    		// Executes its events
	    		final int totalEvents = currentEvents.size();
	    		int myEventsCount = 0;
	    		// if there aren't many events, the first executor deals with them all
	    		if (threadId == 0 && totalEvents < nThreads) {
	    			for (int i = 0; i < totalEvents; i++)
	    				currentEvents.get(i).run();
	    			myEventsCount = totalEvents;
	    		}
	    		else if (totalEvents >= nThreads) {
	    			final int lastEvent = (threadId + 1 == nThreads) ? totalEvents : (totalEvents * (threadId + 1) / nThreads);
	    			for (int i = totalEvents * threadId / nThreads; i < lastEvent; i++)
	    				currentEvents.get(i).run();
	    			myEventsCount = lastEvent - (totalEvents * threadId / nThreads); 
	    		}
	    		
	    		// Executes its local events
				while (!extraEvents.isEmpty()) {
					extraEvents.pop().run();
			    	executingEvents.decrementAndGet();
				}
				// Now subtracts the original events, just to ensure that the barrier is not passed incorrectly 
				executingEvents.addAndGet(-myEventsCount);
				
				// Waits to carry out the second phase
				while (executingEvents.get() != 0);
				assert executingEvents.get() == 0 : "Executing " + executingEvents.get();
				for (ActivityManager am : amList)
					am.executeWork();
				if (nThreads > 1)
					barrier.await(threadId);
				else {
					// Updates the future event list with the events produced by the executor threads
			    	for (SlaveEventExecutor ee : executor) {
			    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
			    		while (!list.isEmpty()) {
			    			BasicElement.DiscreteEvent e = list.pop();
			    			addWait(e);
			    		}    		
			    	}
			    	advanceSimulationClock();					
				}
			}
			
		}
	}
	
	/**
	 * Adds an {@link Activity} to the model. This method is invoked from the object's constructor.
	 * @param act Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected Activity add(Activity act) {
		return activityList.put(act.getIdentifier(), act);
	}
	
	/**
	 * Adds an {@link ElementType} to the model. This method is invoked from the object's constructor.
	 * @param et Element Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ElementType add(ElementType et) {
		return elementTypeList.put(et.getIdentifier(), et);
	}
	
	/**
	 * Adds an {@link ResourceType} to the model. This method is invoked from the object's constructor.
	 * @param rt Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceType add(ResourceType rt) {
		return resourceTypeList.put(rt.getIdentifier(), rt);
	}
	
	/**
	 * Adds an {@link Flow} to the model. This method is invoked from the object's constructor.
	 * @param f Flow that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public Flow add(Flow f) {
		return flowList.put(f.getIdentifier(), f);
		
	}
	
	/**
	 * Adds a {@link Resource} to the simulation. This method is invoked from the object's constructor.
	 * @param res Resource that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected Resource add(Resource res) {
		return resourceList.put(res.getIdentifier(), res);
	}
	
	/**
	 * Adds an {@link ActivityManager} to the simulation. The activity managers are
	 * automatically added from their constructor.
	 * @param am Activity manager.
	 */
	protected void add(ActivityManager am) {
		activityManagerList.add(am);
	}

	/**
	 * Adds a {@link Generator} to the simulation. The generators are automatically
	 * added from their constructor.
	 * @param gen Generator.
	 */
	protected void add(Generator gen) {
		generatorList.add(gen);
	}
	
	@Override
	public Map<Integer, Resource> getResourceList() {
		return resourceList;
	}

	@Override
	public Map<Integer, Activity> getActivityList() {
		return activityList;
	}

	@Override
	public Map<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}
	
	@Override
	public Map<Integer, ElementType> getElementTypeList() {
		return elementTypeList;
	}

	@Override
	public Map<Integer, Flow> getFlowList() {
		return flowList;
	}

	/**
	 * Returns a list of the activity managers of the model.
	 * @return Work activity managers of the model.
	 */
	public ArrayList<ActivityManager> getActivityManagerList() {
		return activityManagerList;
	}

	@Override
	public Activity getActivity(int id) {
		return activityList.get(id);
	}

	@Override
	public ResourceType getResourceType(int id) {
		return resourceTypeList.get(id);
	}

	@Override
	public Resource getResource(int id) {
		return resourceList.get(id);
	}

	@Override
	public ElementType getElementType(int id) {
		return elementTypeList.get(id);
	}

	@Override
	public Flow getFlow(int id) {
		return flowList.get(id);
	}

	/**
	 * Adds an element when it starts its execution.
	 * @param elem An element that starts its execution.
	 */
	public void addActiveElement(Element elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * @param elem An element that finishes its execution.
	 */
	public void removeActiveElement(Element elem) {
		activeElementList.remove(elem.getIdentifier());
	}

	/**
	 * Returns the element with the specified identifier.
	 * @param id The element's identifier.
	 * @return The element with the specified identifier.
	 */
	public Element getActiveElement(int id) {
		return activeElementList.get(id);
	}

	/**
	 * Returns the current list of active elements.
	 * @return The current list of active elements
	 */
	public Map<Integer, Element> getActiveElementList() {
		return activeElementList;
	}
	
	/**
	 * Prints the contents of the activity managers created.
	 */
	protected void debugPrintActManager() {
		if (isDebugEnabled()) {
			StringBuffer str1 = new StringBuffer("Activity Managers:\r\n");
			for (ActivityManager am : activityManagerList)
				str1.append(am.getDescription() + "\r\n");
			debug(str1.toString());
		}
	}

	@Override
	public void initializeEngine() {
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();		
		// Creates the event executors
        executor = new SlaveEventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++) {
			executor[i] = new SlaveEventExecutor(i);
        }
        if (nThreads > 1)
        	barrier = new TournamentBarrier(executor.length, new BarrierAction());
        
        // Distributes the AMs among the executors
        for (int i = 0; i < activityManagerList.size(); i++)
        	executor[i % nThreads].assignActivityManager(activityManagerList.get(i));

	}
}
