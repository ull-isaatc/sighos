/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRateBasedTimeToEventCalculator implements ParameterCalculator {
	/** Incidence rate ratio calculator */
	private final ParameterCalculator irrCalculator;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Repository of second order parameters */
	private final SecondOrderParamsRepository secParams;
	/** Name of the second order parameter that defines the patients-year rate */
	private final String paramName;

	/**
	 * 
	 * @param paramName Name of the second order parameter that defines the patients-year rate
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param irrCalculator Incidence rate ratio calculator
	 */
	public AnnualRateBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, DiseaseProgression destManifestation, ParameterCalculator irrCalculator) {
		this.irrCalculator = irrCalculator;
		this.secParams = secParams;
		this.destManifestation = destManifestation;
		this.paramName = paramName;
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEventFromRate(secParams.getParameterValue(paramName, pat), rndValue, irrCalculator.getValue(pat));
	}		
}
