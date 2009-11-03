package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Model;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Model simul, double ts) {
		super(simul, ts);
	}

}
