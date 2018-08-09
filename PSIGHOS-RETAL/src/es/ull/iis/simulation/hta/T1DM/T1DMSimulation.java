/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ResourceUsageParams;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams;
import es.ull.iis.simulation.hta.outcome.Cost;
import es.ull.iis.simulation.hta.outcome.LifeExpectancy;
import es.ull.iis.simulation.hta.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMSimulation extends HTASimulation {
	/** Number of interventions that will be compared for a patient. This value is also used to determine the id of the patient */ 
	public final static int NINTERVENTIONS = 2;
	private final static String DESCRIPTION = "T1DM Simulation";

	private final CommonParams commonParams;
	private final ResourceUsageParams resUsageParams;
	private final UtilityParams utilParams;

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public T1DMSimulation(int id, boolean baseCase, Intervention intervention, CommonParams commonParams, ResourceUsageParams resUsageParams, UtilityParams utilParams) {
		super(id, DESCRIPTION, CommonParams.SIMUNIT, baseCase, intervention, new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - commonParams.getInitAge() + 1)), NINTERVENTIONS, CommonParams.NPATIENTS);
		this.commonParams = commonParams;
		this.utilParams = utilParams;
		this.resUsageParams = resUsageParams;
		new T1DMPatientGenerator(this, nPatients, new ConstantFunction(commonParams.getInitAge()), intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		cost = new Cost(this, commonParams.getDiscountRate());
		qaly = new QualityAdjustedLifeExpectancy(this, commonParams.getDiscountRate());		
		ly = new LifeExpectancy(this, commonParams.getDiscountRate());
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public T1DMSimulation(T1DMSimulation original, Intervention intervention) {
		super(original, intervention);
		this.commonParams = original.commonParams;
		commonParams.reset();
		this.utilParams = original.utilParams;
		this.resUsageParams = original.resUsageParams;
		new T1DMPatientGenerator(this, original.generatedPatients, intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		cost = original.cost;
		qaly = original.qaly;
		ly = original.ly;
	}

	public CommonParams getCommonParams() {
		return commonParams;
	}

	/**
	 * @return the utilParams
	 */
	public UtilityParams getUtilParams() {
		return utilParams;
	}

	/**
	 * @return the resUsageParams
	 */
	public ResourceUsageParams getResUsageParams() {
		return resUsageParams;
	}

}
