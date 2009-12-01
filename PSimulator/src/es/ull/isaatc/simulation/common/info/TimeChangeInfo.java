package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Simulation;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(Simulation simul, long ts) {
		super(simul, ts);
	}
	
	public String toString() {
		return "\n" + simul.long2SimulationTime(getTs()) + "\tCLOCK AVANCED";
	}
}
