/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MyocardialInfarction extends DiseaseProgression {
	private static final double ANNUAL_COST = 948;
	private static final double TRANS_COST = 23536 - ANNUAL_COST;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.055, (0.067 - 0.042) / 3.92};
	/** Probability of sudden death after MI (average men-women) */ 
	private static final double P_DEATH = (0.393 + 0.364) / 2;
	public static final String NAME = "MI";
	
	/**
	 * @param model
	 * @param disease
	 */
	public MyocardialInfarction(HTAModel model, Disease disease) {
		super(model, NAME, "Myocardial Infarction", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.ANNUAL_COST, "year 2+ of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, ANNUAL_COST, Parameter.getRandomVariateForCost(ANNUAL_COST));
		addUsedParameter(StandardParameter.ONE_TIME_COST, "episode of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, TRANS_COST, Parameter.getRandomVariateForCost(TRANS_COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH, "", "Core Model", P_DEATH, Parameter.getRandomVariateForProbability(P_DEATH));
		addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "Disutility of " + getDescription(), "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		addUsedParameter(StandardParameter.INCREASED_MORTALITY_RATE, "macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1));
	}

}
