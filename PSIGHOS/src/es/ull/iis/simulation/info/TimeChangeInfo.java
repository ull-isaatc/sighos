package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(SimulationEngine simul, long ts) {
		super(simul, ts);
	}
	
	public String toString() {
		return simul.long2SimulationTime(getTs()) + "\t[SIM]\tCLOCK AVANCED";
	}
}
