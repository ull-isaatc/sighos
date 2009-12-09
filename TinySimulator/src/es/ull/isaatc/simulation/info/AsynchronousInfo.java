package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	AsynchronousInfo(Simulation simul, long ts) {
		super(simul, ts);
	}

}
