/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
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

}
