package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.core.Simulation;

public abstract class TimeStampedInfo extends SimulationInfo {

	final protected long ts;
	
	TimeStampedInfo(Simulation simul, long ts) {
		super(simul);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

}
