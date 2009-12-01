package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation simul, long ts) {
		super(simul, ts);
	}

}
