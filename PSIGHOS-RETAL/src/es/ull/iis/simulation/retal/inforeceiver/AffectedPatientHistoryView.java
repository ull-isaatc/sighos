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

/**
 * @author Iván Castilla
 *
 */
public class AffectedPatientHistoryView extends Listener {
	private final PrintStream out = System.out;
	

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryView(Simulation simul) {
		super(simul, "Standard patient viewer");
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartInfo.class);
	}

	private String getAgeAt(OphthalmologicPatient pat, EyeState state, int eye) {
		final double ageAt = pat.getAgeAt(state, eye);
		return (ageAt == Double.MAX_VALUE) ? "INF" : ("" + ageAt); 
	}
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			out.println("Patient\tINIT_AGE\tEARM\tGA1\tCNV1\tGA2\tCNV2\tDEATH");
		}
		else {
			PatientInfo pInfo = (PatientInfo) info;
			OphthalmologicPatient pat = (OphthalmologicPatient) pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				if (pat.getTimeToEARM(0) != Long.MAX_VALUE || pat.getTimeToGA(0) != Long.MAX_VALUE || pat.getTimeToCNV(0) != Long.MAX_VALUE)
					out.println(pat + "\t" + pat.getInitAge() + "\t" + getAgeAt(pat, EyeState.EARM, 0) + "\t" 
							+ getAgeAt(pat, EyeState.AMD_GA, 0) + "\t" + getAgeAt(pat, EyeState.AMD_CNV, 0) + "\t" 
							+ getAgeAt(pat, EyeState.AMD_GA, 1) + "\t" + getAgeAt(pat, EyeState.AMD_CNV, 1)+ "\t" + pat.getAge());
			}
		}
	}
}
