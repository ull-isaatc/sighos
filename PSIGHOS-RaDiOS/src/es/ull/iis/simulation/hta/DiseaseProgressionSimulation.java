/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The simulation class for an intervention on patients
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseProgressionSimulation extends Simulation {
	private final static String DESCRIPTION = "Disease progression simulation";
	/** The model simulated */
	private final HTAModel model;
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
	 * @param model The model to be simulated
	 * @param population A collection of populations that will serve to generate patients
	 * @param timeHorizon Duration of the simulation (in years)
	 */
	public DiseaseProgressionSimulation(int id, Intervention intervention, HTAModel model, int timeHorizon) {
		super(id, DESCRIPTION + " " + intervention.getDescription(), SecondOrderParamsRepository.getSimulationTimeUnit(), 0L, SecondOrderParamsRepository.adjustTimeToEvent(timeHorizon, TimeUnit.YEAR));
		this.model = model;
		this.cloned = false;
		this.intervention = intervention;
		this.nPatients = model.getExperiment().getNPatients();
		this.generatedPatients = new Patient[nPatients];	
		new PatientGenerator(this, nPatients, intervention, model.getPopulation());
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
		this.model = original.model;
		new PatientGenerator(this, original.generatedPatients, intervention, model.getPopulation());
	}

	/**
	 * Returns the model to be simulated
	 * @return the model to be simulated
	 */
	public HTAModel getModel() {
		return model;
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
