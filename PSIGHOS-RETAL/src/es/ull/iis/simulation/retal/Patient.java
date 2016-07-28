/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.sequential.BasicElement;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Patient extends BasicElement {
	/** Number of interventions that will be compared for a patient. This value is also used to determine the id of the patient */ 
	private static final int N_INTERVENTIONS = 2;
	/** Counter to assign a unique id to each patient */
	private static int patientCounter = 0;
	
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
	/** The accumulated cost incurred by this patient */
	private double cummCost = 0.0;
	/** The accumulated QALYs lived by this patient */
	private double cummQALYs = 0.0;
	/** The current utility applied to this patient */
	private double utility;
	
	/**
	 * Creates a patient and initializes the default events
	 * @param simul Simulation this patient is attached to
	 * @param initAge The initial age of the patient
	 * @param sex Sex of the patient
	 */
	public Patient(RETALSimulation simul, double initAge, int sex, long timeToDeath) {
		super(patientCounter, simul);
		intervention = new NullIntervention();
		patientCounter += N_INTERVENTIONS;
		this.initAge = 365*initAge;
		this.sex = sex;
		this.clonedFrom = null;
		this.nIntervention = 0;
		// Limiting lifespan to MAX AGE
		this.timeToDeath = timeToDeath;
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
		Patient[] clones = new Patient[N_INTERVENTIONS];
		clones[0] = this;
		for (int i = 0; i < N_INTERVENTIONS - 1; i++)
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
	 * 
	 * @return the current age of the patient
	 */
	public double getAge() {
		return (initAge + ts) / 365;
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
	 * @return the cummCost
	 */
	public double getCummCost() {
		return cummCost;
	}

	/**
	 * @return the cummQALYs
	 */
	public double getCummQALYs() {
		return cummQALYs;
	}

	public void update() {
		cummQALYs += (ts - lastTs) * utility / 365.0;
		cummCost = computeCost(lastTs / 365.0, ts / 365.0); 
	}
	
	/**
	 * Computes the cost associated to the current state between initAge and endAge
	 * @param initAge Age at which the patient starts using the resources
	 * @param endAge Age at which the patient ends using the resources 
	 * @return The accumulated cost during the defined period
	 */
	public double computeCost(double initAge, double endAge) {
		return 0.0;
	}

	/**
	 * @return the utility
	 */
	public double getUtility() {
		return utility;
	}

	@Override
	/**
	 * Sets the current timestamp for this patient, saves the previous timestamp in @link(lastTs), and updates costs and QALYs.
	 * @param ts New timestamp to be assigned
	 */
	public void setTs(long ts) {
		lastTs = this.ts;
		super.setTs(ts);
		update();
	}
	
	/**
	 * Defines a discrete event whose execution can be cancelled after being scheduled
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public abstract class CancelableEvent extends DiscreteEvent {
		protected boolean cancelled = false;
		
		public CancelableEvent(long ts) {
			super(ts);
		}

		/**
		 * Sets the event as cancelled. Cannot be (theoretically) reverted.
		 */
		public void cancel() {
			cancelled = true;
		}
	}
	
	public final class DeathEvent extends DiscreteEvent {
		
		public DeathEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DEATH, this.getTs()));
			// TODO Save statistics
			notifyEnd();
		}
	
	}

}
