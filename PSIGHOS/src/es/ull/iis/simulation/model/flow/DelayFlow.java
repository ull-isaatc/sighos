/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.model.Element;
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
     * @param elem The element delaying
     * @return The activity duration.
     */
    public long getDurationSample(Element elem) {
    	return Math.round(getDuration().getValue(elem));
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
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					final Element elem = ei.getElement();
					simul.notifyInfo(new ElementActionInfo(simul, ei, elem, this, ei.getExecutionWG(), null, ElementActionInfo.Type.START, simul.getTs()));
					elem.debug("Start delay\t" + this + "\t" + getDescription());	
					ei.startDelay(getDurationSample(elem));
					// TODO: Check if it's needed
//					timeLeft = 0;
				}
				else {
					ei.cancel(this);
					next(ei);
				}
			}
			else {
				ei.updatePath(this);
				next(ei);
			}
		} else
			ei.notifyEnd();
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
