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
public class LySevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double P_HYPO = /*0.264;/*/0.234286582;
	private static final double RR_HYPO = /*0.018587361;/*/0.020895447;
	private static final double[] P_HYPO_BETA = /*{26.136, 72.864}; /*/ {23.19437163, 75.80562837};
	private static final double[] RR_HYPO_BETA = /*{-3.985273467, 1.420307792}; /*/ {-3.868224010, 1.421931924};
	
	private static final String DEF_SOURCE = "Ly et al. 2013";

	public LySevereHypoglycemiaEvent() {
		super(
				new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
					DEF_SOURCE, P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])),
				new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
					DEF_SOURCE, RR_HYPO, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))),
				EnumSet.of(DiabetesType.T1)
				);
	}
}
