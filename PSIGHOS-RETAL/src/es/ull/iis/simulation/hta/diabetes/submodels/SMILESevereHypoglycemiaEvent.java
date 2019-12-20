/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SMILESevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double P_HYPO = 0.521;
	private static final double RR_HYPO = 0.163147793;
	private static final double[] P_HYPO_BETA = {51.579, 47.421};
	private static final double[] RR_HYPO_BETA = {-1.813098785, 0.341820006};
	
	private static final double DU_HYPO_EPISODE = 0.0631; // From Walters et al.
	private static final double[] LIMITS_DU_HYPO_EPISODE = {DU_HYPO_EPISODE - 0.03, DU_HYPO_EPISODE + 0.03}; // Assumption
	private static final String DEF_SOURCE = "SMILE";

	public SMILESevereHypoglycemiaEvent() {
		super(
			new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
			new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))),
			new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", 
				DU_HYPO_EPISODE, "UniformVariate", LIMITS_DU_HYPO_EPISODE[0], LIMITS_DU_HYPO_EPISODE[1]),
			StandardSevereHypoglycemiaEvent.getDefaultCostParameter(),
			StandardSevereHypoglycemiaEvent.getDefaultMortalityParameter(),
			EnumSet.of(DiabetesType.T1)
			);
	}

}
