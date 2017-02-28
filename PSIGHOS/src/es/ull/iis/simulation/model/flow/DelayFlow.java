/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;

/**
 * @author Iván Castilla
 *
 */
public class DelayFlow extends SingleSuccessorFlow implements TaskFlow, ResourceHandlerFlow {
    /** A brief description of the delay */
    private final String description;
	/** Duration of the delay */
    private final TimeFunction duration;

	/**
	 * 
	 */
	public DelayFlow(Model model, String description, TimeFunction duration) {
		super(model);
		this.description = description;
		this.duration = duration;
	}

	/**
	 * 
	 */
	public DelayFlow(Model model, String description, long duration) {
		this(model, description, TimeFunctionFactory.getInstance("ConstantVariate", duration));
	}

	@Override
	public String getDescription() {
		return description;
	}
    
	/**
	 * @return the duration
	 */
	public TimeFunction getDuration() {
		return duration;
	}

    /**
     * Returns the duration of the activity where this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.
     * @return The activity duration.
     */
    public long getDurationSample(FlowExecutor fe) {
    	return Math.round(getDuration().getValue(fe));
    }

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.flow.Flow#addPredecessor(es.ull.iis.simulation.model.flow.Flow)
	 */
	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public void afterFinalize(FlowExecutor fe) {
	}

	@Override
	public void request(FlowExecutor wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread)) {
					model.notifyInfo(new ElementActionInfo(model, wThread, wThread.getElement(), this, wThread.getExecutionWG(), ElementActionInfo.Type.START, model.getSimulationEngine().getTs()));
					wThread.getElement().debug("Starts\t" + this + "\t" + getDescription());			
					long finishTs = model.getSimulationEngine().getTs() + getDurationSample(wThread);
					// TODO: Check if it's needed
//					timeLeft = 0;
					wThread.getElement().addFinishEvent(finishTs, this, wThread);
				}
				else {
					wThread.cancel(this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	@Override
	public void finish(FlowExecutor wThread) {
		model.notifyInfo(new ElementActionInfo(model, wThread, wThread.getElement(), this, wThread.getExecutionWG(), ElementActionInfo.Type.END, model.getSimulationEngine().getTs()));
		if (wThread.getElement().isDebugEnabled())
			wThread.getElement().debug("Finishes\t" + this + "\t" + getDescription());
		afterFinalize(wThread);
		next(wThread);
	}

}
