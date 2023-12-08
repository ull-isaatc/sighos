/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRateBasedTimeToEventParameter extends Parameter {
	/** Incidence rate ratio calculator */
	private final String irrParamName;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Name of the second order parameter that defines the patients-year rate */
	private final String paramName;

	/**
	 * 
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param rateParamName Name of the second order parameter that defines the patients-year rate
	 * @param irrParamName Incidence rate ratio calculator
	 */
	public AnnualRateBasedTimeToEventParameter(SecondOrderParamsRepository secParams, String paramName, DiseaseProgression destManifestation, String rateParamName, String irrParamName) {
		super(secParams, paramName);
		this.irrParamName = irrParamName;
		this.destManifestation = destManifestation;
		this.paramName = rateParamName;
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEventFromRate(getRepository().getParameterValue(paramName, pat), rndValue, getRepository().getParameterValue(irrParamName, pat));
	}		
}
