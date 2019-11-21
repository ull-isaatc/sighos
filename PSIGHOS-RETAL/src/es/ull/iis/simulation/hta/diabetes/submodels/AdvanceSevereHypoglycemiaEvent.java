/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import simkit.random.RandomVariateFactory;

/**
 * An hypoglycemic event, according to the Advance study
 * @author Iván Castilla Rodríguez
 *
 */
public class AdvanceSevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double P_HYPO = 0.0040;
	private static final double RR_HYPO = 1.75;
	private static final double[] P_HYPO_BETA = {0.3960, 98.6040};
	private static final double[] RR_HYPO_BETA = {0.559615788, 1.977010731};
	
	private static final String DEF_SOURCE = "Advance study. 10.1056/NEJMoa0802987";

	public AdvanceSevereHypoglycemiaEvent() {
		super(
				new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-year)", 
					DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
				new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-year)", 
					DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))),
				EnumSet.of(DiabetesType.T2)
				);
	}
}
