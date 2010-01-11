/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;

/**
 * A wrapper class for {@link es.ull.isaatc.function.TimeFunction TimeFunction}.
 * Thus {@link TimeStamp} can be used to define the time function parameters.
 * @author Iván Castilla Rodríguez
 */
public class SimulationTimeFunction {
	/** Inner {@link es.ull.isaatc.function.TimeFunction TimeFunction} */
	private final TimeFunction function;

	/**
	 * Creates a time function to be used in a simulation.
	 * @param unit Simulation time unit
	 * @param className Name of the time function (must be a class accepted by 
	 * {@link es.ull.isaatc.function.TimeFunctionFactory TimeFunctionFactory} 
	 * @param parameters Parameters of the time function to be created
	 */
	public SimulationTimeFunction(TimeUnit unit, String className, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof TimeStamp)
				parameters[i] = unit.convert((TimeStamp)parameters[i]);
			else if (parameters[i] instanceof Number)
				parameters[i] = unit.convert(new TimeStamp(unit, Math.round(((Number)parameters[i]).doubleValue()))); 
		}
		function = TimeFunctionFactory.getInstance(className, parameters);
	}
	
	/**
	 * Returns the inner {@link es.ull.isaatc.function.TimeFunction TimeFunction}
	 * @return the inner {@link es.ull.isaatc.function.TimeFunction TimeFunction}
	 */
	public es.ull.isaatc.function.TimeFunction getFunction() {
		return function;
	}
}
