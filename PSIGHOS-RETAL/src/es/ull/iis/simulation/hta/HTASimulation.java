/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HTASimulation extends Simulation {
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	public final static int DEF_NPATIENTS = 1000;

	protected final Intervention intervention;
	protected final int nPatients;
	
	/** True if this is a clone of an original simulation; false otherwise */
	protected final boolean cloned;
	
	protected final Patient[] generatedPatients; 

	/**
	 * 
	 * @param id
	 * @param description
	 * @param secondOrder
	 * @param intervention
	 * @param endTs
	 * @param nInterventions
	 * @param nPatients
	 */
	public HTASimulation(int id, String description, TimeUnit unit, Intervention intervention, long endTs, int nPatients) {
		super(id, description + " " + intervention.getDescription(), unit, 0L, endTs);
		this.cloned = false;
		this.intervention = intervention;
		this.nPatients = nPatients;
		this.generatedPatients = new Patient[nPatients];
	}

	/**
	 * 
	 * @param original
	 * @param intervention
	 */
	public HTASimulation(HTASimulation original, Intervention intervention) {
		super(original.id, original.description + " " + intervention.getDescription(), original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.cloned = true;
		this.intervention = intervention;
		this.nPatients = original.nPatients;
		this.generatedPatients = new Patient[nPatients];
	}
	
	/**
	 * @return the nPatients
	 */
	public int getnPatients() {
		return nPatients;
	}

	/**
	 * Returns the counter of patients created
	 * @return the counter of patients created
	 */
	public int getPatientCounter() {
		return patientCounter++;
	}

	/**
	 * @return False if this is a copy of another simulation; true otherwise
	 */
	public boolean isCloned() {
		return cloned;
	}

	/**
	 * 
	 * @return The intervention being analyzed with this simulation
	 */
	public Intervention getIntervention() {
		return intervention;
	}
	
	/**
	 * Adds a new patient
	 * @param pat A patient
	 * @param index Order of the patient
	 */
	public void addGeneratedPatient(Patient pat, int index) {
		generatedPatients[index] = pat;
	}

	/**
	 * Returns the specified generated patient
	 * @param index Order of the patient
	 * @return the specified generated patient; null if the index is not valid
	 */
	public Patient getGeneratedPatient(int index) {
		return (index < 0 || index >= nPatients) ? null : generatedPatients[index];
	}
	
	@Override
	public void end() {
		super.end();
	}
}
