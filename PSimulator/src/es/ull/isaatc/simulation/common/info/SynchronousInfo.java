package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation simul, long ts) {
		super(simul, ts);
	}

}
