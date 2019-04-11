package es.ull.iis.simulation.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;

public class StdInfoView extends Listener {

	long simulationInit = 0;
	double lastTimeChange = 0;
	
	private final PrintStream out = System.out;

	public StdInfoView() {
		super("STANDARD INFO VIEW");
		addEntrance(SimulationStartStopInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(EntityLocationInfo.class);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		out.println(info.toString());
		if (info instanceof SimulationStartStopInfo) { 
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.START.equals(tInfo.getType()))  {
				simulationInit = tInfo.getCpuTime();
			}
			else if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
				out.println("CPU Time = " + ((tInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
			}
		} 
	}

}
