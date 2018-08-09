/**
 * 
 */
package es.ull.iis.simulation.hta.retal.test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ClinicalPresentationDRParamTest {
	Random rng = new Random();
	/** An index to locate a specific eye state in the {@link #probabilities} array */
	final private static EnumMap<EyeState, Integer> order  = new EnumMap<EyeState, Integer>(EyeState.class);
	/** The annual probability to be clinically detected depending on the states of the first eye */	
	private final static double [] probabilities = {0.001, 0.02, 0.5, 0.9}; 

	static {
		int cont = 0;
		order.put(EyeState.NPDR, cont++);
		order.put(EyeState.NON_HR_PDR, cont++);
		order.put(EyeState.HR_PDR, cont++);
		order.put(EyeState.CSME, cont++);		
	}

	/**
	 */
	public ClinicalPresentationDRParamTest() {
	}
	
	public double getProbability(EnumSet<EyeState> eye1, EnumSet<EyeState> eye2) {
		if (eye1.contains(EyeState.CSME) || eye2.contains(EyeState.CSME))
			return probabilities[order.get(EyeState.CSME)];
		if (eye1.contains(EyeState.HR_PDR) || eye2.contains(EyeState.HR_PDR))
			return probabilities[order.get(EyeState.HR_PDR)];
		if (eye1.contains(EyeState.NON_HR_PDR) || eye2.contains(EyeState.NON_HR_PDR))
			return probabilities[order.get(EyeState.NON_HR_PDR)];
		if (eye1.contains(EyeState.NPDR) || eye2.contains(EyeState.NPDR))
			return probabilities[order.get(EyeState.NPDR)];
		return 0.0;
	}
	
	public long getValidatedTimeToEvent(long currentTs, double age, double ageAtDeath, EnumSet<EyeState> eye1, EnumSet<EyeState> eye2) {
		final double yearlyProb = getProbability(eye1, eye2);
		final int currentAge = (int) age;
		final int yearsToDeath = (int)ageAtDeath - currentAge;
		// First year
		if (rng.nextDouble() < yearlyProb)
			return currentTs;
		// Following years but the last
		for (int i = 1; i < yearsToDeath; i++) {
			if (rng.nextDouble() < yearlyProb)
				return currentTs + TimeUnit.DAY.convert(i, TimeUnit.YEAR);
		}
		// Last year
		if (rng.nextDouble() < yearlyProb)
			return currentTs + TimeUnit.DAY.convert(yearsToDeath, TimeUnit.YEAR);
		return Long.MAX_VALUE;
	}

	/**
	 * To test the class
	 * @param args
	 */
	public static void main(String[] args) {
		ClinicalPresentationDRParamTest param = new ClinicalPresentationDRParamTest();
		final long currentTs = 0;
		final double age = 40.0;
		final double ageAtDeath = 80.0;
		final int N = 20;
		for (int i = 0; i < N; i++) {
			final long timeTo = param.getValidatedTimeToEvent(currentTs, age, ageAtDeath, EnumSet.of(EyeState.HR_PDR), EnumSet.of(EyeState.CSME)); 
			System.out.println((timeTo == Long.MAX_VALUE) ? "INF" : (double)timeTo/365.0);
		}
		
	}
}
