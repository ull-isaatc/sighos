package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 *  
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class HospitalSubModel {
	protected final SimulationObjectFactory factory;
	protected final Simulation simul;
	
	public HospitalSubModel(SimulationObjectFactory factory) {
		this.factory = factory;
		simul = factory.getSimulation();
	}
	
	
}
