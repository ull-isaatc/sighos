/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.TreeMap;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// TODO: Currently only using first-order estimates
public class VAParam {
	/**
	 * Source: Karnon model
	 */
	private final static double[] VA_AT_INCIDENCE_GA = {0.150298257, 0.086371351};
	/**
	 * Source: Karnon model
	 */
	private final static double[][] VA_AT_INCIDENCE_CNV_VALUES = {
			{0.255174268, 0.142468756},
			{0.400182418, 0.176357241},
			{0.763685389, 0.25813546},
			{0.255174268, 0.142468756},
			{0.400182418, 0.176357241},
			{0.763685389, 0.25813546},
			{0.245667743, 0.144480836},
			{0.404284814, 0.172330988},
			{0.757729834, 0.255845865}
		};
	private final static TreeMap<CNVStage, double[]> VA_AT_INCIDENCE_CNV = new TreeMap<CNVStage, double[]>();

	static {
		for (int i = 0; i < CNVStage.ALL_STAGES.length; i++)
			VA_AT_INCIDENCE_CNV.put(CNVStage.ALL_STAGES[i], VA_AT_INCIDENCE_CNV_VALUES[i]);
	}

	private static final VAProgressionForEF_JF progEF_JF = new VAProgressionForEF_JF();
	private static final VAProgressionForSF progSF = new VAProgressionForSF();
	private static final VAProgressionForGA progGA = new VAProgressionForGA();
	private static final VAProgressionForDR progDR = new VAProgressionForDR();
	private static final VAProgressionForCNV progCNV = new VAProgressionForCNV();
	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAParam() {
	}

	/**
	 * 
	 * @param pat
	 * @param eyeIndex
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double getVAAtIncidence(EyeState state, CNVStage stage) {
		final double va;
		
		if (state.equals(EyeState.AMD_CNV)) {
			va = VA_AT_INCIDENCE_CNV.get(stage)[0];
		}
		else if (state.equals(EyeState.AMD_GA)) {	
			va = VA_AT_INCIDENCE_GA[0];
		}
		else {
			va = 0.0;
		}
		return va;
	}
	
	/**
	 * Returns a set of 
	 * @param pat
	 * @param eyeIndex
	 * @return
	 */
	private static VAProgressionPair[] getVAProgression(EyeState state, CNVStage stage, double initialVA, double ageAt, long daysFrom) {
		
		if (state.equals(EyeState.AMD_CNV)) {
			// Subfoveal lesion
			if (stage.getPosition() == CNVStage.Position.SF)  {
				return progSF.getLevelChanges(stage, initialVA, ageAt, daysFrom);
			}
			// Juxtafoveal or extrafoveal lesion
			else {
				return progEF_JF.getVA(stage, initialVA, daysFrom); 
			}
		}
//		else if (state.equals(EyeState.AMD_GA)) {
//			return progGA.getProgression(long startGATs, long startTs, long endTs, double currentVA, double expectedVA); 
//		}
		return null;
	}
	
	/**
	 * Merges two lists of visual acuity changes. Each list contains at least one change, due at the end of the studied period. 
	 * The resulting list uses the worst state possible between both lists, by calculating the missing values as a linear interpolation.
	 * @param vaAtStart Visual acuity before all the changes start
	 * @param changes1 Changes due to eye disease 1
	 * @param changes2 Changes due to eye disease 2
	 * @return The worst possible list of changes in visual acuity for a patient affected by two different problems.
	 */
	// FIXME: Eventually fails. Possibly due to incorrect final change assigned in DR or CNV 
	private static ArrayList<VAProgressionPair> mergeVAProgressions(double vaAtStart, ArrayList<VAProgressionPair> changes1, ArrayList<VAProgressionPair> changes2) {
		final Iterator<VAProgressionPair> iter1 = changes1.iterator();
		final Iterator<VAProgressionPair> iter2 = changes2.iterator();
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>(changes1.size() + changes2.size() - 1);
		
		VAProgressionPair pair1 = iter1.next();
		VAProgressionPair pair2 = iter2.next();
		double lastVA1 = vaAtStart;
		double lastVA2 = vaAtStart;
		long t1 = pair1.timeToChange;
		long t2 = pair2.timeToChange;
		while (iter1.hasNext() || iter2.hasNext()) {
			if (t1 < t2) {
				// Update time of pair 2
				t2 = t2 - t1;
				// Interpolate VA in pair 2
				lastVA2 = lastVA2 + (pair2.va - lastVA2) / t1;
				// Take the worst VA
				changes.add(new VAProgressionPair(t1, (lastVA2 > pair1.va) ? lastVA2 : pair1.va));
				// Update pair 1
				lastVA1 = pair1.va;
				pair1 = iter1.next();
				t1 = pair1.timeToChange;
			}
			else if (t2 < t1) {
				// Update time of pair 1
				t1 = t1 - t2;
				// Interpolate VA in pair 1
				lastVA1 = lastVA1 + (pair1.va - lastVA1) / t2;
				// Take the worst VA
				changes.add(new VAProgressionPair(t2, (lastVA1 > pair2.va) ? lastVA1 : pair2.va));
				// Update pair 2
				lastVA2 = pair2.va;
				pair2 = iter2.next();
				t2 = pair2.timeToChange;
			}
			else {
				changes.add((pair1.va > pair2.va) ? pair1 : pair2);
				// Update pair 1
				if (iter1.hasNext()) {
					pair1 = iter1.next();
					lastVA1 = pair1.va;
					t1 = pair1.timeToChange;
				}
				// Update pair 2
				if (iter2.hasNext()) {
					pair2 = iter2.next();
					lastVA2 = pair2.va;
					t2 = pair2.timeToChange;
				}
			}
		}
		// This point is reached when both have no next pair
		changes.add(new VAProgressionPair(t2, (pair1.va > pair2.va) ? pair1.va : pair2.va));
		return changes;
	}
		
