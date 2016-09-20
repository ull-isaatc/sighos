/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumMap;

import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

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
		TIME_TO_PC_FROM_MC,
		TIME_TO_PC_FROM_OCC,
		TIME_TO_MC_FROM_OCC,
		TIME_TO_JF_FROM_EF,
		TIME_TO_SF_FROM_EF,
		TIME_TO_SF_FROM_JF,
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
		DR_CLINICAL_PRESENTATION,
		DR_INITIAL_VA,
		DR_VA_NONHR_PDR,
		DR_VA_HR_PDR,
		DR_VA_CSME,		
		TIME_TO_NPDR,
		TIME_TO_PDR,
		TIME_TO_CSME,
		TIME_TO_CSME_AND_NONHR_PDR_FROM_CSME,
		TIME_TO_CSME_AND_HR_PDR_FROM_CSME,
		TIME_TO_CSME_AND_NONHR_PDR_FROM_NONHR_PDR,
		TIME_TO_HR_PDR_FROM_NONHR_PDR,
		TIME_TO_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR,
		TIME_TO_CSME_AND_HR_PDR_FROM_HR_PDR,
		ANNUAL_VISITS
	}
	private final static EnumMap<ITEM, RandomNumber> RANDOM_GENERATORS = new EnumMap<ITEM, RandomNumber>(ITEM.class);
	static {
		for (ITEM item : ITEM.values())
			RANDOM_GENERATORS.put(item, RandomNumberFactory.getInstance());
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
	
	public static RandomNumber getRandomNumber(ITEM item) {
		return RANDOM_GENERATORS.get(item);
	}
	
	public static void reset() {
		for (RandomNumber rng : RANDOM_GENERATORS.values()) {
			rng.resetSeed();
		}
	}
	
	public double draw(ITEM item) {
		double rnd;
		final ArrayList<Double> itemRng = rng.get(item);
		// If this is not a reused generator 
		if (countersRng == null) { 
			rnd = RANDOM_GENERATORS.get(item).draw();
			itemRng.add(rnd);
		}
		else {
			final int counter = countersRng.get(item); 
			if (counter < itemRng.size()) {
				rnd = itemRng.get(counter);
			}
			else {
				rnd = RANDOM_GENERATORS.get(item).draw();
				itemRng.add(rnd);				
			}
			countersRng.put(item, counter + 1);
		}
		return rnd;
	}
	
	public double[] draw(ITEM item, int n) {
		double[] rnd = new double[n];
		final ArrayList<Double> itemRng = rng.get(item);
		// If this is not a reused generator 
		if (countersRng == null) { 
			for (int i = 0; i < n; i++) {
				rnd[i] = RANDOM_GENERATORS.get(item).draw();
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
					rnd[i] = RANDOM_GENERATORS.get(item).draw();
					rng.get(item).add(rnd[i]);
				}
			}
			countersRng.put(item, counter + n);
		}
		return rnd;
	}
}
