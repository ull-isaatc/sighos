/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.model.Model;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory {
	public enum SimulationType {
		SEQUENTIAL,
		SIMEVENTS
	}
	public static Simulation getInstance(SimulationType type, int id, Model model) {
		switch (type) {
		case SEQUENTIAL: return es.ull.isaatc.simulation.sequential.factory.SimulationFactory.getSimulationInstance(id, model);
		}
		return null;
	}
}
