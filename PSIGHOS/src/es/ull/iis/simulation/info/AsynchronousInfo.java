package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation<?> simul, long ts) {
		super(simul, ts);
	}

}
