/**
 * 
 */
package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class RequestResourcesEngine extends EngineObject implements es.ull.iis.simulation.model.engine.RequestResourcesEngine {
    /** Total of work items waiting for carrying out this activity */
    protected int queueSize = 0;

    final protected RequestResourcesFlow modelReq;
	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesEngine(SequentialSimulationEngine simul, RequestResourcesFlow modelReq) {
		super(modelReq.getIdentifier(), simul, "REQ");
		this.modelReq = modelReq;
	}

	public RequestResourcesFlow getModelReqFlow() {
		return modelReq;
	}
	
	@Override
	public void queueAdd(FlowExecutor fe) {
        modelReq.getManager().queueAdd(fe);
    	queueSize++;
		fe.getElement().incInQueue(fe);
		modelReq.inqueue(fe);
    }
    
	@Override
    public void queueRemove(FlowExecutor fe) {
		modelReq.getManager().queueRemove(fe);
    	queueSize--;
		fe.getElement().decInQueue(fe);
    }

    /**
     * Returns the size of this activity's queue 
     * @return the size of this activity's queue
     */
    public int getQueueSize() {
    	return queueSize;    	
    }

	@Override
	public boolean checkWorkGroup(ActivityWorkGroup wg, FlowExecutor fe) {
    	if (!wg.getCondition().check(fe))
    		return false;
        return wg.findSolution(fe);
	}

}
