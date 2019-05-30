/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SMILESevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double P_HYPO = 0.521;
	private static final double RR_HYPO = 0.163147793;
	private static final double[] P_HYPO_BETA = {51.579, 47.421};
	private static final double[] RR_HYPO_BETA = {-1.813098785, 0.341820006};
	
	private static final double DU_HYPO_EPISODE = BasicConfigParams.USE_REVIEW_UTILITIES ? (0.047 / 3.0) : 0.0206; // From Canada
	private static final double[] LIMITS_DU_HYPO_EPISODE = {(BasicConfigParams.USE_REVIEW_UTILITIES ? 0.035 : 0.01) / 3.0, (BasicConfigParams.USE_REVIEW_UTILITIES ? 0.059 : 0.122) / 3.0}; // From Canada
	private static final String DEF_SOURCE = "SMILE";

	public SMILESevereHypoglycemiaEvent() {
		super(
			new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
			new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))),
			new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", 
				DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]),
			new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", 
				"https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, SecondOrderParamsRepository.getRandomVariateForCost(716.82))
			);
	}

}
