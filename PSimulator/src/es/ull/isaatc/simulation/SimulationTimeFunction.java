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
			if (parameters[i] instanceof SimulationTime)
				parameters[i] = simul.simulationTime2Double((SimulationTime)parameters[i]);
			else if (parameters[i] instanceof Number)
				parameters[i] = simul.simulationTime2Double(new SimulationTime(simul.getUnit(), ((Number)parameters[i]).doubleValue())); 
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
