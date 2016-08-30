/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

/**
 * A container class for all the random number generators of a patient
 * @author Ivan Castilla Rodriguez
 *
 */
public class RandomForPatient {
	public static enum ITEM {
		SEX,
		TIME_TO_EARM,
		TIME_TO_E1AMD,
		TIME_TO_AMD_E2_NOARM,
		TIME_TO_AMD_E2_EARM,
		TIME_TO_AMD_E2_GA,
		TIME_TO_AMD_E2_CNV,
		TIME_TO_CNV_E2_NOARM,
		TIME_TO_CNV_E2_EARM,
		TIME_TO_CNV_E2_GA,
		TIME_TO_CNV_E2_CNV,
		TIME_TO_E2AMD_E1EARM,
		TIME_TO_E2AMD_E1GA,
		TIME_TO_E2AMD_E1CNV,
		ARMD_P_CNV1,
		ARMD_P_CNV2,
		ARMD_TYPE_POSITION_CNV,
		ARMD_PROG_TWICE_GA,
		ARMD_PROG_GA,
		ARMD_LEVELS_LOST_SF,
		ARMD_CLINICAL_PRESENTATION,
		SENSITIVITY,
		SPECIFICITY,
		DIABETIC,
		DIABETES_TYPE,
		DIABETES_INCIDENCE,
		DR_INITIAL_STATE,
		DR_INITIAL_ME,
		DR_INITIAL_CSME,
		TIME_TO_NPDR,
		TIME_TO_PDR,
		TIME_TO_CSME,
		MILD_DR
	}
	private final static EnumMap<ITEM, Random> RANDOM_GENERATORS = new EnumMap<ITEM, Random>(ITEM.class);
	static {
		for (ITEM item : ITEM.values())
			RANDOM_GENERATORS.put(item, new Random());
	}

	private final EnumMap<ITEM, ArrayList<Double>> rng;
	private final EnumMap<ITEM, Integer> countersRng;
	
	/**
	 * 
	 */
	public RandomForPatient() {
		rng = new EnumMap<ITEM, ArrayList<Double>>(ITEM.class);
		countersRng = null;
		for (ITEM item : ITEM.values()) {
			rng.put(item, new ArrayList<Double>());
		}
	}

	/**
	 * Creates a container of random number generators which is a copy of an original one
	 * @param copyOf Original container of random number generators 
	 */
	public RandomForPatient(RandomForPatient copyOf) {
		rng = new EnumMap<ITEM, ArrayList<Double>>(copyOf.rng);
		countersRng = new EnumMap<ITEM, Integer>(ITEM.class);
		for (ITEM item : ITEM.values()) {
			countersRng.put(item, 0);
		}
	}
	
	public double getRandomNumber(ITEM item) {
		double rnd;
		final ArrayList<Double> itemRng = rng.get(item);
		// If this is not a reused generator 
		if (countersRng == null) { 
			rnd = RANDOM_GENERATORS.get(item).nextDouble();
			itemRng.add(rnd);
		}
		else {
			final int counter = countersRng.get(item); 
			if (counter < itemRng.size()) {
				rnd = itemRng.get(counter);
			}
			else {
				rnd = RANDOM_GENERATORS.get(item).nextDouble();
				itemRng.add(rnd);				
			}
			countersRng.put(item, counter + 1);
		}
		return rnd;
	}
	
	public double[] getRandomNumber(ITEM item, int n) {
		double[] rnd = new double[n];
		final ArrayList<Double> itemRng = rng.get(item);
		// If this is not a reused generator 
		if (countersRng == null) { 
			for (int i = 0; i < n; i++) {
				rnd[i] = RANDOM_GENERATORS.get(item).nextDouble();
				rng.get(item).add(rnd[i]);
			}
		}
		else {
			final int counter = countersRng.get(item);
			if (counter + n <= itemRng.size()) {
				for (int i = counter; i < counter + n; i++) {
					rnd[i - counter] = itemRng.get(i);
				}
			}
			else {
				for (int i = 0; i < n; i++) {
					rnd[i] = RANDOM_GENERATORS.get(item).nextDouble();
					rng.get(item).add(rnd[i]);
				}
			}
			countersRng.put(item, counter + n);
		}
		return rnd;
	}
}
