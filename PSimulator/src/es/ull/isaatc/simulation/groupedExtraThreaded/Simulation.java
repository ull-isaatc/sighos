/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.groupedExtraThreaded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.groupedExtraThreaded.flow.Flow;
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
 * <p>
 * Includes a fine grain work load distribution policy. If we have <code>nThreads</code> slave 
 * workers, <code>nBunches</code> sets of work will be created, taking 
 * <code>nBunches = nThreads * grain + rest;</code>. Lets consider N the total amount of
 * events to be executed in a specific timestamp, <code>(N / nBunches) * grain</code> events will be
 * assigned to each slave worker; the remaining events will be assigned to the master worker.
 * @author Iván Castilla Rodríguez
 */
public class Simulation extends es.ull.isaatc.simulation.common.Simulation {
	
	/** List of resources present in the simulation. */
	protected final Map<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();

	/** List of element generators of the simulation. */
	protected final List<Generator> generatorList = new ArrayList<Generator>();

	/** List of activities present in the simulation. */
	protected final Map<Integer, Activity> activityList = new TreeMap<Integer, Activity>();

	/** List of resource types present in the simulation. */
	protected final Map<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();

	/** List of resource types present in the simulation. */
	protected final Map<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();

	/** List of activity managers that partition the simulation. */
	protected final List<ActivityManager> activityManagerList = new ArrayList<ActivityManager>();
	
	/** List of flows present in the simulation */
	protected final Map<Integer, Flow> flowList = new TreeMap<Integer, Flow>();

	/** List of active elements */
	private final Map<Integer, Element> activeElementList = Collections.synchronizedMap(new TreeMap<Integer, Element>());

	/** The way the Activity Managers are created */
	protected ActivityManagerCreator amCreator = null;

