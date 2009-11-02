package es.ull.isaatc.simulation.sequential.info;

import es.ull.isaatc.simulation.sequential.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation simul, double ts) {
		super(simul, ts);
	}

}
