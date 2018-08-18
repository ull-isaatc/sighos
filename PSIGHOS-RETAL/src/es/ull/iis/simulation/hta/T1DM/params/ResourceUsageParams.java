/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * Model parameters related to costs. 
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageParams extends ModelParams {

	/** Annual cost of complications */
	private final double[] annualComplicationCosts;
	/** Cost of complications upon incidence */
	private final double[] transitionComplicationCosts;
	/** Cost of diabetes with no complications */ 
	private final double costNoComplication;
	/** Cost of a severe hypoglycemic event */
	private final double costHypoglycemicEvent;
	/** When true, the progression of CHD is detailed, so are the costs */
	private final boolean isDetailedCHD;
	/** Annual cost of different CHD complications (angina, heart failure, stroke, myocardial infarction) */
	private final double[] annualCHDComplicationCosts;
	/** Cost of different CHD complications (angina, heart failure, stroke, myocardial infarction) upon incidence */
	private final double[] transitionCHDComplicationCosts;
	
	/**
	 * Creates an instance of the model parameters related to costs.
	 * @param secParams Second order parameters to be used
	 */
	public ResourceUsageParams(SecondOrderParams secParams) {
		super();
		costHypoglycemicEvent = secParams.getCostForSevereHypoglycemicEpisode();
		costNoComplication = secParams.getAnnualNoComplicationCost();
		annualComplicationCosts = secParams.getAnnualComplicationCost();
		transitionComplicationCosts = secParams.getTransitionComplicationCost();
		isDetailedCHD = secParams.isDetailedCHD();
		annualCHDComplicationCosts = secParams.getAnnualCHDComplicationCost(); 
		transitionCHDComplicationCosts = secParams.getTransitionCHDComplicationCost();
	}

	/**
	 * Return the annual cost for the specified patient during a period of time. The initAge and endAge parameters
	 * can be used to select different frequency of treatments according to the age of the patient 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the annual cost for the specified patient during a period of time.
	 */
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = ((T1DMMonitoringIntervention)pat.getIntervention()).getAnnualCost(pat);
		final EnumSet<Complication> state = pat.getState();
		// No complications
		if (state.isEmpty()) {
			cost += costNoComplication;
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
	
	/**
	 * Returns the cost of a complication upon incidence.
	 * @param pat A patient
	 * @param comp A new complication for the patient
	 * @return the cost of a complication upon incidence
	 */
	public double getCostOfComplication(T1DMPatient pat, Complication comp) {
		if (isDetailedCHD && Complication.CHD.equals(comp)) 
			return transitionCHDComplicationCosts[pat.getCHDComplication().ordinal()];
		return transitionComplicationCosts[comp.ordinal()];
	}
	
	/**
	 * Returns the cost of a severe hypoglycemic episode
	 * @param pat A patient
	 * @return the cost of a severe hypoglycemic episode
	 */
	public double getCostForSevereHypoglycemicEpisode(T1DMPatient pat) {
		return costHypoglycemicEvent;
	}

}
