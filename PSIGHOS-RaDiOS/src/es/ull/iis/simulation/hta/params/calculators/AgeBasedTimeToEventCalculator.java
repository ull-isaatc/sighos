/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AgeBasedTimeToEventCalculator implements ParameterCalculator {
	/** Annual risks of the events */
	private final Object[][] ageRisks;
	/** Relative risk calculator */
	private final RRCalculator rr;
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	
	public AgeBasedTimeToEventCalculator(final Object[][] ageRisks, DiseaseProgression destManifestation, RRCalculator rr) {
		this.ageRisks = ageRisks;
		this.rr = rr;			
		this.destManifestation = destManifestation;
	}
	
	@Override
	public double getValue(Patient pat) {
		final double age = pat.getAge();
		
		// Searches the corresponding age interval
		int j = (ageRisks[0].length == 3) ? 1 : 0;
		int interval = 0;
		while (age > (Double) ageRisks[interval][j]) {
			interval++;
		}
		// Generates random numbers for each interval to analyze
		List<Double> rndValues = pat.getRandomNumbersForIncidence(destManifestation, ageRisks.length - interval + 1);
		// Computes time to event within such interval
		double time = Statistics.getAnnualBasedTimeToEvent((Double)ageRisks[interval][j+1], Math.log(rndValues.get(0)), rr.getRR(pat));
		
		// Checks if further intervals compute lower time to event
		for (; interval < ageRisks.length; interval++) {
			final double newTime = Statistics.getAnnualBasedTimeToEvent((Double) ageRisks[interval][j+1], Math.log(rndValues.get(rndValues.size() - (ageRisks.length - interval))), rr.getRR(pat));
			if ((newTime != Double.MAX_VALUE) && ((Double) ageRisks[interval][j] - age + newTime < time))
				time = (Double) ageRisks[interval][j] - age + newTime;
		}
		return time;
	}

}
