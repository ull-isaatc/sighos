/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class DelayFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the delay */
    private final String description;
	/** Duration of the delay */
    private final TimeFunction duration;

	/**
	 * 
	 */
	public DelayFlow(Simulation model, String description, TimeFunction duration) {
		super(model);
		this.description = description;
		this.duration = duration;
	}

	/**
	 * 
	 */
	public DelayFlow(Simulation model, String description, long duration) {
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
    public long getDurationSample(ElementInstance fe) {
    	return Math.round(getDuration().getValue(fe));
    }

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.flow.Flow#addPredecessor(es.ull.iis.simulation.model.flow.Flow)
	 */
	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public void afterFinalize(ElementInstance fe) {
	}

	@Override
	public void request(ElementInstance wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread)) {
					simul.notifyInfo(new ElementActionInfo(simul, wThread, wThread.getElement(), this, wThread.getExecutionWG(), null, ElementActionInfo.Type.START, simul.getTs()));
					wThread.getElement().debug("Start delay\t" + this + "\t" + getDescription());	
					wThread.startDelay(getDurationSample(wThread));
					// TODO: Check if it's needed
//					timeLeft = 0;
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
	public void finish(ElementInstance wThread) {
		simul.notifyInfo(new ElementActionInfo(simul, wThread, wThread.getElement(), this, wThread.getExecutionWG(), null, ElementActionInfo.Type.END, simul.getTs()));
		if (wThread.getElement().isDebugEnabled())
			wThread.getElement().debug("Finishes\t" + this + "\t" + getDescription());
		afterFinalize(wThread);
		next(wThread);
	}

}
