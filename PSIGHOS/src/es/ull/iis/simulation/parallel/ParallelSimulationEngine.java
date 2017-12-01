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
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationObject;
import es.ull.iis.simulation.model.engine.EventSourceEngine;
import es.ull.iis.simulation.model.flow.MergeFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Main parallel discrete event simulation class. A simulation uses all kind of 
 * {@link SimulationObject ParallelSimulationEngine Objects} to define a model which will be executed.<p>
 * Two important simulation objects are {@link RequestResourcesEngine activities} and {@link ResourceType 
 * resource types}. Both are grouped in different {@link ActivityManager activity managers}, 
 * which serve as an initial partition for parallelism.<p>
 * The simulation is feed with {@link EventSourceEngine.DiscreteEvent discrete events} produced by 
 * {@link EventSourceEngine Basic elements}.
 * @author Iván Castilla Rodríguez
 */
public class ParallelSimulationEngine extends es.ull.iis.simulation.model.engine.SimulationEngine {
	/** List of active elements */
	private final Map<Integer, ElementEngine> activeElementList = Collections.synchronizedMap(new TreeMap<Integer, ElementEngine>());
	/** Local virtual time. Represents the current simulation time */
	private volatile long lvt;
    /** A counter to know how many events are in execution */
    private AtomicInteger executingEvents = new AtomicInteger(0);
	/** A timestamp-ordered list of events whose timestamp is in the future. Events are grouped according 
	 * to their timestamps. */
	private final TreeMap<Long, ArrayList<DiscreteEvent>> futureEventList  = new TreeMap<Long, ArrayList<DiscreteEvent>>();
	private ArrayList<DiscreteEvent> currentEvents;
	/** The slave event executors */
    private SlaveEventExecutor [] executor;
    /** The barrier to control the phases of simulation */
	private AbstractBarrier barrier;
	/** Number of worker threads which run the simulation events */
	private final int nThreads;
	
	
	/**
	 * Creates a new ParallelSimulationEngine which starts at <code>startTs</code> and finishes at <code>endTs</code>.
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation
	 * @param unit Time unit used to define the simulation time
	 * @param startTs Timestamp of simulation's start
	 * @param endTs Timestamp of simulation's end
	 */
	public ParallelSimulationEngine(int id, Simulation simul, int nThreads) {
		super(id, simul);
        // The Local virtual time is set to the immediately previous instant to the simulation start time
        lvt = simul.getStartTs() - 1;
        this.nThreads = nThreads;
	}
	
