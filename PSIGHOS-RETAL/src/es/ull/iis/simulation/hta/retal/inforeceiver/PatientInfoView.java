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
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
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
		addEntrance(SimulationStartStopInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo)info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
				out.println(info.toString() + ": CPU Time = " 
						+ ((tInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");				
			}
			else if (SimulationStartStopInfo.Type.START.equals(tInfo.getType())) {
				simulationInit = tInfo.getCpuTime();
				out.println(info.toString());
			}
			else {
				out.println(info.toString());				
			}
		} else {
			out.println(info.toString());
		}
	}
}
