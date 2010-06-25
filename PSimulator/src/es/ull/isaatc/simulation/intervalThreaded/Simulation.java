/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.intervalThreaded;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.intervalThreaded.flow.Flow;
import es.ull.isaatc.util.Output;

/**
 * Main simulation class. A simulation needs a model (introduced by means of the
 * <code>createModel()</code> method) to create several structures: activity
 * managers, logical processes...
 * <p>
 * A simulation use <b>InfoListeners</b> to show results. The "listeners" can
 * be added by invoking the <code>addListener()</code> method. When the
 * <code>endTs</code> is reached, it stores its state. This state can be
 * obtained by using the <code>getState</code> method.
 * 
 * @author Iván Castilla Rodríguez
 */
public class Simulation extends es.ull.isaatc.simulation.common.Simulation {
	
	/** List of resources present in the simulation. */
	protected final Map<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();

	/** List of element generators of the simulation. */
	protected final ArrayList<Generator> generatorList = new ArrayList<Generator>();

	/** List of activities present in the simulation. */
	protected final Map<Integer, Activity> activityList = new TreeMap<Integer, Activity>();

	/** List of resource types present in the simulation. */
	protected final Map<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();

	/** List of resource types present in the simulation. */
	protected final Map<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();

	/** List of activity managers that partition the simulation. */
	protected final ArrayList<ActivityManager> activityManagerList = new ArrayList<ActivityManager>();
	
	/** List of flows present in the simulation */
	protected final Map<Integer, Flow> flowList = new TreeMap<Integer, Flow>();

	/** List of active elements */
	private final Map<Integer, Element> activeElementList = Collections.synchronizedMap(new TreeMap<Integer, Element>());

	protected ActivityManagerCreator amCreator = null;

	/** Local virtual time. Represents the current simulation time for this LP. */
	protected volatile long lvt;
    /** A counter to know how many events are in execution */
    protected AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final AbstractQueue<BasicElement.DiscreteEvent> futureEventList;
    private EventExecutor [] executor;
    private int nextExecutor = 0; 
    private final boolean buffered;
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Timestamp of simulation's start
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public Simulation(int id, String description, boolean buffered, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		super(id, description, unit, startTs, endTs);
		if (buffered)
			futureEventList = new PriorityQueue<BasicElement.DiscreteEvent>();
		else
			futureEventList = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
        this.buffered = buffered;
	}
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param description
	 *            A short text describing this simulation.
	 * @param startTs
	 *            Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs
	 *            Simulation's end timestamp expresed in Simulation Time Units
	 */
	public Simulation(int id, String description, boolean buffered, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		if (buffered)
			futureEventList = new PriorityQueue<BasicElement.DiscreteEvent>();
		else
			futureEventList = new PriorityBlockingQueue<BasicElement.DiscreteEvent>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
        this.buffered = buffered;
	}
	
