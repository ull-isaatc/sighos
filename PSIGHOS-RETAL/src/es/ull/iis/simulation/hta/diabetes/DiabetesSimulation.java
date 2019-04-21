/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatientGenerator.DiabetesPatientGenerationInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.CommonParams;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The simulation class for an intervention on T1DM patients
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabetesSimulation extends HTASimulation {
	private final static String DESCRIPTION = "T1DM Simulation";
	/** Common parameters of the simulation to be used by simulated patients */
	private final CommonParams commonParams;

	/**
	 * Creates a new simulation for T1DM patients
	 * @param id Identifier of the simulation
	 * @param intervention Simulated intervention
	 * @param nPatients Amount of patients to create
	 * @param commonParams Common parameters
	 */
	public DiabetesSimulation(int id, DiabetesIntervention intervention, int nPatients, CommonParams commonParams, DiabetesPatientGenerationInfo[] population, int timeHorizon) {
		super(id, DESCRIPTION, BasicConfigParams.SIMUNIT, intervention, BasicConfigParams.SIMUNIT.convert(timeHorizon, TimeUnit.YEAR), nPatients);
		this.commonParams = commonParams;
		new DiabetesPatientGenerator(this, nPatients, intervention, population);
	}

	/**
	 * Creates a simulation copy of another one that used a different intervention
	 * @param original Original simulation
	 * @param interventionSimulated intervention
	 */
	public DiabetesSimulation(DiabetesSimulation original, DiabetesIntervention intervention) {
		super(original, intervention);
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

}
