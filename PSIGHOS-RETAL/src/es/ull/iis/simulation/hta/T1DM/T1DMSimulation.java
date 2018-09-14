/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The simulation class for an intervention on T1DM patients
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMSimulation extends HTASimulation {
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
	public T1DMSimulation(int id, T1DMMonitoringIntervention intervention, int nPatients, CommonParams commonParams) {
		super(id, DESCRIPTION, BasicConfigParams.SIMUNIT, intervention, new TimeStamp(TimeUnit.YEAR, (long) (BasicConfigParams.MAX_AGE - BasicConfigParams.MIN_AGE + 1)), commonParams.getInterventions().length, nPatients);
		this.commonParams = commonParams;
		new T1DMPatientGenerator(this, nPatients, intervention);
	}

	/**
	 * Creates a simulation copy of another one that used a different intervention
	 * @param original Original simulation
	 * @param interventionSimulated intervention
	 */
	public T1DMSimulation(T1DMSimulation original, T1DMMonitoringIntervention intervention) {
		super(original, intervention);
		this.commonParams = original.commonParams;
		commonParams.reset();
		new T1DMPatientGenerator(this, original.generatedPatients, intervention);
	}

	/**
	 * Returns the common parameters used within this simulation
	 * @return the common parameters used within this simulation
	 */
	public CommonParams getCommonParams() {
		return commonParams;
	}

}
