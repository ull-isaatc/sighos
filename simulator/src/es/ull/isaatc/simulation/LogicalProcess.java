package es.ull.isaatc.simulation;

import java.util.*;

import mjr.heap.HeapAscending;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.sync.*;
import es.ull.isaatc.util.*;

/** 
 * A logical process (LP) is a subregion of the simulation. It includes a set of resource types,
 * activities, resources, and elements. The LP handles a set of events which interact with any
 * of the components associated to this LP. An LP is subdivided into activity managers.   
 * @author Carlos Martín Galán
 */
public class LogicalProcess extends SimulationObject implements Runnable {
	/** LP's counter. */
	private static int nextId = 0;
    /** Controls the advance ot the simulation time. */
	protected Lock lpLock;
    /** Local virtual time. Represents the current simulation time for this LP. */
	protected double lvt;
    /** The maximum timestamp for this logical process. When this timestamp is reached, this LP 
     * finishes its execution. */
    protected double maxgvt; 
    /** Thread pool to execute events */
    protected ThreadPool tp;
	/** A queue containing the events currently executing */
	protected Vector<BasicElement.DiscreteEvent> execQueue;
	/** A timestamp-ordered list of events whose timestamp is in the future. */
	protected HeapAscending waitQueue;
    /** The set of activity managers contained by this LP. */
    protected ArrayList<ActivityManager> managerList;
    /** Thread where the logical process function is implemented */
    private Thread lpThread = null;

	/**
     * Creates a logical process with initial timestamp 0.0
     * @param simul Simulation which this LP is attached to.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, double endT) {
        this(simul, 0.0, endT);
	}

	/**
     * Creates a logical processwith initial timestamp <code>startT</code> and
     * finishing timestamp <code>endT</code>. 
     * @param simul Simulation which this LP is attached to.
     * @param startT Initial timestamp.
     * @param endT Finishing timestamp.
     */
	public LogicalProcess(Simulation simul, double startT, double endT) {
		super(nextId++, simul);
        tp = new ThreadPool(3, 3);
        execQueue = new Vector<BasicElement.DiscreteEvent>();
        waitQueue = new HeapAscending();
        lpLock = new Lock();
        maxgvt = endT;
        lvt = startT;
        managerList = new ArrayList<ActivityManager>();
	}
    
    /**
     * Indicates if the simulation end has been reached.
     * @return True if the maximum simulation time has been reached. False in other case.
     */
    public boolean isSimulationEnd() {
        return(lvt >= maxgvt);
    }

    @Override
	public double getTs() {
		return lvt;
	}

    /**
     * Sends an event to the execution queue by looking for a thread to execute it. An event is 
     * added to the execution queue when the LP has reached the event timestamp. 
     * @param e Event to be executed
     * @return True if the event was succesfully added. False in other case.
     */
	public synchronized boolean addExecution(BasicElement.DiscreteEvent e) {
		tp.getThread(e);
        return execQueue.add(e);
	}

    /**
     * Removes an event from the execution queue. An event is removed from the execution
     * queue when it has finished the action it had to carry out.
     * @param e Event to be removed
     * @return True if the event was succesfully removed. False in other case.
     */
    protected synchronized boolean removeExecution(BasicElement.DiscreteEvent e) {
		if (execQueue.remove(e)) { // pudo quitarse
            // Si era el último elemento del sistema
			// MOD 7/3/06 Añadida la 1ª condición para evitar que más de un evento
			// del mismo elemento dispare esta condición.
            if (execQueue.isEmpty()) {
    			// si era el último tiene que notificarlo
            	// FIXME: Esta comprobación sobra
    			if (!isSimulationEnd()) 
    				unlock();
            }
			return(true);
		}
		return(false);
    }
    
    /**
     * Removes an event from the execution queue, but performing a previous synchronization.
     * The synchronization consists on waiting for the LP to lock, or for the simulation end.
     * @param e Event to be removed
     * @return True if the event was succesfully removed. False in other case.
     */
    protected boolean removeExecutionSync(BasicElement.DiscreteEvent e) {
        while(!isSimulationEnd() && !locked());
        return removeExecution(e);
    }
    
    /**
     * Sends an event to the waiting queue. An event is added to the waiting queue if 
     * its timestamp is greater than the LP timestamp.
     * @param e Event to be added
     */
	public synchronized void addWait(BasicElement.DiscreteEvent e) {
		waitQueue.insert(e);
	}

    /**
     * Removes an event from the waiting queue. An event is removed from the waiting 
     * queue when the LP reaches the timestamp of that event.
     * @return The first event of the waiting queue.
     */
    protected synchronized BasicElement.DiscreteEvent removeWait() {
        return (BasicElement.DiscreteEvent) waitQueue.extractMin();
    }

