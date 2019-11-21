/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import simkit.random.RandomVariateFactory;

/**
 * An hypoglycemic event, according to the study of Battelino et al.
 * @author Iván Castilla Rodríguez
 *
 */
public class BattelinoSevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double P_HYPO = BasicConfigParams.ENABLE_BATTELINO_HYPO_SCENARIO_1 ? 0.027025776 : 0.0408163;
	private static final double RR_HYPO = BasicConfigParams.ENABLE_BATTELINO_HYPO_SCENARIO_1 ? 1.98630137 : 1.0;
	private static final double[] P_HYPO_BETA = {3.94576334, 142.05423666};
	private static final double[] RR_HYPO_BETA = {0.68627430, 0.60401244};
	
	private static final String DEF_SOURCE = "Battelino et al. 2012";

	public BattelinoSevereHypoglycemiaEvent() {
		super(
				new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
					DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
				new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
					DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))),
				EnumSet.of(DiabetesType.T1)
				);
	}
}
