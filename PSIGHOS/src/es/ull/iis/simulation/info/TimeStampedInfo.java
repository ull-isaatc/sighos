package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class TimeStampedInfo extends SimulationInfo {

	final protected long ts;
	
	TimeStampedInfo(SimulationEngine simul, long ts) {
		super(simul);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

}
