/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iván Castilla
 *
 */
public class PatientCommonRandomNumbers {
	private final HashMap<String, ArrayList<Double>> rndValues;
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG = RandomNumberFactory.getInstance();

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
				final double rnd = RNG.draw();
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
			final double rnd = RNG.draw();
			values.add(rnd);
			return rnd;
		}
		return values.get(0);
	}

    /**
     * Changes the default random number generator for first order uncertainty
     * @param rng New random number generator
     */
    public static void setRNG(RandomNumber rng) {
    	RNG = rng;
    }

    /**
     * Returns the random number generator for first order uncertainty
     * @return the random number generator for first order uncertainty
     */
    public static RandomNumber getRNG() {
    	return RNG;
    }
}
