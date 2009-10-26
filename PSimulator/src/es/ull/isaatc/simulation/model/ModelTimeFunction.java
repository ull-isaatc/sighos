/**
 * 
 */
package es.ull.isaatc.simulation.model;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelTimeFunction {
	private final TimeFunction function;

	public ModelTimeFunction(Model model, String className, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof Time)
				parameters[i] = model.simulationTime2Double((Time)parameters[i]);
			else if (parameters[i] instanceof Number)
				parameters[i] = model.simulationTime2Double(new Time(model.getUnit(), ((Number)parameters[i]).doubleValue())); 
		}
		function = TimeFunctionFactory.getInstance(className, parameters);
	}
	
	/**
	 * @return the function
	 */
	public TimeFunction getFunction() {
		return function;
	}
}
