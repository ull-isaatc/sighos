package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation simul, long ts) {
		super(simul, ts);
	}

}
