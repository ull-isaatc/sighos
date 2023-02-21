/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;

/**
 * A flow that makes an element be delayed for a certain time, specified at modeling time
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeFunctionDelayFlow extends DelayFlow {
	/** Duration of the delay */
    private final TimeFunction duration;

    /**
     * Creates a delay flow
     * @param model The simulation model this flow belongs to
     * @param description A short text describing this flow
     * @param duration The duration of the delay
     */
	public TimeFunctionDelayFlow(final Simulation model, final String description, final TimeFunction duration) {
		super(model, description);
		this.duration = duration;
	}

    /**
     * Creates a delay flow
     * @param model The simulation model this flow belongs to
     * @param description A short text describing this flow
     * @param duration The duration of the delay
     */
	public TimeFunctionDelayFlow(final Simulation model, final String description, final long duration) {
		this(model, description, TimeFunctionFactory.getInstance("ConstantVariate", duration));
	}
    
	/**
	 * Returns the time function that characterizes the duration of the delay
	 * @return the time function that characterizes the duration of the delay
	 */
	public TimeFunction getDuration() {
		return duration;
	}

    /**
     * Returns the duration of the delay
     * The value returned by the random number function could be negative. In this case, it returns 0.
     * @param elem The element delaying
     * @return The duration of the delay
     */
	@Override
    public long getDurationSample(final Element elem) {
    	return Math.max(0, Math.round(getDuration().getValue(elem)));
    }

}
