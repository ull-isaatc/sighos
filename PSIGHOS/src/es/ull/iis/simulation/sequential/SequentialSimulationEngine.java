package es.ull.iis.simulation.sequential;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.MergeFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

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
public class SequentialSimulationEngine extends es.ull.iis.simulation.model.engine.SimulationEngine {

	/** End-of-simulation control */
	private CountDownLatch endSignal;

	/** The identifier to be assigned to the next element */ 
	protected int nextElementId = 0;
	/** List of active elements */
	private final Map<Integer, ElementEngine> activeElementList = new TreeMap<Integer, ElementEngine>();

    /** Local virtual time. Represents the current simulation time */
	protected long lvt;

	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected final PriorityQueue<DiscreteEvent> waitQueue = new PriorityQueue<DiscreteEvent>();
	
	/**
	 * Creates a new instance of Simulation
	 *
	 * @param id
	 *            This simulation's identifier
	 * @param startTs
	 *            Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs
	 *            Simulation's end timestamp expresed in Simulation Time Units
	 */
	public SequentialSimulationEngine(int id, Simulation simul) {
		super(id, simul);
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
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	@Override
	public void addWait(DiscreteEvent e) {
		waitQueue.add(e);
	}
	
	public void addExecution(DiscreteEvent e) {
		e.run();
	}
	
    protected DiscreteEvent removeWait() {
        return waitQueue.poll();
    }
    
    /**
     * Removes a specific event from the waiting queue. This function can be used to cancel an event
     * @param e Event to be removed
     * @return True if the queue contained the event; false otherwise
     */
    protected boolean removeWait(DiscreteEvent e) {
        return waitQueue.remove(e);
    }
    

	/**
	 * @return the nextElementId
	 */
	protected int getNextElementId() {
		return nextElementId++;
	}

	/**
	 * Adds an element when it starts its execution.
	 * 
	 * @param elem
	 *            An element that starts its execution.
	 */
	public void addActiveElement(ElementEngine elem) {
		activeElementList.put(elem.getIdentifier(), elem);
	}

	/**
	 * Removes an element when it finishes its execution.
	 * 
	 * @param elem
	 *            An element that finishes its execution.
	 */
	public void removeActiveElement(ElementEngine elem) {
		activeElementList.remove(elem.getIdentifier());
	}

	/**
	 * Returns the element with the specified identifier.
	 * 
	 * @param id
	 *            The element's identifier.
	 * @return The element with the specified identifier.
	 */
	public ElementEngine getActiveElement(int id) {
		return activeElementList.get(id);
	}

	/**
	 * Notifies the end of a logical process.
	 */
	protected void notifyEnd() {
		endSignal.countDown();
	}

	/**
	 * Prints the current state of the simulation for debug purposes. Prints the current local 
	 * time, the contents of the future event list and the execution queue. 
	 */
	public void printState() {
		if (isDebugEnabled()) {
			StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
			strLong.append("LVT: " + lvt + "\r\n");
	        strLong.append(waitQueue.size() + " waiting elements: ");
	        for (DiscreteEvent e : waitQueue)
	            strLong.append(e + " ");
	        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
			debug(strLong.toString());
		}
	}

	public Map<Integer, ElementEngine> getActiveElementList() {
		return activeElementList;
	}
	
	@Override
	public void initializeEngine() {
	}

	@Override
	public ResourceList getResourceListInstance() {
		return new ResourceList();
	}

	@Override
	public ResourceEngine getResourceEngineInstance(Resource modelRes) {
		return new ResourceEngine(this, modelRes);
	}

	@Override
	public ActivityManagerEngine getActivityManagerEngineInstance(ActivityManager modelAM) {
		return new ActivityManagerEngine(this, modelAM);
	}

	@Override
	public ElementEngine getElementEngineInstance(Element modelElem) {
		return new ElementEngine(this, modelElem);
	}
    
	@Override
	public RequestResourcesEngine getRequestResourcesEngineInstance(
			RequestResourcesFlow reqFlow) {
		return new RequestResourcesEngine(this, reqFlow);
	}

	@Override
	public MergeFlowEngine getMergeFlowEngineInstance(MergeFlow modelFlow) {
		return new MergeFlowEngine(this, modelFlow);
	}

	@Override
	public void simulationLoop() {
		while (!simul.isSimulationEnd(lvt)) {
            // Executes all the events with timestamps equal to lvt 
			while (waitQueue.peek().getTs() == lvt) {
            	addExecution(waitQueue.poll());
			}
			// Checks the condition-driven generators and executes the events
			// FIXME: Not sure whether it would work 
			simul.checkConditions(lvt);
			while (waitQueue.peek().getTs() == lvt) {
            	addExecution(waitQueue.poll());
			}
			for (ActivityManager am : simul.getActivityManagerList()) {
				am.executeWork();
			}
			// Executes user-specified actions after all the events with the same timestamp have been executed
            simul.afterClockTick();
			final long newLVT = waitQueue.peek().getTs();
			// Executes user-specified actions before the clock advances
            simul.beforeClockTick();
			if (simul.isSimulationEnd(newLVT)) {
	            // Updates the simulation clock but do not execute further events
				lvt = (lvt > simul.getEndTs()) ? lvt : simul.getEndTs();
			}
			else {
	            // Updates the simulation clock
	            lvt = newLVT;
	            simul.notifyInfo(new TimeChangeInfo(simul, lvt));
	            debug("SIMULATION TIME ADVANCING " + lvt);
			}
		}
//	        // Extracts the first event
//	        if (! waitQueue.isEmpty()) {
//	            DiscreteEvent e = removeWait();
//	            // Advances the simulation clock
//	            simul.beforeClockTick();
//	            lvt = e.getTs();
//	            simul.notifyInfo(new TimeChangeInfo(simul, lvt));
//	            simul.afterClockTick();
//	            debug("SIMULATION TIME ADVANCING " + lvt);
//	            // Events with timestamp greater or equal to the maximum simulation time aren't
//	            // executed
//	            if (lvt >= simul.getEndTs())
//	                addWait(e);
//	            else {
//	            	addExecution(e);
//	                // Extracts all the events with the same timestamp
//	                boolean flag = false;
//	                do {
//	                    if (! waitQueue.isEmpty()) {
//	                        e = removeWait();
//	                        if (e.getTs() == lvt) {
//	                        	addExecution(e);
//	                            flag = true;
//	                        }
//	                        else {  
//	                            flag = false;
//	                            addWait(e);
//	                        }
//	                    }
//	                    else {  // The waiting queue is empty
//	                        flag = false;
//	                    }
//	                } while ( flag );
//	            }
//	        }        
//		}
	}

	@Override
	public void addEvent(DiscreteEvent ev) {
		if (ev.getTs() < lvt) {
			error("Causal restriction broken\t" + lvt + "\t" + ev);
		}
        else {
            addWait(ev);
        }		
	}

}
