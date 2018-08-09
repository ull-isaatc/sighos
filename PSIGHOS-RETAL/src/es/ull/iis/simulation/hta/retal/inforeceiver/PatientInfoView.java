/**
 * 
 */
package es.ull.iis.simulation.hta.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.retal.info.PatientInfo;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class PatientInfoView extends Listener {
	private final PrintStream out = System.out;
	long simulationInit = 0;
	double lastTimeChange = 0;
	

	/**
	 * @param simul
	 */
	public PatientInfoView() {
		super("Standard patient viewer");
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(TimeChangeInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) { 
			SimulationEndInfo endInfo = (SimulationEndInfo) info;
			out.println(info.toString() + ": CPU Time = " 
					+ ((endInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
		} else {
			if (info instanceof SimulationStartInfo) {
				SimulationStartInfo startInfo = (SimulationStartInfo) info;
				simulationInit = startInfo.getCpuTime();
				out.println(info.toString());
			} else {
				out.println(info.toString());
			}
		}
	}
}
