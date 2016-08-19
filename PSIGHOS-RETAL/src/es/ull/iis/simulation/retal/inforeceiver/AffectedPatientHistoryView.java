/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.CNVStage;

/**
 * @author Iván Castilla
 *
 */
public class AffectedPatientHistoryView extends Listener {
	private final PrintStream out = System.out;
	private final static EyeState[] STATES = {EyeState.EARM, EyeState.AMD_GA, EyeState.AMD_CNV};
	private final boolean detailed;
	private final EyeState filterByState;
	

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(Simulation simul, boolean detailed, EyeState filterByState) {
		super(simul, "Standard patient viewer");
		this.detailed = detailed;
		this.filterByState = filterByState;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartInfo.class);
	}
	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(Simulation simul) {
		this(simul, false, EyeState.EARM);
	}

	private String getAgeAt(OphthalmologicPatient pat, EyeState state, int eye) {
		final double ageAt = pat.getAgeAt(state, eye);
		return (ageAt == Double.MAX_VALUE) ? "INF" : ("" + ageAt); 
	}
	
	private String getAgeAt(OphthalmologicPatient pat, CNVStage stage, int eye) {
		final double ageAt = pat.getAgeAt(stage, eye);
		return (ageAt == Double.MAX_VALUE) ? "INF" : ("" + ageAt); 
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			final StringBuilder str = new StringBuilder("Patient\tINIT_AGE\t");
			for (int i = 0; i < STATES.length; i++)
				str.append(STATES[i]).append("_E1\t").append(STATES[i]).append("_E2\t");
			if (detailed) {
				for (CNVStage stage : CNVStage.ALL_STAGES) { 
					str.append(stage.getType()).append("_").append(stage.getPosition()).append("_E1\t");
					str.append(stage.getType()).append("_").append(stage.getPosition()).append("_E2\t");
				}
			}
			out.println(str.append("DEATH"));
		}
		else {
			PatientInfo pInfo = (PatientInfo) info;
			OphthalmologicPatient pat = (OphthalmologicPatient) pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				boolean condition = false;
				if (filterByState == EyeState.AMD_CNV) {
					condition = condition || (pat.getTimeToCNV(0) != Long.MAX_VALUE || pat.getTimeToCNV(1) != Long.MAX_VALUE);
				}
				else if (filterByState == EyeState.AMD_GA) {
					condition = condition || (pat.getTimeToCNV(0) != Long.MAX_VALUE || pat.getTimeToCNV(1) != Long.MAX_VALUE)
							 || (pat.getTimeToGA(0) != Long.MAX_VALUE || pat.getTimeToGA(1) != Long.MAX_VALUE);					
				}
				else if (filterByState == EyeState.EARM) {
					condition = condition || (pat.getTimeToEARM(0) != Long.MAX_VALUE || pat.getTimeToGA(0) != Long.MAX_VALUE || pat.getTimeToCNV(0) != Long.MAX_VALUE);
				}
				if (condition) {
					final StringBuilder str = new StringBuilder(pat.toString()).append("\t").append(pat.getInitAge()).append("\t");
					for (int i = 0; i < STATES.length; i++)
						str.append(getAgeAt(pat, STATES[i], 0)).append("\t").append(getAgeAt(pat, STATES[i], 1)).append("\t");
					if (detailed) {
						for (CNVStage stage : CNVStage.ALL_STAGES)
							str.append(getAgeAt(pat, stage, 0)).append("\t").append(getAgeAt(pat, stage, 1)).append("\t");
					}
					out.println(str.append(pat.getAge()));
				}
			}
		}
	}
}
