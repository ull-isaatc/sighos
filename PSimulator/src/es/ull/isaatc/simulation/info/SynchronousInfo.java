package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.common.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation simul, double ts) {
		super(simul, ts);
	}

}
