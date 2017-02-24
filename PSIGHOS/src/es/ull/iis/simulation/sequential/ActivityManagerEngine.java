package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.PrioritizedMap;

/**
 * Partition of activities. It serves as a mutual exclusion mechanism to access a set of activities
 * and a set of resource types. This mutual exclusion mechanism is never used implicitly by this object, 
 * so it must be controlled by the user by means of a semaphore. When the user wants to modify an object 
 * belonging to this AM, it's required to invoke the <code>waitSemaphore</code> method. When the modification 
 * finishes, the <code>signalSemaphore()</code> method must be invoked.  
 * @author Iván Castilla Rodríguez
 */
public class ActivityManagerEngine extends SimulationObject implements es.ull.iis.simulation.model.ActivityManagerEngine {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
    private final ActivityManager modelAM;
    
   /**
	* Creates a new instance of ActivityManager.
	* @param simul Simulation this activity manager belongs to
    */
    public ActivityManagerEngine(SequentialSimulationEngine simul, ActivityManager modelAM) {
        super(nextid++, simul, "AM");
        this.modelAM = modelAM;
        simul.add(this);
    }

    /**
     * @return the modelAM
     */
    public ActivityManager getModelAM() {
    	return modelAM;
    }

	/**
     * Informs the activities of new available resources. Reviews the queue of waiting work items 
     * looking for those which can be executed with the new available resources. The work items 
     * used are removed from the waiting queue.<p>
     * In order not to traverse the whole list of work items, this method determines the
     * amount of "useless" ones, that is, the amount of work items belonging to an activity 
     * which can't be performed with the current resources. If this amount is equal to the size
     * of waiting work items, this method stops. 
     */
    @Override
    public void notifyResource() {
        // A count of the useless single flows 
    	int uselessSF = 0;
    	// A postponed removal list
    	final ArrayList<FlowExecutor> toRemove = new ArrayList<FlowExecutor>();
    	final Iterator<FlowExecutor> iter = modelAM.getQueueIterator();
    	while (iter.hasNext() && (uselessSF <modelAM.queueSize())) {
    		final FlowExecutor wt = iter.next();
            // TODO: Check whether it always works fine
            final RequestResourcesFlow reqResources = (RequestResourcesFlow) wt.getCurrentFlow();
            
            final int result = wt.availableResource(simul.getRequestResource(reqResources));
            if (result == -1) {
        		toRemove.add(wt);
        		uselessSF--;
        	}
        	else if (result > 0) {	// The activity can't be performed with the current resources
            	uselessSF += result;
        	}
		}
    	// Postponed removal to avoid conflict with the activity manager queue
    	for (FlowExecutor wt : toRemove)
    		simul.getRequestResource((RequestResourcesFlow) wt.getCurrentFlow()).queueRemove(wt);
    } 

}
