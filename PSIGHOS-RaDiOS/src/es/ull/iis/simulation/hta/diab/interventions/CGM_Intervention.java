/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.T1DMRepository;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.Modification.Type;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class CGM_Intervention extends Intervention {
	private static final String NAME = "CGM";
	/** Cost of sensor for DEXCOM G5( */
	private static final double C_SENSOR_G5 = 50;
	/** Daily use of sensor: sensors are changed after 7 days */
	private static final double USE_SENSOR_G5 = 1.0/7.0;
	private static final String STR_USE_SENSOR_G5 = "DAILY_USE_SENSOR";
	private static final double []USE_STRIPS = {3.6, 1.6};
	private static final double []MIN_MAX_USE_STRIPS = {1.0, 8.0};
	private static final String STR_USE_STRIPS = "DAILY_USE_STRIPS_" + NAME;
	private static final String STR_C_STRIPS = "STRIPS_" + NAME;
	private static final int YEAR_C_STRIPS = 2019;
	private static final double []C_STRIPS = {0.281378955, 0.098554232};
	private static final double []MIN_MAX_C_STRIPS = {0.1, 0.5};
	private static final double HBA1C_REDUCTION = 0.49;
	private static final double []HBA1C_REDUCTION_IC95 = {0.375, 0.605};
	
	/**
	 * @param secParams
	 */
	public CGM_Intervention(SecondOrderParamsRepository secParams) {
		super(secParams, NAME, NAME);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		double mode = Statistics.betaModeFromMeanSD(USE_STRIPS[0], USE_STRIPS[1]);
		double[] betaParams = Statistics.betaParametersFromEmpiricData(USE_STRIPS[0], mode, MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[1]);
		RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		secParams.addOtherParam(new SecondOrderParam(secParams, STR_USE_STRIPS, "Use of strips in " + getDescription(), 
				"DIAMOND 10.1001/jama.2016.19975", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
		
		mode = Statistics.betaModeFromMeanSD(C_STRIPS[0], C_STRIPS[1]);
		betaParams = Statistics.betaParametersFromEmpiricData(C_STRIPS[0], mode, MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[1]);
		rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, STR_C_STRIPS, "strips", "Average from Spanish regions", 
				YEAR_C_STRIPS, C_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_C_STRIPS[1] - MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[0]));

		// I assume a daily use with +-25% uncertainty
		secParams.addOtherParam(new SecondOrderParam(secParams, STR_USE_SENSOR_G5, "Use of sensor", 
				"Technical data sheets", USE_SENSOR_G5, RandomVariateFactory.getInstance("UniformVariate", 0.75 * USE_SENSOR_G5, 1.25 * USE_SENSOR_G5)));
		
		final double sd = Statistics.sdFrom95CI(HBA1C_REDUCTION_IC95);
		addClinicalParameterModification(T1DMRepository.STR_HBA1C, new Modification(secParams, Type.DIFF, SecondOrderParamsRepository.getModificationString(this, T1DMRepository.STR_HBA1C + "_REDUX"), T1DMRepository.STR_HBA1C + " reduction",
				"GOLD+DIAMOND", HBA1C_REDUCTION, RandomVariateFactory.getInstance("NormalVariate", HBA1C_REDUCTION, sd)));
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		final SecondOrderParamsRepository secParams = getRepository();
		return discountRate.applyDiscount((CostParamDescriptions.ANNUAL_COST.getValue(secParams, STR_C_STRIPS, pat.getSimulation()) * secParams.getOtherParam(STR_USE_STRIPS, 0.0, pat.getSimulation()) +
				C_SENSOR_G5 * secParams.getOtherParam(STR_USE_SENSOR_G5, 0.0, pat.getSimulation())) * 365, initT, endT);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0.0;
	}

}
