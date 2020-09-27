/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.io.PrintStream;
import java.util.ArrayList;

import es.ull.iis.simulation.hta.ComplicationStage;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla
 *
 */
public class PrevalenceView extends Listener {
	private final int [] nAlivePatients;
	private final int [][] nComplications;
	private final int [] nDeaths;
	private final double[][] ageIntervals;
	private final ArrayList<ComplicationStage> availableStates;
	private final PrintStream out = System.out;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public PrevalenceView(TimeUnit simUnit, double[][] ageIntervals, ArrayList<ComplicationStage> availableStates) {
		super("Standard patient viewer");
		this.availableStates = availableStates;
		nAlivePatients = new int[ageIntervals.length+1];
		nComplications = new int[availableStates.size()][ageIntervals.length+1];
		nDeaths = new int[ageIntervals.length+1];
		this.ageIntervals = ageIntervals;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}

	public static double[][] buildAgesInterval(int minAge, int maxAge, int gap, boolean fillToLifetime) {
		int nGroups = (maxAge - minAge) / gap;
		if (fillToLifetime)
			nGroups++;
		final double[][] ageIntervals = new double[nGroups][2];
		for (int i = 0; i < nGroups; i++) {
			ageIntervals[i][0] = minAge + gap * i;
			ageIntervals[i][1] = minAge + gap * (i + 1);
		}
		if (fillToLifetime)
			ageIntervals[nGroups - 1][1] = BasicConfigParams.DEF_MAX_AGE;
		return ageIntervals;
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
				out.print("Age1\tAge2\tPatients");
				for (ComplicationStage comp : availableStates) {
					out.print("\t" + comp.name());
				}
				out.println("\tDeaths");
				out.print(ageIntervals[0][0] + "\t" + ageIntervals[ageIntervals.length - 1][1] + "\t" + nAlivePatients[ageIntervals.length]);
				for (int[] values : nComplications) {
					out.print("\t" + values[ageIntervals.length]);
				}
				out.println("\t" + nDeaths[ageIntervals.length]);
				for (int i = 0; i < ageIntervals.length; i++) {
					out.print(ageIntervals[i][0] + "\t" + ageIntervals[i][1] + "\t" + nAlivePatients[i]);
					for (int[] values : nComplications) {
						out.print("\t" + values[i]);
					}
					out.println("\t" + nDeaths[i]);
				}
			}
		}
		else {
			PatientInfo pInfo = (PatientInfo) info;
			Patient pat = pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.DEATH) {
				final double initAge = pat.getInitAge(); 
				final double ageAtDeath = pat.getAge();
				// First check if the patient died before the lowest interval
				if (ageAtDeath > ageIntervals[0][0]) {
					// Add the patient to the corresponding groups when she/he's alive
					for (int i = 0; i < ageIntervals.length; i++) {
						if (ageAtDeath > ageIntervals[i][0]) {
							nAlivePatients[i]++;
						}
					}
					nAlivePatients[ageIntervals.length]++;
					// Add the patient to the corresponding group when she/he died
					int j = 0;
					while ((j < ageIntervals.length) && (ageAtDeath > ageIntervals[j][0])) {
						j++;
					}
					nDeaths[j - 1]++;
					nDeaths[ageIntervals.length]++;

					// Check all the complications
					for (ComplicationStage comp : availableStates) {
						final long time = pat.getTimeToChronicComorbidity(comp);
						final double ageAtComp = (time == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + time / BasicConfigParams.YEAR_CONVERSION);
						if (ageAtComp != Double.MAX_VALUE) {
							for (int i = 0; (i < ageIntervals.length) && (ageAtDeath > ageIntervals[i][0]); i++) {
								if (ageAtComp < ageIntervals[i][1]) {
									nComplications[comp.ordinal()][i]++;
								}
							}
							nComplications[comp.ordinal()][ageIntervals.length]++;				
						}						
					}
				}
			}
		}
	}

	// For testing
//	private static void printAgeIntervals(double[][] ageIntervals) {
//		for (double[] dd : ageIntervals) {
//			System.out.print(" [" + dd[0] + "," + dd[1] + "]");
//		}		
//		System.out.println();
//	}
//	public static void main(String[] args) {
//		double[][] intervals = buildAgesInterval(40, 80, 10, true);
//		printAgeIntervals(intervals);
//		intervals = buildAgesInterval(40, 80, 5, false);
//		printAgeIntervals(intervals);
//	}
}
