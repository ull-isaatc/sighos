/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualRiskBasedTimeToEventParameter extends Parameter {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;

	/**
	 * 
	 * @param destManifestation Manifestation to which progress
	 * @param riskParamName Name of the second order parameter that defines the annual risk
	 * @param rrParamName Relative risk calculator
	 */
	public AnnualRiskBasedTimeToEventParameter(HTAModel model, String paramName, String description, String source, int year, DiseaseProgression destManifestation) {
		super(model, paramName, description, source, year, ParameterType.RISK);
		this.destManifestation = destManifestation;
		addUsedParameter(StandardParameter.PROBABILITY);
		addUsedParameter(StandardParameter.RELATIVE_RISK);
	}

	@Override
	public double getValue(Patient pat) {
		final double rndValue = Math.log(pat.getRandomNumberForIncidence(destManifestation));
		return Statistics.getAnnualBasedTimeToEvent(destManifestation.getUsedParameterValue(StandardParameter.PROBABILITY, pat), rndValue, 
					destManifestation.getUsedParameterValue(StandardParameter.RELATIVE_RISK, pat));
	}		
}
