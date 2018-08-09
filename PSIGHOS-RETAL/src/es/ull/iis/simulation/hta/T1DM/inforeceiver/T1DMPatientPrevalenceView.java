/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.Complication;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla
 *
 */
public class T1DMPatientPrevalenceView extends Listener {
	private final int [] nPatients;
	private final int [][] nComplications;
	private final int [] nDeaths;
	private final double[][] ageIntervals;
	private final PrintStream out = System.out;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public T1DMPatientPrevalenceView(TimeUnit simUnit, double[][] ageIntervals) {
		super("Standard patient viewer");
		nPatients = new int[ageIntervals.length+1];
		nComplications = new int[CommonParams.N_COMPLICATIONS][ageIntervals.length+1];
		nDeaths = new int[ageIntervals.length+1];
		this.ageIntervals = ageIntervals;
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationEndInfo.class);
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
			ageIntervals[nGroups - 1][1] = CommonParams.MAX_AGE;
		return ageIntervals;
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			out.print("Age1\tAge2\tPatients");
			for (Complication comp : Complication.values()) {
				out.print("\t" + comp.name());
			}
			out.println("\tDeaths");
			out.print(ageIntervals[0][0] + "\t" + ageIntervals[ageIntervals.length - 1][1] + "\t" + nPatients[ageIntervals.length]);
			for (int[] values : nComplications) {
				out.print("\t" + values[ageIntervals.length]);
			}
			out.println("\t" + nDeaths[ageIntervals.length]);
			for (int i = 0; i < ageIntervals.length; i++) {
				out.print(ageIntervals[i][0] + "\t" + ageIntervals[i][1] + "\t" + nPatients[i]);
				for (int[] values : nComplications) {
					out.print("\t" + values[i]);
				}
				out.println("\t" + nDeaths[i]);
			}
		}
		else {
			T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			T1DMPatient pat = pInfo.getPatient();
			if (pInfo.getType() == T1DMPatientInfo.Type.FINISH) {
				final double initAge = pat.getInitAge(); 
				final double ageAtDeath = pat.getAge();
				// First check if the patient died before the lowest interval
				if (ageAtDeath > ageIntervals[0][0]) {
					// Add the patient to the corresponding groups when she/he's alive
					for (int i = 0; i < ageIntervals.length; i++) {
						if (ageAtDeath > ageIntervals[i][0]) {
							nPatients[i]++;
						}
					}
					nPatients[ageIntervals.length]++;
					// Add the patient to the corresponding group when she/he died
					int j = 0;
					while ((j < ageIntervals.length) && (ageAtDeath > ageIntervals[j][0])) {
						j++;
					}
					nDeaths[j - 1]++;
					nDeaths[ageIntervals.length]++;

					// Check all the complications
					for (Complication comp : Complication.values()) {
						final long time = pat.getTimeToComplication(comp);
						final double ageAtComp = (time == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + time / CommonParams.YEAR_CONVERSION);
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
