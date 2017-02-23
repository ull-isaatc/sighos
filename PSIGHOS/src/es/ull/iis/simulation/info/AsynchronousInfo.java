package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(SimulationEngine simul, long ts) {
		super(simul, ts);
	}

}
