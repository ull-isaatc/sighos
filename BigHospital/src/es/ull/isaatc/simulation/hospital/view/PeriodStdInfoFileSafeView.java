package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.common.info.TimeStampedInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

public class PeriodStdInfoFileSafeView extends View {
	private PrintWriter buffer = null;
	long simulationInit = 0;
	final private long start;
	final private long end;
	
	public PeriodStdInfoFileSafeView(Simulation simul, TimeStamp start, TimeStamp end, String fileName) {
		super(simul, "STANDARD INFO VIEW");
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(TimeChangeInfo.class);
		this.start = simul.getTimeUnit().convert(start);
		this.end = simul.getTimeUnit().convert(end);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) { 
			SimulationEndInfo endInfo = (SimulationEndInfo) info;
			buffer.println(info.toString() + ": CPU Time = " 
					+ ((endInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
			buffer.close();
		} else {
			if (info instanceof SimulationStartInfo) {
				SimulationStartInfo startInfo = (SimulationStartInfo) info;
				simulationInit = startInfo.getCpuTime();
				buffer.println(info.toString());
			} else {
				final long ts = ((TimeStampedInfo)info).getTs();
				if (ts >= start && ts < end)
					buffer.println(info.toString());
			}
		}
	}

}
