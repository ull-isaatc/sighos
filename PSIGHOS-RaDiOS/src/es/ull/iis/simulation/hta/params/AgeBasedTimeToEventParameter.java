/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * TODO: Make ageRisks a Parameter, i.e., create a Parameter for "Tables" and use the name here
 * @author Iván Castilla Rodríguez
 *
 */
public class AgeBasedTimeToEventParameter extends Parameter {
	/** Annual risks of the events */
	private final double[][] ageRisks;
	/** Relative risk calculator */
	private final String rrParamName;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	
	/**
	 * 
	 * @param secParams Repository of second order parameters
	 * @param destManifestation Manifestation to which progress
	 * @param ageRisks
	 * @param rrParamName
	 */
	public AgeBasedTimeToEventParameter(String paramName, String description, String source, int year, DiseaseProgression destManifestation, final double[][] ageRisks, String rrParamName) {
		super(paramName, description, source, year, ParameterType.RISK);
		this.ageRisks = ageRisks;
		this.rrParamName = rrParamName;			
		this.destManifestation = destManifestation;
	}
	
	@Override
	public double getValue(Patient pat) {
		final double age = pat.getAge();
		
		// Searches the corresponding age interval
		int j = (ageRisks[0].length == 3) ? 1 : 0;
		int interval = 0;
		while (age > ageRisks[interval][j]) {
			interval++;
		}
		// Generates random numbers for each interval to analyze
		List<Double> rndValues = pat.getRandomNumbersForIncidence(destManifestation, ageRisks.length - interval + 1);
		final double rr = destManifestation.getModel().getParameterValue(rrParamName, pat);
		// Computes time to event within such interval
		double time = Statistics.getAnnualBasedTimeToEvent(ageRisks[interval][j+1], Math.log(rndValues.get(0)), rr);
		
		// Checks if further intervals compute lower time to event
		for (; interval < ageRisks.length; interval++) {
			final double newTime = Statistics.getAnnualBasedTimeToEvent((Double) ageRisks[interval][j+1], Math.log(rndValues.get(rndValues.size() - (ageRisks.length - interval))), rr);
			if ((newTime != Double.MAX_VALUE) && (ageRisks[interval][j] - age + newTime < time))
				time = ageRisks[interval][j] - age + newTime;
		}
		return time;
	}

}
