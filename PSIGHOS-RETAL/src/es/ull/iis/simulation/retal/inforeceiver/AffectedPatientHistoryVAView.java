/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.VAProgressionPair;

/**
 * @author Iván Castilla
 *
 */
public class AffectedPatientHistoryVAView extends Listener {
	private final PrintStream out = System.out;
	private final EyeState filterByState;

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryVAView(Simulation simul, EyeState filterByState) {
		super(simul, "Standard patient viewer");
		this.filterByState = filterByState;		
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryVAView(Simulation simul) {
		this(simul, EyeState.EARM);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof PatientInfo) {
			PatientInfo pInfo = (PatientInfo) info;
			Patient pat = (Patient) pInfo.getPatient();
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
					final double startAge = pat.getInitAge();
					out.print(pat.toString() + "\tEye 1\t");
					long ts = pat.getStartTs();
					for (VAProgressionPair pair : pat.getVaProgression(0)) {
						ts += pair.timeToChange;
						out.print((startAge + (ts / 365.0)) + "\t" + pair.va + "\t");
					}
					out.println();
					ts = pat.getStartTs();
					out.print(pat.toString() + "\tEye 2\t");
					for (VAProgressionPair pair : pat.getVaProgression(1)) {
						ts += pair.timeToChange;
						out.print((startAge + (ts / 365.0)) + "\t" + pair.va + "\t");
					}
					out.println();
				}
			}
		}
	}
}
