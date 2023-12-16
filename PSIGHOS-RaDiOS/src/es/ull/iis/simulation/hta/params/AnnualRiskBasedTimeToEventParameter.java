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
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the second order parameter that defines the annual risk
	 * @param rrParamName Relative risk calculator
	 */
	public AnnualRiskBasedTimeToEventParameter(String paramName, String description, String source, int year, DiseaseProgression destManifestation, String riskParamName, String rrParamName) {
		super(paramName, description, source, year, ParameterType.RISK);
		this.rrParamName = rrParamName;
		this.destManifestation = destManifestation;
		this.paramName = riskParamName;
	}
	
	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param paramName Name of the second order parameter that defines the annual risk
	 */
	public AnnualRiskBasedTimeToEventParameter(String paramName, String description, String source, int year, DiseaseProgression destManifestation, String riskParamName) {
		this(paramName, description, source, year, destManifestation, riskParamName, Parameter.NO_RR.name());
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEvent(destManifestation.getModel().getParameterValue(paramName, pat), rndValue, destManifestation.getModel().getParameterValue(rrParamName, pat));
	}		
}
