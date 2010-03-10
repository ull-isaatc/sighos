/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.bonnThreaded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.isaatc.simulation.bonnThreaded.flow.Flow;
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
	// Fine grain adjust of the work load
	/** Work load factor applied to slave workers */ 
	private final int grain = 1;
	/** Work load factor applied to the master worker */
	private final int rest = 1;
	/** Amount of sets of events to be distributed among the workers */
	private int nBunches; 

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

		nBunches = nThreads * grain + rest;
        executor = new SlaveEventExecutor[nThreads];
		mainExecutor = new MasterExecutor();
        for (int i = 0; i < nThreads; i++)
			executor[i] = new SlaveEventExecutor(i);
        
        final int numVirtualThreads = ExtendedMath.nextHigherPowerOfTwo(nThreads + 1);
        mainExecutor.setupBarrier(numVirtualThreads);
        for (SlaveEventExecutor se : executor)
        	se.setupBarrier(numVirtualThreads);
        
        for (SlaveEventExecutor se : executor)
        	se.start();
        
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.currentTimeMillis(), this.internalStartTs));
		// Starts all the generators
		for (Generator gen : generatorList)
			mainExecutor.addWaitingEvent(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			mainExecutor.addWaitingEvent(res.getStartEvent(internalStartTs));		

		mainExecutor.addWaitingEvent(new SimulationElement().getStartEvent(internalEndTs));
        mainExecutor.start();
        try {
			mainExecutor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
		end();
		
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationEndInfo(this, System.currentTimeMillis(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
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
		
		protected EventExecutor(int threadId) {
			super("LPExec-" + threadId);
			this.threadId = threadId;
			numRounds = (int) Math.ceil(Math.log(nThreads + 1) / Math.log(2.0));
			tourRounds = new Round[numRounds];
			tourFlag = new AtomicBoolean[numRounds];
		}
		
		protected abstract void addEvent(BasicElement.DiscreteEvent event);
		protected abstract void addEvents(List<BasicElement.DiscreteEvent> eventList);
		protected abstract void addWaitingEvent(BasicElement.DiscreteEvent event);
		
		protected abstract void setupBarrier(int numVirtualThreads);
		protected abstract void await();

	}

	final private class SlaveEventExecutor extends EventExecutor {
		private ArrayDeque<BasicElement.DiscreteEvent> events = new ArrayDeque<BasicElement.DiscreteEvent>();
		private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		
		private SlaveEventExecutor(int index) {
			super(index);
		}
		
		/**
		 * @param event the event to set
		 */
		protected void addEvent(BasicElement.DiscreteEvent event) {
			events.push(event);
		}

		/**
		 * @param event the event to set
		 */
		protected void addEvents(List<BasicElement.DiscreteEvent> eventList) {
			events.addAll(eventList);
		}

		/**
		 * @param event the event to set
		 */
		protected void addWaitingEvent(BasicElement.DiscreteEvent event) {
			extraWaitingEvents.push(event);
		}

		protected ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		@Override
		public void run() {
			long endTs = internalEndTs;
			while (lvt < endTs) {
				await();
				while (!events.isEmpty()) {
					events.pop().run();
				}
			}
			
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnThreaded.Simulation.EventExecutor#await()
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
		 * @see es.ull.isaatc.simulation.bonnThreaded.Simulation.EventExecutor#setupBarrier(int)
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
						partner = executor[partnerId];
				}
				Round roundObj = new Round(partner, role);
				tourRounds[round] = roundObj;
				tourFlag[round] = new AtomicBoolean(false);
			}
		}

	}

	final private class MasterExecutor extends EventExecutor {
	    /** The list of current executing events for the main executor */ 
		private ArrayDeque<BasicElement.DiscreteEvent> execEvents = new ArrayDeque<BasicElement.DiscreteEvent>();

		private MasterExecutor() {
			super(0);
		}
		
		@Override
		protected void addEvent(BasicElement.DiscreteEvent event) {
			execEvents.push(event);
		}

		@Override
		protected void addEvents(List<BasicElement.DiscreteEvent> eventList) {
			execEvents.addAll(eventList);
		}

		@Override
		protected void addWaitingEvent(BasicElement.DiscreteEvent event) {
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
	     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
	     * timestamp equal to the LP timestamp. 
	     */
	    private void execWaitingElements() {
	    	// Updates the future event list with the events produced by the executor threads
	    	for (SlaveEventExecutor ee : executor) {
	    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
	    		while (!list.isEmpty()) {
	    			BasicElement.DiscreteEvent e = list.pop();
	    			addWaitingEvent(e);
	    		}    		
	    	}
	        // Advances the simulation clock
	        beforeClockTick();
	        lvt = waitQueue.firstKey();
	        infoHandler.notifyInfo(new TimeChangeInfo(Simulation.this, lvt));
	        afterClockTick();
	        debug("SIMULATION TIME ADVANCING " + lvt);
	        // Events with timestamp greater or equal to the maximum simulation time aren't
	        // executed
	        if (lvt < internalEndTs) {
	        	ArrayList<BasicElement.DiscreteEvent> list = waitQueue.pollFirstEntry().getValue();
	    		int share = list.size();
	    		if (share < nBunches)
	    			addEvents(list);    			
	    		else {
		    		share = share / nBunches;
		    		for (int iter = 0; iter < nThreads; iter++)
		    			executor[iter].addEvents(list.subList(share * iter * grain, share * grain * (iter + 1)));
		    		addEvents(list.subList(share * executor.length * grain, list.size()));
	    		}
	        }
	    }

		public void run() {
	        // Simulation main loop
			while (!isSimulationEnd()) {
	    		while (!execEvents.isEmpty())
	    			execEvents.pop().run();
				// Every time the loop is entered we must wait for all the events from the 
				// previous iteration to be finished (the execution queue must be empty)
				await();
			}
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnThreaded.Simulation.EventExecutor#await()
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
				case LOSER:
					roundObj.partner.tourFlag[currentRound].set(tourSense);
					// Wait for the tournament winner (root) to complete the
					// barrier action.
					while (tourFlagOut != tourSense) {
					}
					// Exit switch statement (and thus the for loop).
					break;
				case ROOT:
					while (tourFlag[currentRound].get() != tourSense) {
					}
					execWaitingElements();
					tourFlagOut = tourSense;
					// Exit switch statement (and thus the for loop).
					break;
				}
				// Exit for loop.
				break;
			}
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonnThreaded.Simulation.EventExecutor#setupBarrier(int)
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnThreaded.Activity} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnThreaded.ElementType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnThreaded.ResourceType} to the model. These method
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
	 * Adds an {@link es.ull.isaatc.simulation.bonnThreaded.flow.Flow} to the model. These method
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
	
	/**
	 * Returns a list of the resources of the model.
	 * 
	 * @return Resources of the model.
	 */
	public TreeMap<Integer, Resource> getResourceList() {
		return resourceList;
	}

	/**
	 * Returns a list of the activities of the model.
	 * 
	 * @return Activities of the model.
	 */
	public TreeMap<Integer, Activity> getActivityList() {
		return activityList;
	}

	/**
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public TreeMap<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}

	/**
	 * Returns a list of the element types of the model.
	 * 
	 * @return element types of the model.
	 */
	public TreeMap<Integer, ElementType> getElementTypeList() {
		return elementTypeList;
	}

	/**
	 * Returns a list of the flows of the model.
	 * 
	 * @return flows of the model.
	 */
	public TreeMap<Integer, Flow> getFlowList() {
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
