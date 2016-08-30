/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.CommonParams;

/**
 * @author Iván Castilla
 *
 */
public class PatientPrevalenceView extends Listener {
	private static final double[][] AGES = {{65,70}, {70,75}, {75,80}, {80,CommonParams.MAX_AGE}};
	private static final int N_INTERVALS = AGES.length;
	private final int [] nPatients;
	private final int [] nEARM;
	private final int [] nCNV;
	private final int [] nGA;
	private final int [] nDeaths;
	private final PrintStream out = System.out;
	

	/**
	 * @param simul
	 */
	public PatientPrevalenceView(Simulation simul) {
		super(simul, "Standard patient viewer");
		nPatients = new int[N_INTERVALS+1];
		nEARM = new int[N_INTERVALS+1];
		nCNV = new int[N_INTERVALS+1];
		nGA = new int[N_INTERVALS+1];
		nDeaths = new int[N_INTERVALS+1];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			out.println("Age\tPatients\tEARM\tGA\tCNV\tDeaths");
			out.println("TOTAL\t" + nPatients[N_INTERVALS] + "\t" + nEARM[N_INTERVALS] + "\t" + nGA[N_INTERVALS] 
					+ "\t" + nCNV[N_INTERVALS] + "\t" + nDeaths[N_INTERVALS]);
			for (int i = 0; i < N_INTERVALS; i++)
				System.out.println(AGES[i][0] + "\t" + nPatients[i] + "\t" + nEARM[i] + "\t" + nGA[i] + "\t" + nCNV[i] + "\t" + nDeaths[i]);
		}
		else {
			PatientInfo pInfo = (PatientInfo) info;
			Patient pat = (Patient) pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				final double initAge = pat.getInitAge(); 
				final double ageAtDeath = pat.getAge();
				final double ageAtEARM = (pat.getTimeToEyeState(EyeState.EARM, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (pat.getTimeToEyeState(EyeState.EARM, 0) / 365.0));
				final double ageAtGA = (pat.getTimeToEyeState(EyeState.AMD_GA, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.AMD_GA, 0), pat.getTimeToEyeState(EyeState.AMD_GA, 1)) / 365.0));
				final double ageAtCNV = (pat.getTimeToEyeState(EyeState.AMD_CNV, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.AMD_CNV, 0), pat.getTimeToEyeState(EyeState.AMD_CNV, 1)) / 365.0));
				final double ageAtAMD = Math.min(ageAtGA, ageAtCNV);
				// First check if the patient died before the lowest interval
				if (ageAtDeath > AGES[0][0]) {
					// Add the patient to the corresponding groups when she/he's alive
					for (int i = 0; i < N_INTERVALS; i++) {
						if (ageAtDeath > AGES[i][0]) {
							nPatients[i]++;
						}
					}
					nPatients[N_INTERVALS]++;
					// Add the patient to the corresponding group when she/he died
					int j = 0;
					while ((j < N_INTERVALS) && (ageAtDeath > AGES[j][0])) {
						j++;
					}
					nDeaths[j - 1]++;
					nDeaths[N_INTERVALS]++;
					if (ageAtEARM != Double.MAX_VALUE) {
						for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]) && (ageAtAMD > AGES[i][0]); i++) {
							if (ageAtEARM < AGES[i][1]) {
								nEARM[i]++;
							}
						}
						if (ageAtAMD >= AGES[0][0])
							nEARM[N_INTERVALS]++;
					}
					if (ageAtGA != Double.MAX_VALUE) {
						for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]) && (ageAtCNV > AGES[i][0]); i++) {
							if (ageAtGA < AGES[i][1]) {
								nGA[i]++;
							}
						}					  
						if (ageAtCNV >= AGES[0][0])
							nGA[N_INTERVALS]++;
					}
					if (ageAtCNV != Double.MAX_VALUE) {
						for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]); i++) {
							if (ageAtCNV < AGES[i][1]) {
								nCNV[i]++;
							}
						}
						nCNV[N_INTERVALS]++;
					}
				}
			}
		}
	}
}
