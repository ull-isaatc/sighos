package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation<?> simul, long ts) {
		super(simul, ts);
	}

}
