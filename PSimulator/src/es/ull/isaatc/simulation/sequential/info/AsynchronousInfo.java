package es.ull.isaatc.simulation.sequential.info;

import es.ull.isaatc.simulation.sequential.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation simul, double ts) {
		super(simul, ts);
	}

}
