/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationTimeFunction {
	private final TimeFunction function;

	public SimulationTimeFunction(Simulation simul, String className, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof Time)
				parameters[i] = simul.simulationTime2Long((Time)parameters[i]);
			if (parameters[i] instanceof Number)
				parameters[i] = simul.simulationTime2Long(new Time(simul.getUnit(), ((Number)parameters[i]).longValue())); 
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
