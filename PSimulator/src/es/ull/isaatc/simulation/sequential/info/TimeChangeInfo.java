package es.ull.isaatc.simulation.sequential.info;

import es.ull.isaatc.simulation.sequential.Simulation;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(Simulation simul, double ts) {
		super(simul, ts);
	}
	
	public String toString() {
		return "\n" + simul.double2SimulationTime(getTs()) + "\tCLOCK AVANCED";
	}
}
