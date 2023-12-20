/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Stroke extends DiseaseProgression {
	private static final double ANNUAL_COST = 2485.66;
	private static final double TRANS_COST = 6120.32 - ANNUAL_COST;
	private static final int COSTYEAR = 2016;
	private static final double[] DU = new double[] {0.164, (0.222 - 0.105) / 3.92};
	/** Probability of 30-day death after stroke */ 
	private static final double P_DEATH = 0.124;
	public static final String NAME = "STROKE";
	
	/**
	 * @param model
	 * @param disease
	 */
	public Stroke(HTAModel model, Disease disease) {
		super(model, NAME, "Stroke", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.ANNUAL_COST, "year 2+ of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, ANNUAL_COST, StandardParameter.getRandomVariateForCost(ANNUAL_COST));
		addUsedParameter(StandardParameter.ONE_TIME_COST, "episode of " + getDescription(), "https://doi.org/10.1016/j.endinu.2018.03.008", COSTYEAR, TRANS_COST, StandardParameter.getRandomVariateForCost(TRANS_COST));
		final double[] paramsDu = Statistics.betaParametersFromNormal(DU[0], DU[1]);
		addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "Disutility of " + getDescription(), "Bagust and Beale", DU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDu[0], paramsDu[1]));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH, "", "Core Model", P_DEATH, StandardParameter.getRandomVariateForProbability(P_DEATH));
		addUsedParameter(StandardParameter.INCREASED_MORTALITY_RATE, "macrovascular disease", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1));
	}

}
