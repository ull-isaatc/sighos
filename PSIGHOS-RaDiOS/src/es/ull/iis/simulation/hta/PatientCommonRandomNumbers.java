/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class PatientCommonRandomNumbers {
	final HashMap<String, ArrayList<Double>> rndValues;

	/**
	 * 
	 */
	public PatientCommonRandomNumbers() {
		rndValues = new HashMap<>();
	}

	/**
	 * Returns n random numbers
	 * @param n
	 * @return
	 */
	public List<Double> draw(String key, int n) {
		ArrayList<Double> values = rndValues.get(key);
		if (values == null) {
			values = new ArrayList<>();
			rndValues.put(key, values);
		}
		if (n > values.size()) {
			for (int i = values.size(); i < n; i++) {
				final double rnd = SecondOrderParamsRepository.getRNG_FIRST_ORDER().draw();
				values.add(rnd);
			}
		}
		return values.subList(0, n);
	}
	
	/**
	 * Returns n random numbers
	 * @param n
	 * @return
	 */
	public double draw(String key) {
		ArrayList<Double> values = rndValues.get(key);
		if (values == null) {
			values = new ArrayList<>();
			rndValues.put(key, values);
			final double rnd = SecondOrderParamsRepository.getRNG_FIRST_ORDER().draw();
			values.add(rnd);
			return rnd;
		}
		return values.get(0);
	}
}
