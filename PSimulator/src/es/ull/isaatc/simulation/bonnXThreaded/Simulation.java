/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.bonnXThreaded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.isaatc.simulation.bonnXThreaded.flow.Flow;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.util.ExtendedMath;
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
public abstract class Simulation extends es.ull.isaatc.simulation.common.Simulation {
	
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

	/** The way the Activity Managers are created */
	protected ActivityManagerCreator amCreator = null;

	/** Local virtual time. Represents the current simulation time. */
	protected volatile long lvt;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> waitQueue;
	/** The pool of slave event executors */ 
    private SlaveEventExecutor [] executor;
    /** Represents the master executor, which runs events and the simulation clock */
    private MasterExecutor mainExecutor;
    
	private final Semaphore simAccess = new Semaphore(1); 

	private AtomicBoolean[] workReady;
	
	// Bonn Barrier
	/** Out flag set by the winner */
	private volatile boolean tourFlagOut = false;
	
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
		super(id, description, unit, startTs, endTs);
        waitQueue = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
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
		super(id, description, unit, startTs, endTs);
        waitQueue = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
	}
	
	/**
	 * Contains the specifications of the model. All the components of the model
	 * must be declared here.
	 * <p>
	 * The components are added simply by invoking their constructors. For
	 * example: <code>
	 * Activity a1 = new Activity(0, this, "Act1");
	 * ResourceType rt1 = new ResourceType(0, this, "RT1");
	 * </code>
	 */
	protected abstract void createModel();

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
		
		createModel();
		debug("SIMULATION MODEL CREATED");
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();

		// TODO: Should change the creation so to create exactly "nthreads", not "nthreads + 1"
		workReady = new AtomicBoolean[nThreads + 1];
        executor = new SlaveEventExecutor[nThreads];
		mainExecutor = new MasterExecutor();
		workReady[0] = new AtomicBoolean(false);
        for (int i = 0; i < nThreads; i++) {
			executor[i] = new SlaveEventExecutor(i + 1);
			workReady[i + 1] = new AtomicBoolean(false);
        }
        
        final int numVirtualThreads = ExtendedMath.nextHigherPowerOfTwo(nThreads + 1);
        mainExecutor.setupBarrier(numVirtualThreads);
        for (SlaveEventExecutor se : executor)
        	se.setupBarrier(numVirtualThreads);
        
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.nanoTime(), this.internalStartTs));
		// Starts all the generators
		for (Generator gen : generatorList)
			addFutureEvent(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addFutureEvent(res.getStartEvent(internalStartTs));		

		addFutureEvent(new SimulationElement().getStartEvent(internalEndTs));
		
		advanceClock();
        mainExecutor.start();
        for (SlaveEventExecutor se : executor)
        	se.start();
        
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

    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    private void advanceClock() {
        // Advances the simulation clock
        beforeClockTick();
        lvt = waitQueue.firstKey();
        infoHandler.notifyInfo(new TimeChangeInfo(Simulation.this, lvt));
        afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
    }

	private void addFutureEvent(BasicElement.DiscreteEvent e) {
		ArrayList<BasicElement.DiscreteEvent> list = waitQueue.get(e.getTs());
		if (list == null) {
			list = new ArrayList<BasicElement.DiscreteEvent>();
			list.add(e);
			waitQueue.put(e.getTs(), list);
		}
		else
			list.add(e);		
	}
	
	private void updateFutureEventList(Deque<BasicElement.DiscreteEvent> moreEvents) {
		try {
			simAccess.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// First updates the future event list
		while (!moreEvents.isEmpty()) {
			// TODO: Check if pop() is more expensive than going through the list and make clear at the end
			addFutureEvent(moreEvents.pop());
		}    		
		
		// Now checks if the current timestamp entry can be deleted
		boolean ready = true;
		for (int i = 0; (i <= nThreads) && ready; i++)
			if (!workReady[i].get())
				ready = false;
		if (ready) {
			waitQueue.pollFirstEntry();
			// TODO: Check if this is the correct place to do this
			for (AtomicBoolean bol : workReady)
				bol.set(false);
		}
		
		simAccess.release();
	}
	
	/**
	 * Roles of the different threads during tournament barriers.
	 * 
	 * @author Patrick Peschlow
	 */
	private static enum Role {
		WINNER, LOSER, WILDCARD, ROOT;
	}

	/**
	 * Helper object to pre-compute rounds of tournament barriers.
	 * 
	 * @author Patrick Peschlow
	 */
	private static class Round {
		/** The "competitor" thread. */
		private final EventExecutor partner;
		/** The role of this thread in the current round. */
		private final Role role;

		private Round(EventExecutor partner, Role role) {
			this.partner = partner;
			this.role = role;
		}
	}

	protected abstract class EventExecutor extends Thread {
		protected final int threadId;
		/** Sense-reversing flag */
		protected boolean tourSense = false;
		/** Pre-computed rounds */
		protected final Round[] tourRounds;
		/** Flags to be set during the barrier */
		protected final AtomicBoolean[] tourFlag;
		/** The number of rounds used for the barrier */		
		protected final int numRounds;
		protected ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
	    /** The list of current executing events for the main executor */ 
		protected ArrayDeque<BasicElement.DiscreteEvent> execEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		
		protected EventExecutor(int threadId) {
			super("LPExec-" + threadId);
			this.threadId = threadId;
			numRounds = (int) Math.ceil(Math.log(nThreads + 1) / Math.log(2.0));
			tourRounds = new Round[numRounds];
			tourFlag = new AtomicBoolean[numRounds];
		}
		
		protected void addEvent(BasicElement.DiscreteEvent event) {
			execEvents.push(event);
		}
		/**
		 * @param event the event to set
		 */
		protected void addWaitingEvent(BasicElement.DiscreteEvent event) {
			extraWaitingEvents.push(event);
		}

		protected abstract void setupBarrier(int numVirtualThreads);
		protected abstract void await();

	}

	final private class SlaveEventExecutor extends EventExecutor {
		
		private SlaveEventExecutor(int index) {
			super(index);
		}
		
		@Override
		public void run() {
			long endTs = internalEndTs;
			while (lvt < endTs) {
				// First executes global list events
				final ArrayList<BasicElement.DiscreteEvent> current = waitQueue.firstEntry().getValue();
    			workReady[threadId].set(true);
				
				final int totalEvents = current.size();
	    		// if there aren't many events, the first executor deals with them all
	    		if (totalEvents > nThreads) {
	    			final int lastEvent = totalEvents * threadId / (nThreads + 1);
	    			for (int i = totalEvents * (threadId - 1) / (nThreads + 1); i < lastEvent; i++)
	    				current.get(i).run();
					while (!execEvents.isEmpty()) {
						execEvents.pop().run();
					}
					updateFutureEventList(extraWaitingEvents);
	    		}
				await();
			}
			
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnXThreaded.Simulation.EventExecutor#await()
		 */
		@Override
		protected void await() {
			tourSense = !tourSense;
			int currentRound = 0;
			for (;;) {
				final Round roundObj = tourRounds[currentRound];
				assert roundObj.role != Role.ROOT;
				switch (roundObj.role) {
				case WINNER:
					while (tourFlag[currentRound].get() != tourSense) {
					}
					++currentRound;
					// Continue to next round.
					continue;
				case WILDCARD:
					++currentRound;
					// Continue to next round.
					continue;
				case LOSER:
					roundObj.partner.tourFlag[currentRound].set(tourSense);
					// Wait for the tournament winner (root) to complete the
					// barrier action.
					while (tourFlagOut != tourSense) {
					}
					// Exit switch statement (and thus the for loop).
					break;
				}
				// Exit for loop.
				break;
			}
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnXThreaded.Simulation.EventExecutor#setupBarrier(int)
		 */
		@Override
		protected void setupBarrier(int numVirtualThreads) {
			for (int round = 0; round < numRounds; round++) {
				final int partnerId = (threadId ^ ExtendedMath.powInt(2, round))
						% numVirtualThreads;
				final boolean isWinner = (threadId % ExtendedMath.powInt(2, round + 1) == 0);
				Role role;
				EventExecutor partner;
				if (partnerId > nThreads) {
					role = Role.WILDCARD;
					partner = null;
				} else {
					if (isWinner) {
						role = Role.WINNER;
					} else {
						role = Role.LOSER;
					}
					if (partnerId == 0)
						partner = mainExecutor;
					else
						partner = executor[partnerId - 1];
				}
				Round roundObj = new Round(partner, role);
				tourRounds[round] = roundObj;
				tourFlag[round] = new AtomicBoolean(false);
			}
		}

	}

	final private class MasterExecutor extends EventExecutor {
		private MasterExecutor() {
			super(0);
		}
		
		public void run() {
    		if (nThreads > 0) {
    	        // Simulation main loop
    			while (!isSimulationEnd()) {
    				// First executes global list events
    				final ArrayList<BasicElement.DiscreteEvent> current = waitQueue.firstEntry().getValue();
	    			workReady[threadId].set(true);
    				
    				final int totalEvents = current.size();
    	    		// if there aren't many events, the first executor deals with them all
    	    		if (totalEvents < (nThreads + 1)) {
    	    			for (BasicElement.DiscreteEvent e : current)
    	    				e.run();
    	    		}
    	    		else {
    	    			for (int i = totalEvents * nThreads / (nThreads + 1); i < totalEvents; i++)
    	    				current.get(i).run();
    	    		}
    	    		
    	    		// Executes local events
    	    		while (!execEvents.isEmpty())
    	    			execEvents.pop().run();
					updateFutureEventList(extraWaitingEvents);
    	    		
    				// Every time the loop is entered we must wait for all the events from the 
    				// previous iteration to be finished (the execution queue must be empty)
   	    			await();
    			}
    		}
    		else {
    	        // Simulation main loop
    			while (!isSimulationEnd()) {
    				// First executes global list events
    				final ArrayList<BasicElement.DiscreteEvent> current = waitQueue.firstEntry().getValue();
	    			workReady[threadId].set(true);
    				
	    			for (BasicElement.DiscreteEvent e : current)
	    				e.run();
    	    		
    	    		// Executes local events
    	    		while (!execEvents.isEmpty())
    	    			execEvents.pop().run();

    	    		updateFutureEventList(extraWaitingEvents);    	    		
   	    			advanceClock();
    	    		
    			}
    			
    		}
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnXThreaded.Simulation.EventExecutor#await()
		 */
		@Override
		protected void await() {
			tourSense = !tourSense;
			int currentRound = 0;
			for (;;) {
				final Round roundObj = tourRounds[currentRound];
				switch (roundObj.role) {
				case WINNER:
					while (tourFlag[currentRound].get() != tourSense) {
					}
					++currentRound;
					// Continue to next round.
					continue;
				case WILDCARD:
					++currentRound;
					// Continue to next round.
					continue;
				case ROOT:
					while (tourFlag[currentRound].get() != tourSense) {
					}
					advanceClock();
					tourFlagOut = tourSense;
					// Exit switch statement (and thus the for loop).
					break;
				}
				// Exit for loop.
				break;
			}
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnXThreaded.Simulation.EventExecutor#setupBarrier(int)
		 */
		@Override
		protected void setupBarrier(int numVirtualThreads) {
			for (int round = 0; round < numRounds; round++) {
				final int partnerId = ExtendedMath.powInt(2, round) % numVirtualThreads;
				Role role = Role.WINNER;
				EventExecutor partner;
				if (partnerId > nThreads) {
					role = Role.WILDCARD;
					partner = null;
				} else {
					if (round == numRounds - 1) {
						role = Role.ROOT;
					}
					partner = executor[partnerId - 1];
				}
				Round roundObj = new Round(partner, role);
				tourRounds[round] = roundObj;
				tourFlag[round] = new AtomicBoolean(false);
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
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	/**
	 * Adds an {@link es.ull.isaatc.simulation.bonnXThreaded.Activity} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnXThreaded.ElementType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnXThreaded.ResourceType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnXThreaded.flow.Flow} to the model. These method
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
