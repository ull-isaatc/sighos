package es.ull.isaatc.simulation.common.inforeceiver;

import java.io.PrintStream;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;

public class StdInfoView extends View {

	long simulationInit = 0;
	double lastTimeChange = 0;
	
	private final PrintStream out = System.out;

	public StdInfoView(Simulation simul) {
		super(simul, "STANDARD INFO VIEW");
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
					+ (endInfo.getCpuTime() - simulationInit) + " miliseconds.");
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
