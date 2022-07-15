/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A recurrent acute manifestation 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class AcuteManifestation extends Manifestation {
	private final RandomValues[][] rndValues;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param disease
	 */
	public AcuteManifestation(SecondOrderParamsRepository secParams, String name, String description, Disease disease) {
		super(secParams, name, description, disease, Type.ACUTE);
		rndValues = new RandomValues[secParams.getnRuns() + 1][secParams.getnPatients()];
		for (int i = 0; i < secParams.getnRuns() + 1; i++)
			for (int j = 0; j < secParams.getnPatients(); j++)
				rndValues[i][j] = new RandomValues();
	}

	@Override
	public List<Double> getRandomValues(Patient pat, int n) {
		return rndValues[pat.getSimulation().getIdentifier()][pat.getIdentifier()].draw(pat.getNManifestations(this) + 1, n);
	}

	@Override
	public double getRandomValue(Patient pat) {
		return rndValues[pat.getSimulation().getIdentifier()][pat.getIdentifier()].draw(pat.getNManifestations(this) + 1);
	}

	
	private class RandomValues {
		private final ArrayList<ArrayList<Double>> values;
		
		public RandomValues() {
			values = new ArrayList<>();
		}
		
		public List<Double> draw(int currentEvent, int n) {
			if (currentEvent > values.size()) {
				values.add(new ArrayList<>());
			}
			final ArrayList<Double> currentValues = values.get(currentEvent - 1); 
			if (n > currentValues.size()) {
				for (int i = currentValues.size(); i < n; i++) {
					final double rnd = SecondOrderParamsRepository.getRNG_FIRST_ORDER().draw();
					currentValues.add(rnd);
				}
			}
			return currentValues.subList(0, n);
		}
		
		public double draw(int nEvent) {
			return draw(nEvent, 1).get(0);
		}
		
	}
	
}