	/** Local virtual time. Represents the current simulation time. */
	protected volatile long lvt;
    /** A counter to know how many events are in execution */
    protected AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> waitQueue;
	/** The pool of slave event executors */ 
    private SlaveEventExecutor [] executor;
    /** Represents the master executor, which runs events and the simulation clock */
    private MasterExecutor mainExecutor;
    /** The list of current executing events */ 
	private List<BasicElement.DiscreteEvent> currentEvents;
	// Fine grain adjust of the work load
	/** Work load factor applied to slave workers */ 
	public int grain = 1;
	/** Work load factor applied to the master worker */
	public int rest = 1;
	/** Amount of sets of events to be distributed among the workers */
	private int nBunches; 
	private final boolean pasive;
	
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
	public Simulation(int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		this(id, description, false, unit, startTs, endTs);
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
	public Simulation(int id, String description, TimeUnit unit, long startTs, long endTs) {
		this(id, description, false, unit, startTs, endTs);
	}
	
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
	public Simulation(int id, String description, boolean pasive, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		super(id, description, unit, startTs, endTs);
        waitQueue = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
        this.pasive = pasive;
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
	public Simulation(int id, String description, boolean pasive, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
        waitQueue = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
        this.pasive = pasive;
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

        executor = new SlaveEventExecutor[nThreads - 1];
        if (pasive) {
	        for (int i = 0; i < nThreads - 1; i++) {
				executor[i] = new PasiveSlaveEventExecutor(i);
			}
        }
        else {
            for (int i = 0; i < nThreads - 1; i++) {
    			executor[i] = new ActiveSlaveEventExecutor(i);
    		}        	
        }
		nBunches = (nThreads - 1) * grain + rest;
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.nanoTime(), this.internalStartTs));
		mainExecutor = new MasterExecutor();
		// Starts all the generators
		for (Generator gen : generatorList)
			addWait(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.getStartEvent(internalStartTs));		

		addWait(new SimulationElement().getStartEvent(internalEndTs));

    	advanceSimulationClock();
    	
		mainExecutor.start();
        for (int i = 0; i < nThreads - 1; i++)
			executor[i].start();
        
        try {
			mainExecutor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
		end();
		
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationEndInfo(this, System.nanoTime(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

	private void advanceSimulationClock() {
        beforeClockTick();
        lvt = waitQueue.firstKey();
        infoHandler.notifyInfo(new TimeChangeInfo(Simulation.this, lvt));
        afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
        if (lvt < internalEndTs) {
        	currentEvents = waitQueue.pollFirstEntry().getValue();
    		executingEvents.addAndGet(currentEvents.size());
        }			
	}
	
	final class MasterExecutor extends Thread implements EventExecutor {
		/** Execution local buffer */
		private final ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();

		@Override
		public void addEvent(BasicElement.DiscreteEvent event) {
	    	final long evTs = event.getTs();
	        if (evTs == lvt) {
	        	executingEvents.incrementAndGet();
				extraEvents.push(event);
	        }
	        else if (evTs > lvt) {
	           	addWait(event);
	        }
	        else
	        	error("Causal restriction broken\t" + lvt + "\t" + event);
		}

	    /**
	     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
	     * timestamp equal to the LP timestamp. 
	     */
		public void run() {
	        // Simulation main loop
			while (!isSimulationEnd()) {
	    		// Executes its events
	    		final int totalEvents = currentEvents.size();
	    		int myEventsCount = 0;
	    		// if there aren't many events, the master executor deals with them all
	    		if ((nThreads == 1) || (totalEvents < nThreads)) {
	    			for (int i = 0; i < totalEvents; i++)
	    				currentEvents.get(i).run();
	    			myEventsCount = totalEvents; 
	    		}
	    		else {
	    			int share = totalEvents / nBunches;
	    			for (int i = share * executor.length * grain; i < totalEvents; i++)
	    				currentEvents.get(i).run();
	    			myEventsCount = totalEvents - share * executor.length * grain; 
	    		}
	    		
	    		// Executes its local events
				while (!extraEvents.isEmpty()) {
					extraEvents.pop().run();
			    	executingEvents.decrementAndGet();
				}
				// Now subtracts the original events, just to ensure that the barrier is not passed incorrectly 
				executingEvents.addAndGet(-myEventsCount);

				// Waits for the slave executors to finish
				while (executingEvents.get() > 0);
				
		    	// Updates the future event list with the events produced by the executor threads
		    	for (SlaveEventExecutor ee : executor) {
		    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
		    		while (!list.isEmpty()) {
		    			BasicElement.DiscreteEvent e = list.pop();
		    			addWait(e);
		    		}    		
		    	}
		        // Advances the simulation clock
		    	advanceSimulationClock();

				// Notifies the slaves that the events are available
				for (SlaveEventExecutor ee : executor)
					ee.notifyEvents();
			}
	    }

	}
	
	private abstract class SlaveEventExecutor extends Thread implements EventExecutor {
		protected ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		protected final int threadId; 
		
		public SlaveEventExecutor(int index) {
			super("LPExec-" + index);
			threadId = index;
		}
		
		/**
		 * @param event the event to set
		 */
		public void addEvent(BasicElement.DiscreteEvent event) {
	    	final long evTs = event.getTs();
	        if (evTs == lvt) {
	        	executingEvents.incrementAndGet();
				extraEvents.push(event);
	        }
	        else if (evTs > lvt) {
				extraWaitingEvents.push(event);
	        }
	        else
	        	error("Causal restriction broken\t" + lvt + "\t" + event);
		}
		
		public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		@Override
		public void run() {
	        // Simulation main loop
			while (!isSimulationEnd()) {
	    		// Executes its events
	    		final int totalEvents = currentEvents.size();
	    		int myEventsCount = 0;
	    		// if there aren't many events, the master executor deals with them all
	    		if (totalEvents >= nThreads) {
	    			final int share = totalEvents / nBunches;
	    			final int lastEvent = share * (threadId + 1) * grain;
	    			for (int i = share * threadId * grain; i < lastEvent; i++)
	    				currentEvents.get(i).run();
	    			myEventsCount = share * grain; 
	    		}
	    		
	    		// Executes its local events
				while (!extraEvents.isEmpty()) {
					extraEvents.pop().run();
			    	executingEvents.decrementAndGet();
				}
				// Now subtracts the original events, just to ensure that the barrier is not passed incorrectly 
				executingEvents.addAndGet(-myEventsCount);
	    		
				// Every time the loop is entered we must wait for all the events from the 
				// previous iteration to be finished
				await();
			}
		}

		protected abstract void await();
		protected abstract void notifyEvents(); 
	}
	
	private class ActiveSlaveEventExecutor extends SlaveEventExecutor {
		private AtomicBoolean flag = new AtomicBoolean(false);

		public ActiveSlaveEventExecutor(int index) {
			super(index);
		}
		
		@Override
		public void notifyEvents() {
			flag.set(true);
		}

		@Override
		protected void await() {
			while (!flag.compareAndSet(true, false)) {
			}			
		}

	}

	private class PasiveSlaveEventExecutor extends SlaveEventExecutor {
		private Semaphore lock = new Semaphore(0);
		
		public PasiveSlaveEventExecutor(int index) {
			super(index);
		}
		
		@Override
		public void notifyEvents() {
			lock.release();
		}

		@Override
		protected void await() {
			try {
				lock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}

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

	public void addWait(BasicElement.DiscreteEvent event) {
		ArrayList<BasicElement.DiscreteEvent> list = waitQueue.get(event.getTs());
		if (list == null) {
			list = new ArrayList<BasicElement.DiscreteEvent>();
			list.add(event);
			waitQueue.put(event.getTs(), list);
		}
		else
			list.add(event);
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
	        strLong.append(waitQueue.size() + " waiting elements: ");
	        for (ArrayList<BasicElement.DiscreteEvent> ad : waitQueue.values())
	        	for (BasicElement.DiscreteEvent e : ad)
	        		strLong.append(e + " ");
	        strLong.append("\r\n" + executingEvents + " executing elements:");
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	/**
	 * Adds an {@link es.ull.isaatc.simulation.groupedExtraThreaded.Activity} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.groupedExtraThreaded.ElementType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.groupedExtraThreaded.ResourceType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.groupedExtraThreaded.flow.Flow} to the model. These method
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
	public List<ActivityManager> getActivityManagerList() {
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
