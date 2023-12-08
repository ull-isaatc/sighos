/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRiskBasedTimeToEventParameter extends Parameter {
	/** Relative risk parameter */
	private final String rrParamName;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Name of the second order parameter that defines the annual risk */
	private final String paramName;
	
	/**
	 * 
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the second order parameter that defines the annual risk
	 * @param rrParamName Relative risk calculator
	 */
	public AnnualRiskBasedTimeToEventParameter(SecondOrderParamsRepository secParams, String paramName, DiseaseProgression destManifestation, String riskParamName, String rrParamName) {
		super(secParams, paramName);
		this.rrParamName = rrParamName;
		this.destManifestation = destManifestation;
		this.paramName = riskParamName;
	}
	
	/**
	 * 
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param paramName Name of the second order parameter that defines the annual risk
	 */
	public AnnualRiskBasedTimeToEventParameter(SecondOrderParamsRepository secParams, String paramName, DiseaseProgression destManifestation, String riskParamName) {
		this(secParams, paramName, destManifestation, riskParamName, secParams.NO_RR);
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEvent(getRepository().getParameterValue(paramName, pat), rndValue, getRepository().getParameterValue(rrParamName, pat));
	}		
}
