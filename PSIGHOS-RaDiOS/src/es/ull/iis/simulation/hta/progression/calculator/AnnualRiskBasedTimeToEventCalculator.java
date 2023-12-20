/**
 * 
 */
package es.ull.iis.simulation.hta.progression.calculator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;

/**
 * Calculates years to an event based on annual risk. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRiskBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Incidence rate ratio calculator */
	private final String rrParamName;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Name of the parameter that defines the patients-year rate */
	private final String riskParamName;
	
	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the parameter that defines the annual risk
	 * @param rrParamName Name of the parameter that defines the relative riek
	 */
	public AnnualRiskBasedTimeToEventCalculator(DiseaseProgression destManifestation, String riskParamName, String rrParamName) {
		this.rrParamName = rrParamName;
		this.destManifestation = destManifestation;
		this.riskParamName = riskParamName;
	}
	
	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the parameter that defines the annual risk
	 */
	public AnnualRiskBasedTimeToEventCalculator(DiseaseProgression destManifestation, String riskParamName) {
		this(destManifestation, riskParamName, null);
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.YEAR;
	}
	
	@Override
	public double getTimeToEvent(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		final double rrValue = (rrParamName == null) ? 1.0 : destManifestation.getModel().getParameterValue(rrParamName, pat);
		return Statistics.getAnnualBasedTimeToEvent(destManifestation.getModel().getParameterValue(riskParamName, pat), rndValue, rrValue);
	}		
}
