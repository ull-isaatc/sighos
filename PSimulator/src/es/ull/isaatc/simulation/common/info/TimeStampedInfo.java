package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Simulation;

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
