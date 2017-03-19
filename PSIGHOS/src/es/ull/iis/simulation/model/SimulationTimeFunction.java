/**
 * 
 */
package es.ull.iis.simulation.model;

import simkit.random.RandomNumberFactory;
import es.ull.iis.function.RandomFunction;
import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.function.TimeFunctionParams;

/**
 * A wrapper class for {@link es.ull.iis.function.TimeFunction TimeFunction}.
 * Thus {@link TimeStamp} can be used to define the time function parameters.
 * @author Iván Castilla Rodríguez
 */
public class SimulationTimeFunction extends TimeFunction {
	/** Inner {@link es.ull.iis.function.TimeFunction TimeFunction} */
	private final TimeFunction function;

	/**
	 * Creates a time function to be used in a simulation.
	 * @param unit ParallelSimulationEngine time unit
	 * @param className Name of the time function (must be a class accepted by 
	 * {@link es.ull.iis.function.TimeFunctionFactory TimeFunctionFactory} 
	 * @param parameters Parameters of the time function to be created
	 */
	public SimulationTimeFunction(TimeUnit unit, String className, Object... parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof TimeStamp)
				parameters[i] = unit.convert((TimeStamp)parameters[i]);
			else if (parameters[i] instanceof Number)
				parameters[i] = unit.convert(new TimeStamp(unit, Math.round(((Number)parameters[i]).doubleValue())));
			// Emulates a kind of recursive behaviour
			else if (parameters[i] instanceof SimulationTimeFunction)
				parameters[i] = ((SimulationTimeFunction)parameters[i]).function;
		}
		function = TimeFunctionFactory.getInstance(className, parameters);
		if (function instanceof RandomFunction) {
			((RandomFunction)function).getRandom().setRandomNumber(RandomNumberFactory.getInstance());
		}
	}

	@Override
	public double getValue(TimeFunctionParams params) {
		return function.getValue(params);
	}

	@Override
	public void setParameters(Object... params) {
		// TODO Auto-generated method stub
		
	}
}
