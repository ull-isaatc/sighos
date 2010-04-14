/*
 * Simulation.java
 *
 * Created on 8 de noviembre de 2005, 18:47
 */

package es.ull.isaatc.simulation.bonnThreaded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import edu.bonn.cs.net.barrier.AbstractBarrier;
import edu.bonn.cs.net.barrier.TournamentBarrier;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.bonnThreaded.flow.Flow;
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
	private final TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>> futureEventList;
	private ArrayList<BasicElement.DiscreteEvent> currentEvents; 
	/** The pool of slave event executors */ 
    private SlaveEventExecutor [] executor;
    /** The barrier to control the phases of simulation */
	private AbstractBarrier barrier;
	
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
        futureEventList = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
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
        futureEventList = new TreeMap<Long, ArrayList<BasicElement.DiscreteEvent>>();
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = internalStartTs - 1;
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

        executor = new SlaveEventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++) {
			executor[i] = new SlaveEventExecutor(i);
		}
        barrier = new TournamentBarrier(executor, new BarrierAction());
		
        init();

		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationStartInfo(this, System.currentTimeMillis(), this.internalStartTs));
		// Starts all the generators
		for (Generator gen : generatorList)
			addWait(gen.getStartEvent(internalStartTs));
		// Starts all the resources
		for (Resource res : resourceList.values())
			addWait(res.getStartEvent(internalStartTs));		
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
    	debug("SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + internalEndTs);
    	printState();
		
		end();
		
		infoHandler.notifyInfo(new es.ull.isaatc.simulation.common.info.SimulationEndInfo(this, System.currentTimeMillis(), this.internalEndTs));
		debug("SIMULATION COMPLETELY FINISHED");
	}

	private void addWait(BasicElement.DiscreteEvent e) {
		ArrayList<BasicElement.DiscreteEvent> list = futureEventList.get(e.getTs());
		if (list == null) {
			list = new ArrayList<BasicElement.DiscreteEvent>();
			list.add(e);
			futureEventList.put(e.getTs(), list);
		}
		else
			list.add(e);		
	}
	
	private final class BarrierAction implements Runnable {
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
     * @author Iván Castilla Rodríguez
     *
     */
    public class SlaveEventExecutor extends Thread implements EventExecutor {
		private final int threadId;
    	private final ArrayDeque<BasicElement.DiscreteEvent> extraEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
    	private final ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
    	
		/**
		 * Creates a new slave executor
		 * @param id Executor's identifier 
		 */
		public SlaveEventExecutor(int id) {
			super("LPExec-" + id);
			threadId = id;
		}

    	/**
    	 * @param event the event to set
    	 */
    	public void addEvent(BasicElement.DiscreteEvent event) {
        	final long evTs = event.getTs();
            if (evTs == lvt) {
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
			while (lvt < internalEndTs) {
	    		// Executes its events
	    		final int totalEvents = currentEvents.size();
	    		// if there aren't many events, the first executor deals with them all
	    		if (threadId == 0 && totalEvents < nThreads) {
	    			for (int i = 0; i < totalEvents; i++)
	    				currentEvents.get(i).run();
	    		}
	    		else if (totalEvents >= nThreads) {
	    			final int lastEvent = (threadId + 1 == nThreads) ? totalEvents : (totalEvents * (threadId + 1) / nThreads);
	    			for (int i = totalEvents * threadId / nThreads; i < lastEvent; i++)
	    				currentEvents.get(i).run();
	    		}
	    		
	    		// Executes its local events
				while (!extraEvents.isEmpty()) {
					extraEvents.pop().run();
				}
				
				if (nThreads > 1)
					barrier.await();
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
	        for (ArrayList<BasicElement.DiscreteEvent> ad : futureEventList.values())
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
