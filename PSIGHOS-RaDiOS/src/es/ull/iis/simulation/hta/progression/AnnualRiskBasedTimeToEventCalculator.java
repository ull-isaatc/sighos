/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class AnnualRiskBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Relative risk calculator */
	private final RRCalculator rr;
	/** Manifestation to which progress */
	private final Manifestation destManifestation;
	/** Repository of second order parameters */
	private final SecondOrderParamsRepository secParams;
	/** Name of the second order parameter that defines the annual risk */
	private final String paramName;
	
	/**
	 * 
	 * @param paramName Name of the second order parameter that defines the annual risk
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param rr Relative risk calculator
	 */
	public AnnualRiskBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, Manifestation destManifestation, RRCalculator rr) {
		this.rr = rr;
		this.secParams = secParams;
		this.destManifestation = destManifestation;
		this.paramName = paramName;
	}
	
	/**
	 * 
	 * @param paramName Name of the second order parameter that defines the annual risk
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 */
	public AnnualRiskBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, Manifestation destManifestation) {
		this(paramName, secParams, destManifestation, SecondOrderParamsRepository.NO_RR);
	}

	@Override
	public long getTimeToEvent(Patient pat) {
		final double rndValue = destManifestation.getRandomValue(pat);
		return SecondOrderParamsRepository.getAnnualBasedTimeToEvent(pat, 
				secParams.getProbParam(paramName, pat.getSimulation()), Math.log(rndValue), rr.getRR(pat));
	}		
}
