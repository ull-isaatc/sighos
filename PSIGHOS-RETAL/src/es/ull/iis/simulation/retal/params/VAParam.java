/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.TreeMap;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// TODO: Add second-order estimates
public class VAParam extends Param {
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
	/** The VA at incidence of each CNV stage */
	private final static TreeMap<CNVStage, double[]> VA_AT_INCIDENCE_CNV = new TreeMap<CNVStage, double[]>();
	
	static {
		for (int i = 0; i < CNVStage.ALL_STAGES.length; i++)
			VA_AT_INCIDENCE_CNV.put(CNVStage.ALL_STAGES[i], VA_AT_INCIDENCE_CNV_VALUES[i]);
	}

	private final VAProgressionForEF_JF progEF_JF;
	private final VAProgressionForSF progSF;
	private final VAProgressionForGA progGA;
	private final VAProgressionForDR progDR;
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		progGA = new VAProgressionForGA(simul, baseCase);
		progEF_JF = new VAProgressionForEF_JF(simul, baseCase);
		progSF = new VAProgressionForSF(simul, baseCase);
		progDR = new VAProgressionForDR(simul, baseCase);
	}

	/**
	 * Returns the initial VA when the eye is already affected.
	 * A priori, it should be used only for DR
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	public double getInitialVA(Patient pat, int eyeIndex) {
		return progDR.getInitialVA(pat, eyeIndex);
	}
	
	/**
	 * Returns the visual acuity of an eye upon incidence of a new stage in the ARMD progression. 
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return The visual acuity of an eye upon incidence of a new stage in the ARMD progression.
	 */
	public double getVAAtIncidence(EyeState incidentState, CNVStage incidentCNVStage) {
		final double va;
		
		if (incidentState.equals(EyeState.AMD_CNV)) {
			va = VA_AT_INCIDENCE_CNV.get(incidentCNVStage)[0];
		}
		else if (incidentState.equals(EyeState.AMD_GA)) {	
			va = VA_AT_INCIDENCE_GA[0];
		}
		else {
			va = 0.0;
		}
		return va;
	}

	private static void print(ArrayList<VAProgressionPair> pairs) {
		for (VAProgressionPair pair : pairs) 
			System.err.println(pair);		
		
	}
	/**
	 * Merges two lists of visual acuity changes. Each list contains at least one change, due at the end of the studied period. 
	 * The resulting list uses the worst state possible between both lists, by calculating the missing values as a linear interpolation.
	 * @param vaAtStart Visual acuity before all the changes start
	 * @param changes1 Changes due to eye disease 1
	 * @param changes2 Changes due to eye disease 2
	 * @return The worst possible list of changes in visual acuity for a patient affected by two different problems.
	 */
	private ArrayList<VAProgressionPair> mergeVAProgressions(double vaAtStart, ArrayList<VAProgressionPair> changes1, ArrayList<VAProgressionPair> changes2) {
		final Iterator<VAProgressionPair> iter1 = changes1.iterator();
		final Iterator<VAProgressionPair> iter2 = changes2.iterator();
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>(changes1.size() + changes2.size() - 1);
		
		try {
		
		VAProgressionPair pair1 = iter1.next();
		VAProgressionPair pair2 = iter2.next();
		double lastVA1 = vaAtStart;
		double lastVA2 = vaAtStart;
		long lastT1 = 0;
		long lastT2 = 0;
		long t1 = pair1.timeToChange;
		long t2 = pair2.timeToChange;
		while (iter1.hasNext() || iter2.hasNext()) {
			if (t1 < t2) {
				final double interpolated = lastVA2 + (pair2.va - lastVA2) / (t2 - lastT2);
				changes.add((interpolated > pair1.va) ? new VAProgressionPair(t1, interpolated) : pair1);
				lastT1 = t1;
				lastVA1 = pair1.va;
				pair1 = iter1.next();
				t1 += pair1.timeToChange;
			}
			else if (t2 < t1) {
				final double interpolated = lastVA1 + (pair1.va - lastVA1) / (t1 - lastT1);
				changes.add((interpolated > pair2.va) ? new VAProgressionPair(t2, interpolated) : pair2);
				lastT2 = t2;
				lastVA2 = pair2.va;
				pair2 = iter2.next();
				t2 += pair2.timeToChange;
			}
			else {
				changes.add((pair1.va > pair2.va) ? pair1 : pair2);
				// To avoid problems with 0 offsets
				if (iter1.hasNext()) {
					lastT1 = t1;
					lastVA1 = pair1.va;
					pair1 = iter1.next();
					t1 += pair1.timeToChange;
				}
				if (iter2.hasNext()) {
					lastT2 = t2;
					lastVA2 = pair2.va;
					pair2 = iter2.next();
					t2 += pair2.timeToChange;
				}
			}
		}
		// This point is reached when both have no next pair
		changes.add((pair1.va > pair2.va) ? pair1 : pair2);
		} catch(Exception e) {
			System.err.println("Changes 1:");
			print(changes1);
			System.err.println("Changes 2:");
			print(changes2);
			System.err.println("Changes merged:");
			print(changes);
			e.printStackTrace();
		} finally {
		return changes;
		}
	}
	
	/**
	 * Returns a set of 
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, EyeState incidentState, CNVStage incidentCNVStage) {
		ArrayList<VAProgressionPair> changes = null;
		final double vaAtStart = pat.getVA(eyeIndex);
		// If the patient had the worst VA, it remains the same
		if (vaAtStart == VisualAcuity.MAX_LOGMAR) {
			changes = new ArrayList<VAProgressionPair>();
			changes.add(new VAProgressionPair(simul.getTs() - pat.getLastVAChangeTs(eyeIndex), vaAtStart));
		}
		else {				
			// Computes the new VA expected for the new eye state; if no new state is expected (death), uses the current VA 
			final double incidentVA = (incidentState == null) ? vaAtStart : Math.max(vaAtStart, getVAAtIncidence(incidentState, incidentCNVStage));
			
			final EnumSet<EyeState> affectedEye = pat.getEyeState(eyeIndex);
			// No progression but the incident VA is expected if the eye is healthy
			if (affectedEye.contains(EyeState.HEALTHY)) {
				changes = new ArrayList<VAProgressionPair>();
				changes.add(new VAProgressionPair(simul.getTs() - pat.getLastVAChangeTs(eyeIndex), incidentVA));
			}
			else {
				if (affectedEye.contains(EyeState.AMD_CNV)) {
					final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
					// Subfoveal lesion
					if (stage.getPosition() == CNVStage.Position.SF)  {
						changes = progSF.getVAProgression(pat, eyeIndex, incidentVA);
					}
					// Juxtafoveal or extrafoveal lesion
					else {
						changes = progEF_JF.getVAProgression(pat, eyeIndex, incidentVA); 
					}
				}
				else if (affectedEye.contains(EyeState.AMD_GA)) {
					changes = progGA.getVAProgression(pat, eyeIndex, incidentVA); 
				}
				else {
					// No progression but the incident VA is expected if the eye has EARM
					changes = new ArrayList<VAProgressionPair>();
					changes.add(new VAProgressionPair(simul.getTs() - pat.getLastVAChangeTs(eyeIndex), incidentVA));
				}
				if (affectedEye.contains(EyeState.CSME) || affectedEye.contains(EyeState.NPDR) || affectedEye.contains(EyeState.NON_HR_PDR)
						|| affectedEye.contains(EyeState.HR_PDR)) {
					ArrayList<VAProgressionPair> changesDR = progDR.getVAProgression(pat, eyeIndex, incidentVA);
					// If the patient was already affected by ARMD, both progressions have to be merged
					if (changes != null) {
						changes = mergeVAProgressions(vaAtStart, changes, changesDR);
					}
					else {
						changes = changesDR;
					}
				}
			}
		}
		return changes;
	}
}
