/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSevereHypoglycemiaEvent extends StandardSevereHypoglycemiaEvent {
	private static final String DEF_SOURCE = "DCCT: https://www.ncbi.nlm.nih.gov/pubmed/90007053";
	private static final double C_HYPO_EPISODE = 3755;		// Assumption
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada

	public DCCTSevereHypoglycemiaEvent() {
		super(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				DEF_SOURCE, 0.187, RandomVariateFactory.getInstance("BetaVariate", 18.513, 80.487)),
				new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
					DEF_SOURCE,	3.27272727, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", 1.1856237, 0.22319455))),
				new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "Assumption", DU_HYPO_EPISODE),
				new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "Assumption", 2018, C_HYPO_EPISODE),
				EnumSet.of(DiabetesType.T1));
	}		
}
