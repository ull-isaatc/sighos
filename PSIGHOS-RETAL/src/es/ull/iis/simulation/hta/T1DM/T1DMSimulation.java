/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMSimulation extends HTASimulation {
	private final static String DESCRIPTION = "T1DM Simulation";

	private final CommonParams commonParams;

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public T1DMSimulation(int id, boolean baseCase, T1DMMonitoringIntervention intervention, int nPatients, CommonParams commonParams) {
		super(id, DESCRIPTION, BasicConfigParams.SIMUNIT, baseCase, intervention, new TimeStamp(TimeUnit.YEAR, (long) (BasicConfigParams.MAX_AGE - BasicConfigParams.MIN_AGE + 1)), commonParams.getInterventions().length, nPatients);
		this.commonParams = commonParams;
		new T1DMPatientGenerator(this, nPatients, intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public T1DMSimulation(T1DMSimulation original, T1DMMonitoringIntervention intervention) {
		super(original, intervention);
		this.commonParams = original.commonParams;
		commonParams.reset();
		new T1DMPatientGenerator(this, original.generatedPatients, intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
	}

	public CommonParams getCommonParams() {
		return commonParams;
	}

}
