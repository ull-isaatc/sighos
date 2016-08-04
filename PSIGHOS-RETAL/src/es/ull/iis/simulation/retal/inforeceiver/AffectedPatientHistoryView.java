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

	private String getAgeAt(OphthalmologicPatient pat, EyeState state, boolean firstEye) {
		final double ageAt = pat.getAgeAt(state, firstEye);
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
				if (pat.getTimeToEARM() != Long.MAX_VALUE || pat.getTimeToGA() != Long.MAX_VALUE || pat.getTimeToCNV() != Long.MAX_VALUE)
					out.println(pat + "\t" + pat.getInitAge() + "\t" + getAgeAt(pat, EyeState.EARM, true) + "\t" 
							+ getAgeAt(pat, EyeState.AMD_GA, true) + "\t" + getAgeAt(pat, EyeState.AMD_CNV, true) + "\t" 
							+ getAgeAt(pat, EyeState.AMD_GA, false) + "\t" + getAgeAt(pat, EyeState.AMD_CNV, false)+ "\t" + pat.getAge());
			}
		}
	}
}
