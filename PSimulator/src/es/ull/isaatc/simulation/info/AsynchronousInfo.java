package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation simul, double ts) {
		super(simul, ts);
	}

}
