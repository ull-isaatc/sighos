/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.VariableStoreSimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Patient extends VariableStoreSimulationObject implements EventSource {
	private final static String OBJ_TYPE_ID = "PAT";
	/** The original patient, this one was cloned from */ 
	private final Patient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	protected final int nIntervention;
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** The timestamp when this patient enters the simulation */
	protected long startTs;
	/** True if the patient is dead */
	private boolean dead; 


	/**
	 * 
	 * @param simul
	 * @param intervention
	 */
	public Patient(HTASimulation simul, Intervention intervention) {
		super(simul, simul.getPatientCounter(), OBJ_TYPE_ID);
		// Initialize patients with no complications
		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = null;
		this.dead = false;
	}

	public Patient(HTASimulation simul, Patient original, Intervention intervention) {
		super(simul, original.id, OBJ_TYPE_ID);
		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = original;		
		this.dead = false;
	}

	@Override
	public void notifyEnd() {
        simul.addEvent(onDestroy(simul.getSimulationEngine().getTs()));
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// Nothing to do
	}
	
	/**
	 * @return
	 */
	public int getnIntervention() {
		return nIntervention;
	}

	/**
	 * @return the intervention
	 */
	public Intervention getIntervention() {
		return intervention;
	}

	/**
	 * @return the clonedFrom
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * @return the startTs
	 */
	public long getStartTs() {
		return startTs;
	}

	/**
	 * Returns true if the patient is dead; false otherwise
	 * @return true if the patient is dead; false otherwise
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Sets the patient as dead
	 */
	public void setDead() {
		this.dead = true;
	}

}
