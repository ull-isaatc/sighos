/**
 * 
 */
package es.ull.isaatc.function;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;

/**
 * Defines a time function which consists of an array of other time functions. The time unit
 * is used to choose the function corresponding to the current timestamp. Therefore,
 * getValue will return a value of part[i], where i = (ts / timeUnit) % functionArray.length 
 * @author Iván Castilla Rodríguez
 */
public class SimulationUniformlyDistributedSplitFunction extends SimulationTimeFunction {
	
	/**
	 * @param part
	 * @param timeUnit
	 */
	public SimulationUniformlyDistributedSplitFunction(Simulation simul, SimulationTimeFunction[] part, SimulationTime timeStep) {
		super(simul, "UniformlyDistributedSplitFunction");
		TimeFunction[] partf = new TimeFunction[part.length];
		for (int i = 0; i < partf.length; i++)
			partf[i] = part[i].getFunction();
		this.getFunction().setParameters(partf, simul.simulationTime2Double(timeStep));
	}

	/**
	 * @param part
	 * @param timeUnit
	 */
	public SimulationUniformlyDistributedSplitFunction(Simulation simul, SimulationTimeFunction[] part, double timeStep) {
		this(simul, part, new SimulationTime(simul.getUnit(), timeStep));
	}

}
