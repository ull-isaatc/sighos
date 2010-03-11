/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.bonn3Phase;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.isaatc.simulation.bonn3Phase.flow.Flow;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.util.ExtendedMath;
import es.ull.isaatc.util.Output;

/**
 * Main parallel discrete event simulation class. A simulation uses all kind of 
 * {@link SimulationObject Simulation Objects} to define a model which will be executed.<p>
 * Two important simulation objects are {@link Activity activities} and {@link ResourceType 
 * resource types}. Both are grouped in different {@link ActivityManager activity managers}, 
 * which serve as an initial partition for parallelism.<p>
 * The simulation is feed with {@link BasicElement.DiscreteEvent discrete events} produced by 
 * {@link BasicElement Basic elements}.
 * @author Iván Castilla Rodríguez
 */
public class Simulation extends es.ull.isaatc.simulation.common.Simulation implements BarrierEnabled {	
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
	/** A timestamp-ordered list of events whose timestamp is in the future. Events are grouped according 
	 * to their timestamps. */
	private final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> futureEventList  = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
	/** The slave event executors */
    private SlaveEventExecutor [] executor;

	// Added for Bonn barrier
	/** Out flag set by the winner */
	private volatile boolean tourFlagOut = false;
	/** Sense-reversing flag */
	private boolean tourSense = false;
	/** Pre-computed rounds */
	private Round[] tourRounds;
	/** Flags to be set during the barrier */
	private AtomicBoolean[] tourFlag;
	private TPCBarrier secondBarrier;
    
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
	 * <li>As many {@link SlaveEventExecutor event executors} as threads set by using 
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
		
		// Sets default AM creator
		if (amCreator == null)
			amCreator = new StandardActivityManagerCreator(this);
		amCreator.createActivityManagers();
		debugPrintActManager();
		
		// Creates the event executors
        executor = new SlaveEventExecutor[nThreads];
    	/** The number of rounds used for the barrier */
    	final int numRounds = (int) Math.ceil(Math.log(nThreads + 1) / Math.log(2.0));
        for (int i = 0; i < nThreads; i++)
			executor[i] = new SlaveEventExecutor(i + 1, numRounds);

        // Starts second barrier
		secondBarrier = new TournamentBarrier(executor);

		final int numVirtualThreads = ExtendedMath.nextHigherPowerOfTwo(nThreads + 1);
        // Initializes the barrier of each executor
        for (SlaveEventExecutor ee : executor)
        	ee.setupBarrier(numVirtualThreads);
        
