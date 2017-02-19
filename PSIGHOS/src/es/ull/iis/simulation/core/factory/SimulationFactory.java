/**
 * 
 */
package es.ull.iis.simulation.core.factory;

import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

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
	public static SimulationObjectFactory<?, ?> getInstance(SimulationType type, int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.iis.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PARALLEL: return new es.ull.iis.simulation.parallel.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}

	public static SimulationObjectFactory <?, ?>getInstance(SimulationType type, int id, String description, TimeUnit unit, long startTs, long endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.iis.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PARALLEL: return new es.ull.iis.simulation.parallel.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}
}
