/**
 * 
 */
package es.ull.iis.simulation.hta.retal.inforeceiver;

import java.io.PrintStream;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RETALSimulation;
import es.ull.iis.simulation.hta.retal.info.PatientInfo;
import es.ull.iis.simulation.hta.retal.params.VAProgressionPair;
import es.ull.iis.simulation.info.SimulationInfo;

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
			RetalPatient pat = (RetalPatient) pInfo.getPatient();
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
