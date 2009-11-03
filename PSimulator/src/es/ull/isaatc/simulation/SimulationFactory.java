/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.common.Model;
import es.ull.isaatc.simulation.model.Model;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimulationFactory {
	public enum SimulationType {
		SEQUENTIAL,
		SIMEVENTS
	}
	public static Model getInstance(SimulationType type, int id, Model model) {
		switch (type) {
		case SEQUENTIAL: return es.ull.isaatc.simulation.sequential.factory.SimulationFactory.getSimulationInstance(id, model);
		}
		return null;
	}
}
