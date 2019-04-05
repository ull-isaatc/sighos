/**
 * 
 */
package es.ull.iis.simulation.hta.retal.inforeceiver;

import java.io.PrintStream;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RETALSimulation;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.info.PatientInfo;
import es.ull.iis.simulation.hta.retal.params.CommonParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class PatientPrevalenceView extends Listener {
	private final EnumSet<RETALSimulation.DISEASES> diseases;
	private static final double[][] AGES = {{40, 45}, {45, 50}, {50, 55}, {55, 60}, {60, 65}, {65,70}, {70,75}, {75,80}, {80,CommonParams.MAX_AGE}};
	private static final int N_INTERVALS = AGES.length;
	private final int [] nPatients;
	private final int [] nEARM;
	private final int [] nCNV;
	private final int [] nGA;
	private final int [] nNPDR;
	private final int [] nNonHRPDR;
	private final int [] nHRPDR;
	private final int [] nCSME;
	private final int [] nDiabetes;
	private final int [] nDeaths;
	private final PrintStream out = System.out;
	

	/**
	 * @param simul
	 */
	public PatientPrevalenceView(EnumSet<RETALSimulation.DISEASES> diseases) {
		super("Standard patient viewer");
		this.diseases = EnumSet.copyOf(diseases);
		nPatients = new int[N_INTERVALS+1];
		if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
			nEARM = new int[N_INTERVALS+1];
			nCNV = new int[N_INTERVALS+1];
			nGA = new int[N_INTERVALS+1];
		}
		else {
			nEARM = null;
			nCNV = null;
			nGA = null;
		}
		if (diseases.contains(RETALSimulation.DISEASES.DR)) {
			nNPDR = new int[N_INTERVALS+1];
			nNonHRPDR = new int[N_INTERVALS+1];
			nHRPDR = new int[N_INTERVALS+1];
			nCSME= new int[N_INTERVALS+1];
		}
		else {
			nNPDR = null;
			nNonHRPDR = null;
			nHRPDR = null;
			nCSME= null;
		}
		nDiabetes = new int[N_INTERVALS+1];
		nDeaths = new int[N_INTERVALS+1];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationTimeInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationTimeInfo) {
			if (SimulationTimeInfo.Type.END.equals(((SimulationTimeInfo) info).getType())) {
				out.print("Age\tPatients\tDiabetes");
				if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
					out.print("\tEARM\tGA\tCNV");
				}
				if (diseases.contains(RETALSimulation.DISEASES.DR)) {
					out.print("\tNPDR\tNonHRPDR\tHRPDR\tCSME");
				}
				out.println("\tDeaths");
				out.print("TOTAL\t" + nPatients[N_INTERVALS] + "\t" + nDiabetes[N_INTERVALS]);
				if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
					out.print("\t" + nEARM[N_INTERVALS] + "\t" + nGA[N_INTERVALS]	+ "\t" + nCNV[N_INTERVALS]);
				}
				if (diseases.contains(RETALSimulation.DISEASES.DR)) {
					out.print("\t" + nNPDR[N_INTERVALS] + "\t" + nNonHRPDR[N_INTERVALS] + "\t" + nHRPDR[N_INTERVALS] + "\t" + nCSME[N_INTERVALS]);
				}
				out.println("\t" + nDeaths[N_INTERVALS]);
				for (int i = 0; i < N_INTERVALS; i++) {
					out.print(AGES[i][0] + "\t" + nPatients[i] + "\t" + nDiabetes[i]);
					if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
						out.print("\t" + nEARM[i] + "\t" + nGA[i] + "\t" + nCNV[i]);
					}
					if (diseases.contains(RETALSimulation.DISEASES.DR)) {
						out.print("\t" + nNPDR[i] + "\t" + nNonHRPDR[i] + "\t" + nHRPDR[i] + "\t" + nCSME[i]);
					}
					out.println("\t" + nDeaths[i]);
				}
			}
		}
		else {
			PatientInfo pInfo = (PatientInfo) info;
			RetalPatient pat = pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				final double initAge = pat.getInitAge(); 
				final double ageAtDeath = pat.getAge();
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

					final double ageAtDiabetes = pat.getAgeAtDiabetes();
					if (ageAtDiabetes != Double.MAX_VALUE) {
						for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]); i++) {
							if (ageAtDiabetes < AGES[i][1]) {
								nDiabetes[i]++;
							}
						}
						nDiabetes[N_INTERVALS]++;						
					}
					if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
						final double ageAtEARM = (pat.getTimeToEyeState(EyeState.EARM, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (pat.getTimeToEyeState(EyeState.EARM, 0) / 365.0));
						final double ageAtGA = (pat.getTimeToEyeState(EyeState.AMD_GA, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.AMD_GA, 0), pat.getTimeToEyeState(EyeState.AMD_GA, 1)) / 365.0));
						final double ageAtCNV = (pat.getTimeToEyeState(EyeState.AMD_CNV, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.AMD_CNV, 0), pat.getTimeToEyeState(EyeState.AMD_CNV, 1)) / 365.0));
						final double ageAtAMD = Math.min(ageAtGA, ageAtCNV);
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
					if (diseases.contains(RETALSimulation.DISEASES.DR)) {
						final double ageAtNPDR = (pat.getTimeToEyeState(EyeState.NPDR, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.NPDR, 0), pat.getTimeToEyeState(EyeState.NPDR, 1)) / 365.0));						
						final double ageAtNonHRPDR = (pat.getTimeToEyeState(EyeState.NON_HR_PDR, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.NON_HR_PDR, 0), pat.getTimeToEyeState(EyeState.NON_HR_PDR, 1)) / 365.0));						
						final double ageAtHRPDR = (pat.getTimeToEyeState(EyeState.NPDR, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.HR_PDR, 0), pat.getTimeToEyeState(EyeState.HR_PDR, 1)) / 365.0));						
						final double ageAtCSME = (pat.getTimeToEyeState(EyeState.CSME, 0) == Long.MAX_VALUE) ? Double.MAX_VALUE : (initAge + (Math.min(pat.getTimeToEyeState(EyeState.CSME, 0), pat.getTimeToEyeState(EyeState.CSME, 1)) / 365.0));
						if (ageAtNPDR != Double.MAX_VALUE) {
							for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]) && ((ageAtNonHRPDR > AGES[i][0]) && (ageAtHRPDR > AGES[i][0])); i++) {
								if (ageAtNPDR < AGES[i][1]) {
									nNPDR[i]++;
								}
							}
							if ((ageAtNonHRPDR >= AGES[0][0]) && (ageAtHRPDR >= AGES[0][0]))
								nNPDR[N_INTERVALS]++;
						}
						if (ageAtNonHRPDR != Double.MAX_VALUE) {
							for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]) && (ageAtHRPDR > AGES[i][0]); i++) {
								if (ageAtNonHRPDR < AGES[i][1]) {
									nNonHRPDR[i]++;
								}
							}
							if (ageAtHRPDR >= AGES[0][0])
								nNonHRPDR[N_INTERVALS]++;
						}
						if (ageAtHRPDR != Double.MAX_VALUE) {
							for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]); i++) {
								if (ageAtHRPDR < AGES[i][1]) {
									nHRPDR[i]++;
								}
							}
							nHRPDR[N_INTERVALS]++;
						}
						if (ageAtCSME != Double.MAX_VALUE) {
							for (int i = 0; (i < N_INTERVALS) && (ageAtDeath > AGES[i][0]); i++) {
								if (ageAtCSME < AGES[i][1]) {
									nCSME[i]++;
								}
							}
							nCSME[N_INTERVALS]++;
						}
						
					}					
				}
			}
		}
	}
}
