/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The simulation class for an intervention on patients
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseProgressionSimulation extends Simulation {
	private final static String DESCRIPTION = "Disease progression simulation";
	/** Common parameters of the simulation to be used by simulated patients */
	private final RepositoryInstance commonParams;
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	/** The intervention assessed in this simulation */
	protected final Intervention intervention;
	/** Number of patients created in this simulation */
	protected final int nPatients;
	
	/** True if this is a clone of an original simulation; false otherwise */
	protected final boolean cloned;
	/** The list of generated patients */
	protected final Patient[] generatedPatients; 

	/**
	 * Creates a new simulation for patients
	 * @param id Identifier of the simulation
	 * @param intervention Simulated intervention
	 * @param nPatients Amount of patients to create
	 * @param commonParams Common parameters
	 * @param population A collection of populations that will serve to generate patients
	 * @param timeHorizon Duration of the simulation (in years)
	 */
	public DiseaseProgressionSimulation(int id, Intervention intervention, int nPatients, RepositoryInstance commonParams, Population population, int timeHorizon) {
		super(id, DESCRIPTION + " " + intervention.getDescription(), BasicConfigParams.SIMUNIT, 0L, BasicConfigParams.SIMUNIT.convert(timeHorizon, TimeUnit.YEAR));
		this.commonParams = commonParams;
		this.cloned = false;
		this.intervention = intervention;
		this.nPatients = nPatients;
		this.generatedPatients = new Patient[nPatients];	
		new PatientGenerator(this, nPatients, intervention, population);
	}

	/**
	 * Creates a simulation copy of another one that used a different intervention
	 * @param original Original simulation
	 * @param interventionSimulated intervention
	 */
	public DiseaseProgressionSimulation(DiseaseProgressionSimulation original, Intervention intervention) {
		super(original.id, original.description + " " + intervention.getDescription(), original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.cloned = true;
		this.intervention = intervention;
		this.nPatients = original.nPatients;
		this.generatedPatients = new Patient[nPatients];
		this.commonParams = original.commonParams;
		commonParams.reset();
		new PatientGenerator(this, original.generatedPatients, intervention);
	}

	/**
	 * Returns the common parameters used within this simulation
	 * @return the common parameters used within this simulation
	 */
	public RepositoryInstance getCommonParams() {
		return commonParams;
	}

	/**
	 * Returns the counter of patients created
	 * @return the counter of patients created
	 */
	public int getPatientCounter() {
		return patientCounter++;
	}

	/**
	 * Returns true if this is a copy of another simulation; false otherwise
	 * @return True if this is a copy of another simulation; false otherwise
	 */
	public boolean isCloned() {
		return cloned;
	}

	/**
	 * Returns the intervention being analyzed with this simulation
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

	/**
	 * Returns the complete list of generated patients 
	 * @return the complete list of generated patients
	 */
	public Patient[] getGeneratedPatients() {
		return generatedPatients;
	}
}
