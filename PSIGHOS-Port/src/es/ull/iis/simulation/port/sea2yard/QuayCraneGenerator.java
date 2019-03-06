/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class QuayCraneGenerator extends TimeDrivenElementGenerator {
	final int initPosition;
	final int lastTask;

	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public QuayCraneGenerator(Simulation model, ElementType et, InitializerFlow flow, int initPosition, int lastTask) {
		super(model, 1, et, flow, new SimulationPeriodicCycle(model.getTimeUnit(), 0L, new SimulationTimeFunction(model.getTimeUnit(), "ConstantVariate", model.getEndTs()), 1));
		this.initPosition = initPosition;
		this.lastTask = lastTask;
	}

	@Override
	public EventSource createEventSource(int ind, GenerationInfo info) {
		return new QuayCrane(simul, info.getElementType(), info.getFlow(), initPosition, lastTask);
	}
}
