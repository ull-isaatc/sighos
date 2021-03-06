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
import es.ull.iis.simulation.hta.retal.params.CNVStage;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;

/**
 * @author Iv�n Castilla
 *
 */
public class AffectedPatientHistoryView extends FilteredListener {
	private final PrintStream out = System.out;
	private final EyeState[] STATES_ARMD = {EyeState.EARM, EyeState.AMD_GA, EyeState.AMD_CNV};
	private final EyeState[] STATES_DR = {EyeState.NPDR, EyeState.NON_HR_PDR, EyeState.HR_PDR, EyeState.CSME};
	private final boolean detailed;
	

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(EnumSet<RETALSimulation.DISEASES> diseases, boolean detailed, boolean includeDiabetes, EyeState filterARMDByState, EyeState filterDRByState) {
		super("Standard patient viewer", diseases, includeDiabetes, filterARMDByState, filterDRByState);
		this.detailed = detailed;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}
	
	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(EnumSet<RETALSimulation.DISEASES> diseases) {
		this(diseases, false, false, EyeState.EARM, EyeState.NPDR);
	}

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(EnumSet<RETALSimulation.DISEASES> diseases, boolean includeDiabetes) {
		this(diseases, false, includeDiabetes, EyeState.EARM, EyeState.NPDR);
	}

	private String getAgeAt(RetalPatient pat, EyeState state, int eye) {
		final double ageAt = pat.getAgeAt(state, eye);
		return (ageAt == Double.MAX_VALUE) ? "INF" : ("" + ageAt); 
	}
	
	private String getAgeAt(RetalPatient pat, CNVStage stage, int eye) {
		final double ageAt = pat.getAgeAt(stage, eye);
		return (ageAt == Double.MAX_VALUE) ? "INF" : ("" + ageAt); 
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.START.equals(((SimulationStartStopInfo) info).getType())) {
				final StringBuilder str = new StringBuilder("RetalPatient\tINIT_AGE\tDIABETES\t");
				if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
					for (int i = 0; i < STATES_ARMD.length; i++)
						str.append(STATES_ARMD[i]).append("_E1\t").append(STATES_ARMD[i]).append("_E2\t");
					if (detailed) {
						for (CNVStage stage : CNVStage.ALL_STAGES) { 
							str.append(stage.getType()).append("_").append(stage.getPosition()).append("_E1\t");
							str.append(stage.getType()).append("_").append(stage.getPosition()).append("_E2\t");
						}
					}
				}
				if (diseases.contains(RETALSimulation.DISEASES.DR)) {
					for (int i = 0; i < STATES_DR.length; i++)
						str.append(STATES_DR[i]).append("_E1\t").append(STATES_DR[i]).append("_E2\t");
				}
				out.println(str.append("DEATH"));				
			}
		} 
		else {
			PatientInfo pInfo = (PatientInfo) info;
			RetalPatient pat = (RetalPatient) pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				if (checkFilter(pat)) {
					final StringBuilder str = new StringBuilder(pat.toString()).append("\t").append(pat.getInitAge()).append("\t").append(pat.getAgeAtDiabetes()).append("\t");
					if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
						for (int i = 0; i < STATES_ARMD.length; i++)
							str.append(getAgeAt(pat, STATES_ARMD[i], 0)).append("\t").append(getAgeAt(pat, STATES_ARMD[i], 1)).append("\t");
						if (detailed) {
							for (CNVStage stage : CNVStage.ALL_STAGES)
								str.append(getAgeAt(pat, stage, 0)).append("\t").append(getAgeAt(pat, stage, 1)).append("\t");
						}
					}
					if (diseases.contains(RETALSimulation.DISEASES.DR)) {
						for (int i = 0; i < STATES_DR.length; i++)
							str.append(getAgeAt(pat, STATES_DR[i], 0)).append("\t").append(getAgeAt(pat, STATES_DR[i], 1)).append("\t");
					}
					out.println(str.append(pat.getAge()));
				}
			}
		}
	}
}
