package es.ull.iis.simulation.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;

public class StdInfoView extends Listener {

	long simulationInit = 0;
	double lastTimeChange = 0;
	
	private final PrintStream out = System.out;

	public StdInfoView() {
		super("STANDARD INFO VIEW");
		addEntrance(SimulationTimeInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		out.println(info.toString());
		if (info instanceof SimulationTimeInfo) { 
			final SimulationTimeInfo tInfo = (SimulationTimeInfo) info;
			if (SimulationTimeInfo.Type.START.equals(tInfo.getType()))  {
				simulationInit = tInfo.getCpuTime();
			}
			else if (SimulationTimeInfo.Type.END.equals(tInfo.getType())) {
				out.println("CPU Time = " + ((tInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
			}
		} 
	}

}