	/**
	 * Returns the number of workers to execute events defined in this simulation.
	 * @return the number of workers to execute events defined in this simulation
	 */
	public int getNThreads() {
		return nThreads;
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
	@Override
	public void addWait(DiscreteEvent e) {
		ArrayList<DiscreteEvent> list = futureEventList.get(e.getTs());
		if (list == null) {
			list = new ArrayList<DiscreteEvent>();
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
    protected boolean removeWait(DiscreteEvent e) {
        return (futureEventList.remove(e) != null);
    }
    
	private class BarrierAction implements Runnable {
		@Override
		public void run() {
			// Updates the future event list with the events produced by the executor threads
	    	for (SlaveEventExecutor ee : executor) {
	    		ArrayDeque<DiscreteEvent> list = ee.getWaitingEvents();
	    		while (!list.isEmpty()) {
	    			DiscreteEvent e = list.pop();
	    			addWait(e);
	    		}    		
	    	}
	    	advanceSimulationClock();
			
		}
	}
	
	private void advanceSimulationClock() {
        simul.beforeClockTick();

        // Advances the simulation clock
        lvt = futureEventList.firstKey();
        simul.notifyInfo(new TimeChangeInfo(simul, lvt));
        simul.afterClockTick();
        debug("SIMULATION TIME ADVANCING " + lvt);

        if (!simul.isSimulationEnd(lvt)) {
        	currentEvents = futureEventList.pollFirstEntry().getValue();
        	// Distributes the events with the same timestamp among the executors
    		executingEvents.addAndGet(currentEvents.size());
        }
        
	}
	
	/**
	 * Prints the current state of the simulation for debug purposes. Prints the current local 
	 * time, the contents of the future event list and the execution queue. 
	 */
	public void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(futureEventList.size() + " waiting elements: ");
	        for (ArrayList<DiscreteEvent> ad : futureEventList.values())
	        	for (DiscreteEvent e : ad)
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
		private final ArrayDeque<DiscreteEvent> extraEvents = new ArrayDeque<DiscreteEvent>();
		/** Future event local buffer */
		private final ArrayDeque<DiscreteEvent> extraWaitingEvents = new ArrayDeque<DiscreteEvent>();
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
		public void addEvent(DiscreteEvent event) {
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
		public ArrayDeque<DiscreteEvent> getWaitingEvents() {
			return extraWaitingEvents;
		}
		
		@Override
		public void run() {
			while (!simul.isSimulationEnd(lvt)) {
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
			    		ArrayDeque<DiscreteEvent> list = ee.getWaitingEvents();
			    		while (!list.isEmpty()) {
			    			DiscreteEvent e = list.pop();
			    			addWait(e);
			    		}    		
			    	}
			    	advanceSimulationClock();					
				}
			}
			
		}
	}

	/**
	 * Adds an element when it starts its execution.
	 * @param elem An element that starts its execution.
	 */
	public void addActiveElement(ElementEngine elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * @param elem An element that finishes its execution.
	 */
	public void removeActiveElement(ElementEngine elem) {
		activeElementList.remove(elem.getIdentifier());
	}

	/**
	 * Returns the element with the specified identifier.
	 * @param id The element's identifier.
	 * @return The element with the specified identifier.
	 */
	public ElementEngine getActiveElement(int id) {
		return activeElementList.get(id);
	}

	/**
	 * Returns the current list of active elements.
	 * @return The current list of active elements
	 */
	public Map<Integer, ElementEngine> getActiveElementList() {
		return activeElementList;
	}
	
	@Override
	/**
	 * As many SlaveEventExecutor event executors as threads set by using 
	 * {@link #setNThreads(int)} are created.
	 * The AMs are equally distributed among the available executors.
	 */ 
	public void initializeEngine() {
		// Creates the event executors
        executor = new SlaveEventExecutor[nThreads];
        for (int i = 0; i < nThreads; i++) {
			executor[i] = new SlaveEventExecutor(i);
        }
        if (nThreads > 1)
        	barrier = new TournamentBarrier(executor.length, new BarrierAction());
        
        // Distributes the AMs among the executors
        final List<ActivityManager> amList = simul.getActivityManagerList();
        for (int i = 0; i < amList.size(); i++)
        	executor[i % nThreads].assignActivityManager(amList.get(i));
	}

	@Override
	public void simulationLoop() {
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
	}

	@Override
	public ResourceEngine getResourceEngineInstance(Resource modelRes) {
		return new ResourceEngine(this, modelRes);
	}

	@Override
	public ElementEngine getElementEngineInstance(Element modelElem) {
		return new ElementEngine(this, modelElem);
	}

	@Override
	public ResourceList getResourceListInstance() {
		return new ResourceList();
	}

	@Override
	public ActivityManagerEngine getActivityManagerEngineInstance(ActivityManager modelAM) {
		return new ActivityManagerEngine(this, modelAM);
	}

	@Override
	public RequestResourcesEngine getRequestResourcesEngineInstance(RequestResourcesFlow reqFlow) {
		return new RequestResourcesEngine(this, reqFlow);
	}

	@Override
	public MergeFlowEngine getMergeFlowEngineInstance(MergeFlow modelFlow) {
		return new MergeFlowEngine(this, modelFlow);
	}

	@Override
	public void addEvent(DiscreteEvent ev) {
   		((EventExecutor)Thread.currentThread()).addEvent(ev);
	}

}
