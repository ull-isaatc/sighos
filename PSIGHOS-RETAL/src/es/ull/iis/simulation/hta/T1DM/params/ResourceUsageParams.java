/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageParams extends ModelParams {

	private final double[] annualComplicationCosts;
	private final double[] transitionComplicationCosts;
	private final double costNoComplication;
	private final double costHypoglycemicEvent;
	private final double[] annualInterventionCosts;
	private final boolean isDetailedCHD; 
	private final double[] annualCHDComplicationCosts;
	private final double[] transitionCHDComplicationCosts;
	/**
	 * @param secondOrder
	 */
	public ResourceUsageParams(SecondOrderParams secParams) {
		super();
		costHypoglycemicEvent = secParams.getCostForSevereHypoglycemicEpisode();
		costNoComplication = secParams.getAnnualNoComplicationCost();
		annualComplicationCosts = secParams.getAnnualComplicationCost();
		transitionComplicationCosts = secParams.getTransitionComplicationCost();
		annualInterventionCosts = secParams.getAnnualInterventionCost();
		isDetailedCHD = secParams.isDetailedCHD();
		annualCHDComplicationCosts = secParams.getAnnualCHDComplicationCost(); 
		transitionCHDComplicationCosts = secParams.getTransitionCHDComplicationCost();
	}

	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = annualInterventionCosts[pat.getnIntervention()];
		final EnumSet<Complication> state = pat.getState();
		// No complications
		if (state.isEmpty()) {
			cost = costNoComplication;
		}
		else {
			if (isDetailedCHD && state.contains(Complication.CHD)) {
				for (Complication comp : state) {
					if (Complication.CHD.equals(comp)) {
						cost += annualCHDComplicationCosts[pat.getCHDComplication().ordinal()];
					}
					else {
						cost += annualComplicationCosts[comp.ordinal()];
					}
				}				
			}
			else {
				for (Complication comp : state) {
					cost += annualComplicationCosts[comp.ordinal()];
				}
			}
		}
		return cost;
	}
	
	public double getCostOfComplication(T1DMPatient pat, Complication comp) {
		if (isDetailedCHD && Complication.CHD.equals(comp)) 
			return transitionCHDComplicationCosts[pat.getCHDComplication().ordinal()];
		return transitionComplicationCosts[comp.ordinal()];
	}
	
	public double getCostForSevereHypoglycemicEpisode(T1DMPatient pat) {
		return costHypoglycemicEvent;
	}

}
