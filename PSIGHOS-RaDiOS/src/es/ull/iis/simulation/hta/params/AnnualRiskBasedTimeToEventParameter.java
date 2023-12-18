/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRiskBasedTimeToEventParameter extends Parameter {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	public enum USED_PARAMETERS implements UsedParameter {
		PROB,
		RR
	}

	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the second order parameter that defines the annual risk
	 * @param rrParamName Relative risk calculator
	 */
	public AnnualRiskBasedTimeToEventParameter(HTAModel model, String paramName, String description, String source, int year, DiseaseProgression destManifestation) {
		super(model, paramName, description, source, year, ParameterType.RISK);
		this.destManifestation = destManifestation;
		setUsedParameterName(USED_PARAMETERS.PROB, StandardParameter.PROBABILITY.createName(destManifestation));
		setUsedParameterName(USED_PARAMETERS.RR, StandardParameter.RELATIVE_RISK.createName(destManifestation));
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEvent(destManifestation.getModel().getParameterValue(getUsedParameterName(USED_PARAMETERS.PROB), pat), rndValue, 
					destManifestation.getModel().getParameterValue(getUsedParameterName(USED_PARAMETERS.RR), pat));
	}		
}
