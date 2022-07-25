/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ChronicManifestation extends Manifestation {
	private final RandomValues[][] rndValues;
	
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param disease
	 */
	public ChronicManifestation(SecondOrderParamsRepository secParams, String name, String description, Disease disease) {
		super(secParams, name, description, disease, Type.CHRONIC);
		rndValues = new RandomValues[secParams.getNRuns() + 1][secParams.getNPatients()];
		for (int i = 0; i < secParams.getNRuns() + 1; i++)
			for (int j = 0; j < secParams.getNPatients(); j++)
				rndValues[i][j] = new RandomValues();
	}
	
	@Override
	public List<Double> getRandomValues(Patient pat, int n) {
		return rndValues[pat.getSimulation().getIdentifier()][pat.getIdentifier()].draw(n);
	}
	
	@Override
	public double getRandomValue(Patient pat) {
		return rndValues[pat.getSimulation().getIdentifier()][pat.getIdentifier()].draw();
	}
	
	private class RandomValues {
		private final ArrayList<Double> values;
		
		public RandomValues() {
			values = new ArrayList<>();
		}
		
		public List<Double> draw(int n) {
			if (n > values.size()) {
				for (int i = values.size(); i < n; i++) {
					final double rnd = SecondOrderParamsRepository.getRNG_FIRST_ORDER().draw();
					values.add(rnd);
				}
			}
			return values.subList(0, n);
		}
		
		public double draw() {
			return draw(1).get(0);
		}
		
	}

}
