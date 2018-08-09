/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class T1DMPatientInfoView extends Listener {
	private final PrintStream out = System.out;
	long simulationInit = 0;
	

	/**
	 * @param simul
	 */
	public T1DMPatientInfoView() {
		super("Standard patient viewer");
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) { 
			final SimulationEndInfo endInfo = (SimulationEndInfo) info;
			out.println(info.toString() + ": CPU Time = " 
					+ ((endInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
		} else if (info instanceof SimulationStartInfo) {
			final SimulationStartInfo startInfo = (SimulationStartInfo) info;
			simulationInit = startInfo.getCpuTime();
			out.println(info.toString());
		} else if (info instanceof T1DMPatientInfo){
			out.println(info.toString());
		}
		
	}
}
