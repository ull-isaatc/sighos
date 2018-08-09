/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.CNVStage;

/**
 * @author Iván Castilla
 *
 */
public class PatientCounterHistogramView extends Listener {
	private final EnumSet<RETALSimulation.DISEASES> diseases;
	private final int length;
	private final int nIntervals;
	private final int minAge;
	private final boolean detailDeaths; 
	private final boolean detailCNV; 
	private final int [] nPatients;
	private final EnumMap<EyeState, int[][]> nEyeState;
	private final TreeMap<CNVStage, int[][]> nCNVStage; 
	private final int [][] nDeaths;
	private final int [] nDiabetes;
	
	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 */
	public PatientCounterHistogramView(int minAge, int maxAge, int length, EnumSet<RETALSimulation.DISEASES> diseases) {
		this(minAge, maxAge, length, diseases, false, false);
	}

	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 * @param detailDeaths
	 */
	public PatientCounterHistogramView(int minAge, int maxAge, int length, EnumSet<RETALSimulation.DISEASES> diseases, boolean detailDeaths, boolean detailCNV) {
		super("Counter of patients");
		this.diseases = diseases;
		this.length = length;
		this.nIntervals = ((maxAge - minAge) / length) + 1;
		this.minAge = minAge;
		this.detailDeaths = detailDeaths;
		this.detailCNV = detailCNV;
		nPatients = new int[nIntervals];
		nDiabetes = new int[nIntervals];
		nDeaths = new int[4][nIntervals];
		nEyeState = new EnumMap<EyeState, int[][]>(EyeState.class);
		// TODO: Manually filled by now... but expected to be automatized
		if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
			nEyeState.put(EyeState.EARM, new int[nIntervals][2]);
			nEyeState.put(EyeState.AMD_GA, new int[nIntervals][2]);
			nEyeState.put(EyeState.AMD_CNV, new int[nIntervals][2]);
		}
		if (diseases.contains(RETALSimulation.DISEASES.DR)) {
			nEyeState.put(EyeState.NPDR, new int[nIntervals][2]);
			nEyeState.put(EyeState.NON_HR_PDR, new int[nIntervals][2]);
			nEyeState.put(EyeState.HR_PDR, new int[nIntervals][2]);
			nEyeState.put(EyeState.CSME, new int[nIntervals][2]);
		}
		if (detailCNV && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
			nCNVStage = new TreeMap<CNVStage, int[][]>();
			for (CNVStage stage : CNVStage.ALL_STAGES) { 
				nCNVStage.put(stage , new int[nIntervals][2]);
			}
		}
		else {
			nCNVStage = null;
		}
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			final StringBuilder strHead = new StringBuilder("AGE\tBASE\tDIABETES");
			for (EyeState state : nEyeState.keySet())
				strHead.append("\t").append(state.toString()).append("_1E\t").append(state.toString()).append("_2E");
			if (detailCNV && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
				for (CNVStage stage : CNVStage.ALL_STAGES) { 
					strHead.append("\t").append(stage.getType()).append("_").append(stage.getPosition()).append("_1E\t");
					strHead.append(stage.getType()).append("_").append(stage.getPosition()).append("_2E");
				}
			}
			if (detailDeaths) {
				strHead.append("\tDEATH[NO_ARM]\tDEATH[EARM]\tDEATH[GA]\tDEATH[CNV]");
			}
			else {
				strHead.append("\tDEATH");
			}
			System.out.println(strHead);
			for (int i = 0; i < nIntervals; i++) {
				final StringBuilder str = new StringBuilder((minAge + i * length) + "\t" + nPatients[i] + "\t" + nDiabetes[i]); 
				for (EyeState state : nEyeState.keySet())
					str.append("\t").append(nEyeState.get(state)[i][0]).append("\t").append(nEyeState.get(state)[i][1]);
				if (detailCNV && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
					for (CNVStage stage : CNVStage.ALL_STAGES) 
						str.append("\t").append(nCNVStage.get(stage)[i][0]).append("\t").append(nCNVStage.get(stage)[i][1]);
				}
				if (detailDeaths) {
					for (int j = 0; j < nDeaths.length; j++)
						str.append("\t").append(nDeaths[j][i]);
				}
				else {
					str.append("\t").append(nDeaths[0][i]);
				}
				System.out.println(str);
			}
		}
		else if (info instanceof PatientInfo) {
			final PatientInfo p = (PatientInfo) info;
			final Patient pat = (Patient)p.getPatient();
			final int interval = (int)((pat.getAge() - minAge) / length);
			final int eyeIndex = p.getEyeIndex();
			switch(p.getType()) {
				case START:
					nPatients[interval]++; 
					break;
				case CHANGE_EYE_STATE:
					nEyeState.get(p.getToState())[interval][eyeIndex]++;
					break;
				case CHANGE_CNV_STAGE:
					if (detailCNV && diseases.contains(RETALSimulation.DISEASES.ARMD)) {
						nCNVStage.get(p.getToCNVStage())[interval][eyeIndex]++;
					}
					break;
				case DIABETES:
					nDiabetes[interval]++;
					break;
				case DEATH:
					if (detailDeaths) {
						final EnumSet<EyeState> eye1 = pat.getEyeState(0);
						final EnumSet<EyeState> eye2 = pat.getEyeState(1);
						if (eye1.contains(EyeState.AMD_CNV) || eye2.contains(EyeState.AMD_CNV))
							nDeaths[3][interval]++;
						else if (eye1.contains(EyeState.AMD_GA) || eye2.contains(EyeState.AMD_GA))
							nDeaths[2][interval]++;
						else if (eye1.contains(EyeState.EARM) || eye2.contains(EyeState.EARM))
							nDeaths[1][interval]++;
						else
							nDeaths[0][interval]++;
					}
					else
						nDeaths[0][interval]++;
					break;
				default:
					break;
			}
		}
	}

}
