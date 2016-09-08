/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.params.CNVStage;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ClinicalPresentationARMDParamTest {
	/** An index to locate a specific eye state in the {@link #probabilities} array */
	final private static EnumMap<EyeState, Integer> order  = new EnumMap<EyeState, Integer>(EyeState.class);
	/** An index to locate a specific CNV stage in the {@link #probabilities} array */
	final private static EnumMap<CNVStage.Position, Integer> orderCNV  = new EnumMap<CNVStage.Position, Integer>(CNVStage.Position.class);
	/** The annual probability to be clinically detected depending on the states of first and fellow eyes */
	final private static double[][] probabilities;
	Random rng = new Random();
	
	static {
		int cont = 0;
		order.put(EyeState.HEALTHY, cont++);
		order.put(EyeState.EARM, cont++);
		order.put(EyeState.AMD_GA, cont++);
		orderCNV.put(CNVStage.Position.EF, cont++);
		orderCNV.put(CNVStage.Position.JF, cont++);
		orderCNV.put(CNVStage.Position.SF, cont);
		probabilities = new double[][] { 
				{0.0, 0.000523174, 0.102947627, 0.019835782, 0.059508913, 0.060341195},
				{0.000523174, 0.0, 0.098953626, 0.020499405, 0.058588325, 0.104656435},
				{0.102947627, 0.098953626, 0.171665603, 0.098124373, 0.100203503, 0.102339942},
				{0.019835782, 0.020499405, 0.098124373, 0.039980248, 0.059213979, 0.101338676},
				{0.059508913, 0.058588325, 0.100203503, 0.059213979, 0.098470343, 0.102573043},
				{0.060341195, 0.104656435, 0.102339942, 0.101338676, 0.102573043, 0.118084246}
		};
	}

	/**
	 */
	public ClinicalPresentationARMDParamTest() {
	}

	private int getOrder(EyeState eye, CNVStage stage) {
		if (eye == EyeState.HEALTHY)
			return order.get(EyeState.HEALTHY);
		else if (eye == EyeState.EARM)
			return order.get(EyeState.EARM);
		else if (eye == EyeState.AMD_GA)
			return order.get(EyeState.AMD_GA);
		else if (eye == EyeState.AMD_CNV) 
			return orderCNV.get(stage.getPosition());
		else
			return 0;
	}
	
	public double getProbability(EyeState eye1, CNVStage stage1, EyeState eye2, CNVStage stage2) {
		return probabilities[getOrder(eye1, stage1)][getOrder(eye2, stage2)];
	}
	
	public long getValidatedTimeToEvent(long currentTs, double age, double ageAtDeath, EyeState eye1, CNVStage stage1, EyeState eye2, CNVStage stage2) {
		final double yearlyProb = getProbability(eye1, stage1, eye2, stage2);
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
		ClinicalPresentationARMDParamTest param = new ClinicalPresentationARMDParamTest();
		final long currentTs = 0;
		final double age = 40.0;
		final double ageAtDeath = 80.0;
		final int N = 20;
		for (int i = 0; i < N; i++) {
			final long timeTo = param.getValidatedTimeToEvent(currentTs, age, ageAtDeath, EyeState.AMD_CNV, new CNVStage(CNVStage.Type.PC, CNVStage.Position.SF), EyeState.AMD_GA, null); 
			System.out.println((timeTo == Long.MAX_VALUE) ? "INF" : (double)timeTo/365.0);
		}
		
	}
}
