/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory {
	public enum SimulationType {
		SEQUENTIAL,
		SIMEVENTS,
		SIMEVENTS2,
		INTERVAL,
		BUFFERED
	}
	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, Time startTs, Time endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS2: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalBufferThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}

	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, long startTs, long endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SIMEVENTS2: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalBufferThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}
}