        // Barrier initialization
		tourRounds = new Round[numRounds];
		tourFlag = new AtomicBoolean[numRounds];
		for (int round = 0; round < numRounds; round++) {
			final int partnerId = ExtendedMath.powInt(2, round) % numVirtualThreads;
			Role role = Role.WINNER;
			BarrierEnabled partner;
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
		// Barrier initialized
		
        // Start executors
        for (SlaveEventExecutor ee : executor)
        	ee.start();
        
        // Distributes the AMs among the executors
        for (int i = 0; i < activityManagerList.size(); i++)
        	executor[i % nThreads].assignActivitManager(activityManagerList.get(i));

        // The user defined method for initialization is invoked
		init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.currentTimeMillis(), this.internalStartTs));
		
		// Starts all the generators
		for (Generator gen : generatorList)
			addWait(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.getStartEvent(internalStartTs));		

		// Adds the event to control end of simulation
		addWait(new SimulationElement().getStartEvent(internalEndTs));
        
        // Simulation main loop
		while (!isSimulationEnd()) {
			// Wait for all the events to be finished (the execution queue must be empty)
            await();
		}
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
        // The user defined method for finalization is invoked
		end();
		
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationEndInfo(this, System.currentTimeMillis(), this.internalEndTs));
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

	private void executeEvents() {
    	// Updates the future event list with the events produced by the executor threads
    	for (SlaveEventExecutor ee : executor) {
    		ArrayDeque<BasicElement.DiscreteEvent> list = ee.getWaitingEvents();
    		while (!list.isEmpty()) {
    			BasicElement.DiscreteEvent e = list.pop();
    			addWait(e);
    		}    		
    	}
        beforeClockTick();

        // Advances the simulation clock
        lvt = futureEventList.firstKey();
        infoHandler.notifyInfo(new TimeChangeInfo(this, lvt));
        afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);
        // Events with timestamp higher or equal to the simulation end time aren't
        // executed
        if (lvt < internalEndTs) {
        	ArrayList<BasicElement.DiscreteEvent> list = futureEventList.pollFirstEntry().getValue();
        	// Distributes the events with the same timestamp among the executors
    		int share = list.size();
    		// if there aren't many events, the first executor deals with them all
    		if (share < executor.length)
    			executor[0].addEvents(list);    			
    		else {
	    		share = share / executor.length;
	    		int iter = 0;
	    		for (; iter < executor.length - 1; iter++)
	    			executor[iter].addEvents(list.subList(share * iter, share * (iter + 1)));
	    		executor[iter].addEvents(list.subList(share * iter, list.size()));
    		}
        }
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.bonn3Phase.BarrierEnabled#await()
	 */
	@Override
	public void await() {
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
				roundObj.partner.getTourFlag(currentRound).set(tourSense);
				// Wait for the tournament winner (root) to complete the
				// barrier action.
				while (tourFlagOut != tourSense) {
				}
				// Exit switch statement (and thus the for loop).
				break;
			case ROOT:
				while (tourFlag[currentRound].get() != tourSense) {
				}
				executeEvents();
				tourFlagOut = tourSense;
				// Exit switch statement (and thus the for loop).
				break;
			}
			// Exit for loop.
			break;
		}
	}
    
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.bonn3Phase.BarrierEnabled#getTourFlag(int)
	 */
	@Override
	public AtomicBoolean getTourFlag(int ind) {
		return tourFlag[ind];
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
	        strLong.append("\r\n");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	/**
	 * An event executor used by the simulation. A simulation normally declares as many "slaves" as available
	 * cores in the computer.<p>
	 * Event's execution is divided into two phases. First, the events assigned by the main simulation thread  
	 * (by using {@link #addEvents(List)} are executed. If the execution of the events produces new events, 
	 * they are added to the local buffers of the executor. <p>Once the execution of all the current events has
	 * finished, the second phase is started, and the AM events are executed.  
	 * @author Iván Castilla Rodríguez
	 *
	 */
	final class SlaveEventExecutor extends Thread implements EventExecutor, BarrierEnabled {
		/** Execution local buffer */
		private final ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		/** Future event local buffer */
		private final ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
		/** A flag which indicates that the simulation has added new events to be executed by this executor */
		private final AtomicBoolean flag = new AtomicBoolean(false);
		/** The list of AMs tackled by this executor */
		private final ArrayDeque<ActivityManager> amList = new ArrayDeque<ActivityManager>();

		// Added for Bonn barrier
		/** Thread identifier */
		private final int threadId;
		/** Sense-reversing flag */
		private boolean tourSense = false;
		/** Pre-computed rounds */
		private final Round[] tourRounds;
		/** Flags to be set during the barrier */
		private final AtomicBoolean[] tourFlag;
    	/** The number of rounds used for the barrier */		
		private final int numRounds;
		
		/**
		 * Creates a new slave executor
		 * @param id Executor's identifier 
		 */
		public SlaveEventExecutor(int id, int numRounds) {
			super("LPExec-" + id);
			this.numRounds = numRounds;
			threadId = id;
			tourRounds = new Round[numRounds];
			tourFlag = new AtomicBoolean[numRounds];
		}

		/**
		 * Assigns an AM to this executor. The events of this AM are executed from this thread.
		 * @param am Activity Manager
		 */
		public void assignActivitManager(ActivityManager am) {
			amList.add(am);
		}
		
		@Override
		public void addEvent(BasicElement.DiscreteEvent event) {
	    	final long evTs = event.getTs();
	        if (evTs == lvt) {
				extraEvents.push(event);
	        }
	        else if (evTs > lvt)
				extraWaitingEvents.push(event);
	        else
	        	error("Causal restriction broken\t" + lvt + "\t" + event);
			
		}

		@Override
		public void addEvents(List<BasicElement.DiscreteEvent> eventList) {
			extraEvents.addAll(eventList);
			flag.set(true);
		}

		/**
		 * Returns the contents of the local future event buffer.
		 * @return The contents of the local future event buffer
		 */
		public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonn3Phase.BarrierEnabled#getTourFlag(int)
		 */
		@Override
		public AtomicBoolean getTourFlag(int ind) {
			return tourFlag[ind];
		}

		/* (non-Javadoc)
		 * @see es.ull.isaatc.simulation.bonn3Phase.BarrierEnabled#await()
		 */
		@Override
		public void await() {
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
					roundObj.partner.getTourFlag(currentRound).set(tourSense);
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
	    
		private void setupBarrier(int numVirtualThreads) {
			// Barrier initialization
			for (int round = 0; round < numRounds; round++) {
				final int partnerId = (threadId ^ ExtendedMath.powInt(2, round)) % numVirtualThreads;
				final boolean isWinner = (threadId % ExtendedMath.powInt(2, round + 1) == 0);
				Role role;
				BarrierEnabled partner;
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
						partner = Simulation.this;
					else
						partner = executor[partnerId - 1];
				}
				Round roundObj = new Round(partner, role);
				tourRounds[round] = roundObj;
				tourFlag[round] = new AtomicBoolean(false);
			}
		}
		
		@Override
		public void run() {			
			while (lvt < internalEndTs) {
				// Invokes barrier to start
				await();
				while (!extraEvents.isEmpty()) {
					extraEvents.pop().run();
				}
				// Waits to carry out the second phase (unless there's only one executor)
				if (nThreads > 1)
					secondBarrier.await();
				for (ActivityManager am : amList)
					am.executeWork();
			}
			
		}
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
	private static final class Round {
		/** The "competitor" thread. */
		private final BarrierEnabled partner;
		/** The role of this thread in the current round. */
		private final Role role;

		private Round(BarrierEnabled partner, Role role) {
			this.partner = partner;
			this.role = role;
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

	/**
	 * Returns a list of the resources of the model.
	 * @return Resources of the model.
	 */
	public TreeMap<Integer, Resource> getResourceList() {
		return resourceList;
	}

	/**
	 * Returns a list of the activities of the model.
	 * @return Activities of the model.
	 */
	public TreeMap<Integer, Activity> getActivityList() {
		return activityList;
	}

	/**
	 * Returns a list of the resource types of the model.
	 * @return Resource types of the model.
	 */
	public TreeMap<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}

	/**
	 * Returns a list of the element types of the model.
	 * @return element types of the model.
	 */
	public TreeMap<Integer, ElementType> getElementTypeList() {
		return elementTypeList;
	}

	/**
	 * Returns a list of the flows of the model.
	 * @return flows of the model.
	 */
	public TreeMap<Integer, Flow> getFlowList() {
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

}