	public static void testGA() {
		EyeState state = EyeState.AMD_GA;
		CNVStage stage = CNVStage.ALL_STAGES[8];
		double initialVA = 0.0;
		double ageAt = 40.0;
		long daysFrom = 200;
		
		System.out.println("INITIAL: " + state + ", " + stage + ", va: " + initialVA + ", age: " + ageAt + ", daysfrom: " + daysFrom);
		VAProgressionPair[] pairs = getVAProgression(state, stage, initialVA, ageAt, daysFrom);
		if (pairs != null)
			for (VAProgressionPair pair : pairs) 
				System.out.println((pair.timeToChange) + ":" + pair.va);
		else
			System.out.println("No progression");
		System.out.println("INITIAL: " + state + ", " + stage + ", va: " + initialVA + ", age: " + ageAt + ", daysfrom: " + daysFrom);
		pairs = getVAProgression(state, stage, initialVA, ageAt, daysFrom);
		if (pairs != null)
			for (VAProgressionPair pair : pairs) 
				System.out.println((pair.timeToChange) + ":" + pair.va);
		else
			System.out.println("No progression");		
	}
	
	private static void printProgression(ArrayList<VAProgressionPair> pairs, long ts) {
		long sum = 0;
		for (VAProgressionPair pair : pairs) {
			sum += pair.timeToChange;
			System.out.println(pair);
		}
		System.out.println((sum == ts) ? "OK" : "FAIL!!!!!");
	}
	
	public static void testDR() {
		printProgression(progDR.getVAProgression(2000, 0, EnumSet.of(EyeState.NON_HR_PDR), 0.0, 1.0), 2000);
	}
	
	public static void testMerge() {
		double vaAtStart = 0.0;
		ArrayList<VAProgressionPair> changes1 = new ArrayList<VAProgressionPair>();
		ArrayList<VAProgressionPair> changes2 = new ArrayList<VAProgressionPair>();
		changes1.add(new VAProgressionPair(365, 0.763));
		changes1.add(new VAProgressionPair(152, 0.763));
		changes2.add(new VAProgressionPair(517, 0.763));
//		changes1.add(new VAProgressionPair(1, 0.3));
//		changes1.add(new VAProgressionPair(1, 0.4));
//		changes1.add(new VAProgressionPair(1, 0.6));
//		changes1.add(new VAProgressionPair(1, 0.95));
//		changes2.add(new VAProgressionPair(2, 0.5));
//		changes2.add(new VAProgressionPair(2, 0.9));
//		changes1.add(new VAProgressionPair(6205, 0.20290285195581328));
//		changes2.add(new VAProgressionPair(365, 0.23850285195581328));
//		changes2.add(new VAProgressionPair(365, 0.2741028519558133));
//		changes2.add(new VAProgressionPair(365, 0.3097028519558133));
//		changes2.add(new VAProgressionPair(365, 0.34530285195581334));
//		changes2.add(new VAProgressionPair(365, 0.38090285195581336));
//		changes2.add(new VAProgressionPair(365, 0.4165028519558134));
//		changes2.add(new VAProgressionPair(365, 0.4521028519558134));
//		changes2.add(new VAProgressionPair(365, 0.4877028519558134));
//		changes2.add(new VAProgressionPair(365, 0.5233028519558134));
//		changes2.add(new VAProgressionPair(365, 0.5589028519558134));
//		changes2.add(new VAProgressionPair(365, 0.5945028519558133));
//		changes2.add(new VAProgressionPair(365, 0.6301028519558133));
//		changes2.add(new VAProgressionPair(365, 0.6657028519558132));
//		changes2.add(new VAProgressionPair(365, 0.7013028519558132));
//		changes2.add(new VAProgressionPair(365, 0.7369028519558132));
//		changes2.add(new VAProgressionPair(365, 0.7725028519558131));
//		changes2.add(new VAProgressionPair(365, 0.8081028519558131));
//		changes2.add(new VAProgressionPair(0, 0.8437028519558131));
		
		System.out.println("Pair 1");
		printProgression(changes1, 517);
		System.out.println("Pair 2");
		printProgression(changes2, 517);
		System.out.println("Merged");
		printProgression(mergeVAProgressions(vaAtStart, changes1, changes2), 517);
	}

	public static void main(String[] args) {
//		long currentTs = 2000;
//		long lastChangeTs = 0;
//		double currentVA = 0.3;
//		CNVStage stage = new CNVStage(CNVStage.Type.OCCULT, CNVStage.Position.SF);
//		boolean diagnosed = true;
//		double expectedVA = 0.6;
//		printProgression(progCNV.getVAProgression(currentTs, lastChangeTs, currentVA, stage, diagnosed, expectedVA));
		
//		long startStageTs = 0;
//		long onAntiVEGFTs = 0;
//		long startTs = 0 * 365 + 23;
//		long endTs = 0 * 365 + 197;
//		double currentVA = 0.0;
//		double expectedVA = 0.0;
//		boolean diagnosed = true;
//		printProgression(progCNV.getVAProgression(startTs, endTs, startStageTs, onAntiVEGFTs, currentVA, diagnosed, expectedVA), endTs - startTs);
		testMerge();
	}
}
