/**
 * 
 */
package es.ull.isaatc.simulation.factory;

import es.ull.isaatc.simulation.TimeStamp;
import es.ull.isaatc.simulation.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory {
	/**
	 * @author Iván Castilla Rodríguez
	 */
	public enum SimulationType {
		SEQUENTIAL, 
		PARALLEL		
	}
	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PARALLEL: return new es.ull.isaatc.simulation.parallel.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}

	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, long startTs, long endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PARALLEL: return new es.ull.isaatc.simulation.parallel.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}
}
