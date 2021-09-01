/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * Calculates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class AnnualRateBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Incidence rate ratio calculator */
	private final RRCalculator irr;
	/** Manifestation to which progress */
	private final Manifestation destManifestation;
	/** Repository of second order parameters */
	private final SecondOrderParamsRepository secParams;
	/** Name of the second order parameter that defines the patients-year rate */
	private final String paramName;

	/**
	 * 
	 * @param paramName Name of the second order parameter that defines the patients-year rate
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param irr Incidence rate ratio calculator
	 */
	public AnnualRateBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, Manifestation destManifestation, RRCalculator irr) {
		this.irr = irr;
		this.secParams = secParams;
		this.destManifestation = destManifestation;
		this.paramName = paramName;
	}

	@Override
	public long getTimeToEvent(Patient pat) {
		final double rndValue = destManifestation.getRandomValue(pat);
		return SecondOrderParamsRepository.getAnnualBasedTimeToEventFromRate(pat, secParams.getProbParam(paramName, pat.getSimulation()), 
				Math.log(rndValue), irr.getRR(pat));
	}		
}