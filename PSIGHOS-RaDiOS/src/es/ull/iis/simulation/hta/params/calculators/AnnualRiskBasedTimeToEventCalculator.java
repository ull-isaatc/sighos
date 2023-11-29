/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRiskBasedTimeToEventCalculator implements ParameterCalculator {
	/** Relative risk calculator */
	private final RRCalculator rr;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
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
	public AnnualRiskBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, DiseaseProgression destManifestation, RRCalculator rr) {
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
	public AnnualRiskBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, DiseaseProgression destManifestation) {
		this(paramName, secParams, destManifestation, SecondOrderParamsRepository.NO_RR);
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEvent(secParams.getParameterValue(paramName, pat), rndValue, rr.getRR(pat));
	}		
}
