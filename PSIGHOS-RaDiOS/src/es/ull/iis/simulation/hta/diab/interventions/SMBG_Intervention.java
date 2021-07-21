/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla
 *
 */
public class SMBG_Intervention extends Intervention {
	private static final String NAME = "SMBG";
	private static final double []USE_STRIPS = {4.6, 1.6};
	private static final double []MIN_MAX_USE_STRIPS = {3.0, 10.0};
	private static final String STR_USE_STRIPS = "DAILY_USE_STRIPS_" + NAME;
	private static final String STR_C_STRIPS = SecondOrderParamsRepository.STR_COST_PREFIX + "STRIPS_" + NAME;
	private static final int YEAR_C_STRIPS = 2019;
	private static final double []C_STRIPS = {0.281378955, 0.098554232};
	private static final double []MIN_MAX_C_STRIPS = {0.1, 0.5};
	
	/**
	 * @param secParams
	 */
	public SMBG_Intervention(SecondOrderParamsRepository secParams) {
		super(secParams, NAME, NAME);
	}

	@Override
	public void registerSecondOrderParameters() {
		double mode = Statistics.betaModeFromMeanSD(USE_STRIPS[0], USE_STRIPS[1]);
		double[] betaParams = Statistics.betaParametersFromEmpiricData(USE_STRIPS[0], mode, MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[1]);
		RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		secParams.addOtherParam(new SecondOrderParam(secParams, STR_USE_STRIPS, "Use of strips in " + getDescription(), 
				"DIAMOND 10.1001/jama.2016.19975", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0])));
		
		mode = Statistics.betaModeFromMeanSD(C_STRIPS[0], C_STRIPS[1]);
		betaParams = Statistics.betaParametersFromEmpiricData(C_STRIPS[0], mode, MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[1]);
		rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		secParams.addCostParam(new SecondOrderCostParam(secParams, STR_C_STRIPS, "Annual cost of strips", 
				"Average from Spanish regions", YEAR_C_STRIPS, C_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_C_STRIPS[1] - MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[0])));
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return secParams.getCostParam(STR_C_STRIPS, pat.getSimulation()) * secParams.getOtherParam(STR_USE_STRIPS, 0.0, pat.getSimulation()) * 365;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0.0;
	}

}