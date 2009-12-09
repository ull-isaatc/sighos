package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class TimeStampedInfo extends SimulationInfo {

	public final long ts;
	
	TimeStampedInfo(Simulation simul, long ts) {
		super(simul);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

}
