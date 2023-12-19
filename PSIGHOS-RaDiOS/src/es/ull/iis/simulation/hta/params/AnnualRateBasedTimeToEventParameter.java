/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRateBasedTimeToEventParameter extends Parameter {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;

	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param rateParamName Name of the second order parameter that defines the patients-year rate
	 * @param irrParamName Incidence rate ratio calculator
	 */
	public AnnualRateBasedTimeToEventParameter(HTAModel model, String paramName, String description, String source, int year, DiseaseProgression destManifestation, String rateParamName, String irrParamName) {
		super(model, paramName, description, source, year, ParameterType.RISK);
		this.destManifestation = destManifestation;
		addUsedParameter(StandardParameter.RATE);
		addUsedParameter(StandardParameter.INCIDENCE_RATE_RATIO);		
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEventFromRate(destManifestation.getUsedParameterValue(StandardParameter.RATE, pat), rndValue, destManifestation.getUsedParameterValue(StandardParameter.INCIDENCE_RATE_RATIO, pat));
	}		
}
