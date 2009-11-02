/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunctionFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelTimeFunction {
	private final es.ull.isaatc.function.TimeFunction function;

	public ModelTimeFunction(TimeUnit unit, String className, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof Time)
				parameters[i] = unit.time2Double((Time)parameters[i]);
			else if (parameters[i] instanceof Number)
				parameters[i] = unit.time2Double(new Time(unit, ((Number)parameters[i]).doubleValue())); 
		}
		function = TimeFunctionFactory.getInstance(className, parameters);
	}
	
	/**
	 * @return the function
	 */
	public es.ull.isaatc.function.TimeFunction getFunction() {
		return function;
	}
}
