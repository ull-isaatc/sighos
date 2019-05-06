/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientGenerator.DiabetesPatientGenerationInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.CommonParams;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The simulation class for an intervention on T1DM patients
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabetesSimulation extends Simulation {
	private final static String DESCRIPTION = "T1DM Simulation";
	/** Common parameters of the simulation to be used by simulated patients */
	private final CommonParams commonParams;
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	/** The intervention assessed in this simulation */
	protected final DiabetesIntervention intervention;
	/** Number of patients created in this simulation */
	protected final int nPatients;
	
	/** True if this is a clone of an original simulation; false otherwise */
	protected final boolean cloned;
	/** The list of generated patients */
	protected final DiabetesPatient[] generatedPatients; 

	/**
	 * Creates a new simulation for T1DM patients
	 * @param id Identifier of the simulation
	 * @param intervention Simulated intervention
	 * @param nPatients Amount of patients to create
	 * @param commonParams Common parameters
	 * @param population A collection of populations that will serve to generate patients
	 * @param timeHorizon Duration of the simulation (in years)
	 */
	public DiabetesSimulation(int id, DiabetesIntervention intervention, int nPatients, CommonParams commonParams, DiabetesPatientGenerationInfo[] population, int timeHorizon) {
		super(id, DESCRIPTION + " " + intervention.getDescription(), BasicConfigParams.SIMUNIT, 0L, BasicConfigParams.SIMUNIT.convert(timeHorizon, TimeUnit.YEAR));
		this.commonParams = commonParams;
		this.cloned = false;
		this.intervention = intervention;
		this.nPatients = nPatients;
		this.generatedPatients = new DiabetesPatient[nPatients];	
		new DiabetesPatientGenerator(this, nPatients, intervention, population);
	}

	/**
	 * Creates a simulation copy of another one that used a different intervention
	 * @param original Original simulation
	 * @param interventionSimulated intervention
	 */
	public DiabetesSimulation(DiabetesSimulation original, DiabetesIntervention intervention) {
		super(original.id, original.description + " " + intervention.getDescription(), original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.cloned = true;
		this.intervention = intervention;
		this.nPatients = original.nPatients;
		this.generatedPatients = new DiabetesPatient[nPatients];
		this.commonParams = original.commonParams;
		commonParams.reset();
		new DiabetesPatientGenerator(this, original.generatedPatients, intervention);
	}

	/**
	 * Returns the common parameters used within this simulation
	 * @return the common parameters used within this simulation
	 */
	public CommonParams getCommonParams() {
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
	public DiabetesIntervention getIntervention() {
		return intervention;
	}
	
	/**
	 * Adds a new patient
	 * @param pat A patient
	 * @param index Order of the patient
	 */
	public void addGeneratedPatient(DiabetesPatient pat, int index) {
		generatedPatients[index] = pat;
	}

	/**
	 * Returns the specified generated patient
	 * @param index Order of the patient
	 * @return the specified generated patient; null if the index is not valid
	 */
	public DiabetesPatient getGeneratedPatient(int index) {
		return (index < 0 || index >= nPatients) ? null : generatedPatients[index];
	}
	

}
