package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(SimulationEngine simul, long ts) {
		super(simul, ts);
	}

}
