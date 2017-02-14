package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(Simulation simul, long ts) {
		super(simul, ts);
	}
	
	public String toString() {
		return simul.long2SimulationTime(getTs()) + "\t[SIM]\tCLOCK AVANCED";
	}
}
