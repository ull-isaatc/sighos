package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 *  
 * @author Iván Castilla Rodríguez
 */
public abstract class HospitalSubModel {
	protected final SimulationObjectFactory factory;
	protected final Simulation simul;
	protected final String name;
	protected final String code;
	protected final int firstId;
	
	public HospitalSubModel(SimulationObjectFactory factory, String name, String code, int firstId) {
		this.factory = factory;
		simul = factory.getSimulation();
		this.name = name;
		this.code = code;
		this.firstId = firstId;
	}

	public abstract void createModel();
}
