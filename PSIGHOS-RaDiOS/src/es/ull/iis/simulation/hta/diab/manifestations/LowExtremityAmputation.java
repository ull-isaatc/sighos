/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LowExtremityAmputation extends DiseaseProgression {
	private static final double COST = 918.01;
	/** [Avg, SD] cost of amputation, from Spanish national tariffs */
	private static final double[] TC = {11333.04, 1674.37};
	private static final int COSTYEAR = 2017;
	// Avg, SD
	private static final double[] DU = new double[] {0.28, (0.389 - 0.17) / 3.92};
	public static final String NAME = "LEA";
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public LowExtremityAmputation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Low extremity amputation", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "del Pino et al", COSTYEAR, COST, StandardParameter.getRandomVariateForCost(COST));
		final double[] tcParams = Statistics.gammaParametersFromNormal(TC[0], TC[1]);
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "amputation", 
				"Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", COSTYEAR, 
				TC[0], RandomVariateFactory.getInstance("GammaVariate", tcParams[0], tcParams[1]));

		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, this.name(), "peripheral neuropathy (amputation)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1));
	}

}
