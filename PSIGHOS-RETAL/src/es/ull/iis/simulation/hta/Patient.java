/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.outcome.Cost;
import es.ull.iis.simulation.hta.outcome.LifeExpectancy;
import es.ull.iis.simulation.hta.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.model.DiscreteEvent;
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
	/** The timestamp of the last event executed (but the current one) */
	protected long lastTs = -1;
	/** The timestamp when this patient enters the simulation */
	protected long startTs;
	/** The cost measured for this patient */
	protected final Cost cost;
	/** The QALYs for this patient */
	protected final QualityAdjustedLifeExpectancy qaly; 
	/** The LYs for this patient */
	protected final LifeExpectancy ly; 

	// Events
	protected DeathEvent deathEvent = null;

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
		this.cost = simul.getCost();
		this.qaly = simul.getQALY();
		this.ly = simul.getLY();
	}

	public Patient(HTASimulation simul, Patient original, Intervention intervention) {
		super(simul, original.id, OBJ_TYPE_ID);
		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = original;		
		this.cost = original.cost;
		this.qaly = original.qaly;
		this.ly = simul.getLY();
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
	 * @return the clonedFrom
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * @return the lastTs
	 */
	public long getLastTs() {
		return lastTs;
	}

	/**
	 * @return the startTs
	 */
	public long getStartTs() {
		return startTs;
	}
	
	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	/**
	 * Last things to do when the patient is death, and before the {@link FinalizeEvent} event is launched.
	 */
	protected abstract void death();

	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends DiscreteEvent {
		
		public DeathEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			death();
			notifyEnd();
		}
	
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				deathEvent = null;
				return true;
			}
			return false;
		}
	}

	
}