	/**
	 * Starts the simulation execution. It creates and starts all the necessary 
	 * structures. First, a <code>SafeLPElement</code> is added. The execution loop 
     * consists on waiting for the elements which are in execution, then the simulation clock is 
     * advanced and a new set of events is executed.<br> 
	 * Also checks if a valid output for debug messages has been declared. Note that no 
	 * debug messages can be printed before this method is declared unless <code>setOutput</code>
	 * had been invoked. 
	 */
	public void run() {
		if (out == null)
			out = new Output();
		
		debug("SIMULATION MODEL CREATED");
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();

        executor = new EventExecutor[nThreads];
        if (buffered) {
	        for (int i = 0; i < nThreads; i++) {
				executor[i] = new BufferedEventExecutor(i);
				executor[i].start();
			}
        }
        else {
	        for (int i = 0; i < nThreads; i++) {
				executor[i] = new EventExecutor(i);
				executor[i].start();
			}
        }        	
		
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.nanoTime(), this.internalStartTs));
		
		// Starts all the generators
		for (Generator gen : generatorList)
			addWait(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.getStartEvent(internalStartTs));		

		addWait(new SimulationElement().getStartEvent(internalEndTs));
        
        // Simulation main loop
		if (buffered) {
			while (!isSimulationEnd()) {
				// Every time the loop is entered we must wait for all the events from the 
				// previous iteration to be finished (the execution queue must be empty)
				while (executingEvents.get() > 0);
				// Now the simulation clock can advance
				updateFutureEventList();
	            execWaitingElements();	
			}
		}
		else {
			while (!isSimulationEnd()) {
				// Every time the loop is entered we must wait for all the events from the 
				// previous iteration to be finished (the execution queue must be empty)
				while (executingEvents.get() > 0);
				// Now the simulation clock can advance
	            execWaitingElements();
			}
		}
		// The simulation waits for all the events to be removed from the execution queue 
		while (executingEvents.get() > 0);
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
		end();
		
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationEndInfo(this, System.nanoTime(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

    /**
     * Indicates if the simulation end has been reached.
     * @return True if the maximum simulation time has been reached. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= internalEndTs);
    }

	public long getTs() {
		return lvt;
	}
	
    /**
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     */
	public void addExecution(BasicElement.DiscreteEvent e) {
		executingEvents.incrementAndGet();
	}

    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     */
    protected void removeExecution(BasicElement.DiscreteEvent e) {
    	executingEvents.decrementAndGet();
    }
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public void addWait(BasicElement.DiscreteEvent e) {
		futureEventList.add(e);
	}

    /**
     * Removes an event from the waiting queue. An event is removed from the waiting 
     * queue when the LP reaches the timestamp of that event.
     * @return The first event of the waiting queue.
     */
    protected BasicElement.DiscreteEvent removeWait() {
        return futureEventList.poll();
    }
    
    /**
     * Updates the future event list with the events produced by the executor threads
     */
    private void updateFutureEventList() {
    	for (EventExecutor ee : executor) {
    		ArrayDeque<BasicElement.DiscreteEvent> list = ((BufferedEventExecutor)ee).getWaitingEvents();
    		while (!list.isEmpty()) {
    			BasicElement.DiscreteEvent e = list.pop();
    			addWait(e);
    		}    		
    	}
    	
    }
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    private void execWaitingElements() {
        // Extracts the first event
        BasicElement.DiscreteEvent e = removeWait();
        // Advances the simulation clock
        beforeClockTick();
        lvt = e.getTs();
        infoHandler.notifyInfo(new TimeChangeInfo(this, lvt));
        afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
        // Events with timestamp greater or equal to the maximum simulation time aren't
        // executed
        if (lvt >= internalEndTs)
			addWait(e);
        else {
        	do {
	            addExecution(e);
	            while (!executor[nextExecutor].setEvent(e)) {
	                nextExecutor = (nextExecutor + 1) % executor.length;
	            }
	            nextExecutor = (nextExecutor + 1) % executor.length;
                e = removeWait();
        	} while (e.getTs() == lvt);
            addWait(e);
        }
    }

    
	/**
	 * A basic element which facilitates the end-of-simulation control. It simply
	 * has an event at <code>maxgvt</code>, so there's always at least one event in 
	 * the LP. 
	 * @author Iván Castilla Rodríguez
	 */
    class SimulationElement extends BasicElement {

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
	 * Does a debug print of this LP. Prints the current local time, the contents of
	 * the waiting and execution queues, and the contents of the activity managers. 
	 */
	protected void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(futureEventList.size() + " waiting elements: ");
	        for (BasicElement.DiscreteEvent e : futureEventList)
	            strLong.append(e + " ");
	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	protected class EventExecutor extends Thread {
		ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		AtomicReference<BasicElement.DiscreteEvent> event = new AtomicReference<BasicElement.DiscreteEvent>();
		
		private EventExecutor(int id) {
			super("LPExec-" + id);			
		}
		
		/**
		 * @param event the event to set
		 */
		public void addEvent(BasicElement.DiscreteEvent event) {
	    	final long evTs = event.getTs();
	    	final long lpTs = lvt;
	        if (evTs == lpTs) {
	            addExecution(event);
				extraEvents.push(event);
	        }
	        else if (evTs > lpTs)
	    		addWaitingEvent(event);
	        else
	        	error("Causal restriction broken\t" + lpTs + "\t" + event);
			
		}

		/**
		 * @param event the event to set
		 */
		public void addWaitingEvent(BasicElement.DiscreteEvent event) {
			addWait(event);
		}
		
		/**
		 * @param event the event to set
		 */
		public boolean setEvent(BasicElement.DiscreteEvent event) {
			return this.event.compareAndSet(null, event);
		}

		@Override
		public void run() {
			while (lvt < internalEndTs) {
				if (event.get() != null) {
					event.get().run();
					while (!extraEvents.isEmpty()) {
						extraEvents.pop().run();
					}
					event.set(null);
				}
			}
			
		}
	}
	
	final class BufferedEventExecutor extends EventExecutor {
		private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		
		public BufferedEventExecutor(int i) {
			super(i);
		}

		/**
		 * @param event the event to set
		 */
		public void addWaitingEvent(BasicElement.DiscreteEvent event) {
			extraWaitingEvents.push(event);
		}

		public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
	}
	

	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.intervalBufferThreaded.Activity} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param act
	 *            Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected Activity add(Activity act) {
		return activityList.put(act.getIdentifier(), act);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.intervalBufferThreaded.ElementType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param et
	 *            Element Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ElementType add(ElementType et) {
		return elementTypeList.put(et.getIdentifier(), et);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.intervalBufferThreaded.ResourceType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param rt
	 *            Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceType add(ResourceType rt) {
		return resourceTypeList.put(rt.getIdentifier(), rt);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.intervalBufferThreaded.flow.Flow} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param f
	 *            Flow that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public Flow add(Flow f) {
		return flowList.put(f.getIdentifier(), f);
		
	}
	
	/**
	 * Adds an activity manager to the simulation. The activity managers are
	 * automatically added from their constructor.
	 * 
	 * @param am
	 *            Activity manager.
	 */
	protected void add(ActivityManager am) {
		activityManagerList.add(am);
	}

	/**
	 * Adds a generator to the simulation. The generators are automatically
	 * added from their constructor.
	 * 
	 * @param gen
	 *            Generator.
	 */
	protected void add(Generator gen) {
		generatorList.add(gen);
	}

	/**
	 * Adds a resoruce to the simulation. The resources are automatically added
	 * from their constructor.
	 * 
	 * @param res
	 *            Resource.
	 */
	protected void add(Resource res) {
		resourceList.put(res.getIdentifier(), res);
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
	 * 
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
	 * 
	 * @param elem
	 *            An element that starts its execution.
	 */
	public void addActiveElement(Element elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * 
	 * @param elem
	 *            An element that finishes its execution.
	 */
	public void removeActiveElement(Element elem) {
		activeElementList.remove(elem.getIdentifier());
	}

	/**
	 * Returns the element with the specified identifier.
	 * 
	 * @param id
	 *            The element's identifier.
	 * @return The element with the specified identifier.
	 */
	public Element getActiveElement(int id) {
		return activeElementList.get(id);
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

	public Map<Integer, Element> getActiveElementList() {
		return activeElementList;
	};
	
}
