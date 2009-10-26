/**
 * 
 */
package es.ull.isaatc.simulation;

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
	public static Simulation getInstance(SimulationType type, int id, Model model, Object ... params) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.StandAloneLPSimulation(id, model, params);
		}
	}
}
