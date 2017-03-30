/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.io.PrintStream;
import java.util.EnumSet;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.VAProgressionPair;

/**
 * @author Iván Castilla
 *
 */
public class AffectedPatientHistoryVAView extends FilteredListener {
	private final PrintStream out = System.out;

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryVAView(EnumSet<RETALSimulation.DISEASES> diseases, boolean includeDiabetes, EyeState filterARMDByState, EyeState filterDRByState) {
		super("Standard patient viewer", diseases, includeDiabetes, filterARMDByState, filterDRByState);
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}

	/**
	 * @param simul
	 */
	public AffectedPatientHistoryVAView(EnumSet<RETALSimulation.DISEASES> diseases, boolean includeDiabetes) {
		this(diseases, includeDiabetes, EyeState.EARM, EyeState.NPDR);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof PatientInfo) {
			PatientInfo pInfo = (PatientInfo) info;
			Patient pat = (Patient) pInfo.getPatient();
			if (pInfo.getType() == PatientInfo.Type.FINISH) {
				if (checkFilter(pat)) {
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
