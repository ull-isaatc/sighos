package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(Simulation simul, double ts) {
		super(simul, ts);
	}
	
	public String toString() {
		return "\n" + simul.double2SimulationTime(getTs()) + "\tCLOCK AVANCED";
	}
}
