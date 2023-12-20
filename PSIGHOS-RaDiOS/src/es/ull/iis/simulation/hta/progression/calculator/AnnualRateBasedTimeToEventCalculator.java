/**
 * 
 */
package es.ull.iis.simulation.hta.progression.calculator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;

/**
 * Calculates years to an event based on patients-year rate. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRateBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Incidence rate ratio calculator */
	private final String irrParamName;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Name of the parameter that defines the patients-year rate */
	private final String rateParamName;

	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param rateParamName Name of the parameter that defines the patients-year rate
	 * @param irrParamName Name of the parameter that defines the incidence rate ratio
	 */
	public AnnualRateBasedTimeToEventCalculator(DiseaseProgression destManifestation, String rateParamName, String irrParamName) {
		this.irrParamName = irrParamName;
		this.destManifestation = destManifestation;
		this.rateParamName = rateParamName;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.YEAR;
	}

	@Override
	public double getTimeToEvent(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEventFromRate(destManifestation.getModel().getParameterValue(rateParamName, pat), rndValue, destManifestation.getModel().getParameterValue(irrParamName, pat));
	}		
}
