package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.core.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation simul, long ts) {
		super(simul, ts);
	}

}
