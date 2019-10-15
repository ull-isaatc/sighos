/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final double[] paramsHypo = Statistics.betaParametersFromNormal(0.0982, Statistics.sdFrom95CI(new double[]{0.0526, 0.1513}));
	
	private static final double C_HYPO_EPISODE = 3755;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final String DEF_SOURCE = "Canada";

	public CanadaSevereHypoglycemiaEvent() {
		super(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode", DEF_SOURCE, 0.0982, RandomVariateFactory.getInstance("BetaVariate", paramsHypo[0], paramsHypo[1])),
				new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch", DEF_SOURCE, 0.869, RandomVariateFactory.getInstance("RRFromLnCIVariate", 0.869, 0.476, 1.586, 1)),
				new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", DEF_SOURCE, DU_HYPO_EPISODE),
				new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", DEF_SOURCE, 2018, C_HYPO_EPISODE),
				EnumSet.of(DiabetesType.T1));
	}
}