    /**
     * Locks this LP. This LP is locked when there are no more elements waiting to be executed
     * at the current timestamp.
     */
    protected void lock() {
    	try {
    		lpLock.lock();
    	} catch (InterruptedException ex) {
        	ex.printStackTrace();
        }
    }
    
    /**
     * Unlocks this LP. This LP is unlocked when the last event in the execution queue finishes 
     * its execution.  
     */
    protected void unlock() {
        lpLock.unlock();
    }
    
    /**
     * Checks if the LP is locked.
     * @return True if the LP is locked. False in other case.
     */
    protected boolean locked() {
        return lpLock.locked();
    }

    /**
     * Adds an activity manager to the manager list
     * @param am New Activity manager.
     */
    protected void add(ActivityManager am) {
        managerList.add(am);
    }
    
    /**
     * Executes a simulation clock cycle. Extracts all the events from the waiting queue with 
     * timestamp equal to the LP timestamp. 
     */
    private void execWaitingElements() {
        // Extracts the first event
        if (! waitQueue.isEmpty()) {
            BasicElement.DiscreteEvent e = removeWait();
            // Advances the simulation clock
            lvt = e.getTs();
            simul.notifyListeners(new TimeChangeInfo(this));
            print(Output.MessageType.DEBUG, "SIMULATION TIME ADVANCING " + lvt);
            // Events with timestamp greater or equal to the maximum simulation time aren't
            // executed
            if (lvt >= maxgvt)
                addWait(e);
            else {
                addExecution(e);
                // Extracts all the events with the same timestamp
                boolean flag = false;
                do {
                    if (! waitQueue.isEmpty()) {
                        e = removeWait();
                        if (e.getTs() == lvt) {
                            addExecution(e);
                            flag = true;
                        }
                        else {  
                            flag = false;
                            addWait(e);
                        }
                    }
                    else {  // The waiting queue is empty
                        flag = false;
                    }
                } while ( flag );
            }
        }        
    }

	/**
	 * A basic element which facilitates the end-of-simulation control. It simply
	 * has an event at <code>maxgvt</code>, so there's always at least one event in 
	 * the LP. 
	 * @author Iván Castilla Rodríguez
	 */
    class SafeLPElement extends BasicElement {

		public SafeLPElement() {
			super(0, LogicalProcess.this.simul);
		}

		@Override
		protected void init() {
			addEvent(new DummyEvent(maxgvt));
		}
		
		@Override
		protected void end() {
		}

    	class DummyEvent extends BasicElement.DiscreteEvent {
    		DummyEvent(double ts) {
    			super(ts, LogicalProcess.this);
    		}
			public void event() {				
			}    		
    	}
    }
    
    /**
     * Controls the LP execution. First, a <code>SafeLPElement</code> is added. The execution loop 
     * consists on waiting for the elements which are in execution, then the simulation clock is 
     * advanced and a new set of events is executed. 
     */
	public void run() {
        new SafeLPElement().start(this);
		while (!isSimulationEnd()) {
            lock();
            execWaitingElements();
		}
		// Frees the execution queue
    	print(Output.MessageType.DEBUG, "Execution queue freed",
    			"TP. MAX:" + tp.getMaxThreads() + "\tINI:" + tp.getInitThreads() 
    			+ "\tCREATED:" + tp.getNThreads());
    	tp.finish();    	
    	print(Output.MessageType.DEBUG, "SIMULATION TIME FINISHES",
    			"SIMULATION TIME FINISHES\r\nSimulation time = " +
            	lvt + "\r\nPreviewed simulation time = " + maxgvt);
    	printState();
    	// Notifies the simulation about the end of this logical process
        simul.notifyEnd();
	}

	/**
	 * Starts the thread that handles the LP execution.
	 */
	public void start() {
		if (lpThread == null) {
	        lpThread = new Thread(this);
	        lpThread.start();
		}
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "LP";
	}

	/**
	 * Does a debug print of this LP. Prints the current local time, the contents of
	 * the waiting and execution queues, and the contents of the activity managers. 
	 */
	protected void printState() {
		StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
		strLong.append("LVT: " + lvt + "\r\n");
        strLong.append(waitQueue.size() + " waiting elements: ");
        for (int i = 0; i < waitQueue.size(); i++) {
            BasicElement.DiscreteEvent e = (BasicElement.DiscreteEvent) waitQueue.get(i);            
            strLong.append(e.getElement() + " ");
        }
        strLong.append("\r\n" + execQueue.size() + " executing elements:");
        for (int i = 0; i < execQueue.size(); i++) {
            strLong.append(execQueue.get(i));
        }
        strLong.append("\r\n");
        for (ActivityManager am : managerList)
        	strLong.append("Activity Manager " + am.getIdentifier() + "\r\n");
        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
		print(Output.MessageType.DEBUG, "Waiting\t" + waitQueue.size() + "\tExecuting\t" + execQueue.size(), strLong.toString());
	}
}