/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.outcome.Outcome;
import es.ull.iis.simulation.sequential.BasicElement;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Patient extends BasicElement {
	private Patient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	protected final int nIntervention;
	/** Initial age of the patient (stored in days) */
	protected final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	protected final int sex;
	
	// Event times
	/** Precomputed time to death for this patient */
	protected final long timeToDeath;
	
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** The timestamp of the last event executed (but the current one) */
	private long lastTs = -1;
	/** The timestamp when this patient enters the simulation */
	private long startTs;
	/** The current currentUtility applied to this patient */
	private double currentUtility = 1.0;
	private final ArrayList<Outcome> outcomes;
	
	/**
	 * Creates a patient and initializes the default events
	 * @param simul Simulation this patient is attached to
	 * @param initAge The initial age of the patient
	 * @param sex Sex of the patient
	 */
	public Patient(RETALSimulation simul, double initAge, int sex) {
		super(simul.getPatientCounter() * RETALSimulation.NINTERVENTIONS, simul);
		intervention = new NullIntervention();
		this.initAge = 365*initAge;
		this.sex = sex;
		this.clonedFrom = null;
		this.nIntervention = 0;
		// Limiting lifespan to MAX AGE
		this.timeToDeath = simul.getCommonParams().getDeathTime(this);
		this.outcomes = simul.getOutcomes();
	}

	/**
	 * Creates a new patient who is a clone of another one and who is assigned to a different intervention
	 * @param original The original patient whose attributes will be cloned 
	 * @param nIntervention New intervention this clone is assigned to
	 */
	public Patient(Patient original, int nIntervention) {
		super(original.id + nIntervention, original.simul);
		intervention = new Screening(new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1), 1.0, 1.0);
		this.clonedFrom = original;
		this.nIntervention = nIntervention;
		this.initAge = original.initAge;
		this.sex = original.sex;
		this.timeToDeath = original.timeToDeath;
		this.outcomes = original.outcomes;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "PAT";
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElement#init()
	 */
	@Override
	protected void init() {
		startTs = this.getTs();
		simul.getInfoHandler().notifyInfo(new PatientInfo(this.simul, this, PatientInfo.Type.START, this.getTs()));
		addEvent(new DeathEvent(timeToDeath));
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElement#end()
	 */
	@Override
	protected void end() {
		simul.getInfoHandler().notifyInfo(new PatientInfo(this.simul, this, PatientInfo.Type.FINISH, this.getTs()));

	}
	
	/**
	 * Creates as many exact copies of this patient as interventions need to be simulated. 
	 * The id of each clone is the id of the original patient plus the intervention branch number.
	 * @return An array of patients containing all the clones including the original one in position 0.    
	 */
	protected Patient[] clone() {
		Patient[] clones = new Patient[RETALSimulation.NINTERVENTIONS];
		clones[0] = this;
		for (int i = 0; i < RETALSimulation.NINTERVENTIONS - 1; i++)
			clones[i] = new Patient(this, i);
		return clones;
	}
	
	/**
	 * Redeclaration of addEvent to make it visible from the rest of classes of this package. @see es.ull.iis.simulation.sequential.BasicElement.addEvent
	 * @param e
	 */
	protected void addEvent(DiscreteEvent e) {
		super.addEvent(e);
	}
	
	/**
	 * @return
	 */
	public int getnIntervention() {
		return nIntervention;
	}

	/**
	 * @return the initial age of the patient
	 */
	public double getInitAge() {
		return initAge / 365;
	}

	/**
	 * @return the startTs
	 */
	public long getStartTs() {
		return startTs;
	}

	/**
	 * 
	 * @return the current age of the patient
	 */
	public double getAge() {
		return (initAge + ts - startTs) / 365.0;
	}
	
	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return timeToDeath;
	}

	/**
	 * @return the clonedFrom
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * @return the currentUtility
	 */
	public double getUtility() {
		return currentUtility;
	}

	/**
	 * @param currentUtility the current utility to set
	 */
	public void setUtility(double currentUtility) {
		this.currentUtility = currentUtility;
	}

	@Override
	/**
	 * Sets the current timestamp for this patient, saves the previous timestamp in @link(lastTs), and updates costs and QALYs.
	 * @param ts New timestamp to be assigned
	 */
	public void setTs(long ts) {
		lastTs = this.ts;
		super.setTs(ts);
		for (Outcome outcome : outcomes) {
			outcome.update(this);
		}
	}
	
	/**
	 * @return the lastTs
	 */
	public long getLastTs() {
		return lastTs;
	}

	/**
	 * Last things to do when the patient is death, and before the {@link FinalizeEvent} event is launched.
	 */
	public void death() {
		
	}
	
	public final class DeathEvent extends DiscreteEvent {
		
		public DeathEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DEATH, this.getTs()));
			death();
			notifyEnd();
		}
	
	}

}
