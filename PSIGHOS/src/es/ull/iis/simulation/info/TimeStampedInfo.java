package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

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
