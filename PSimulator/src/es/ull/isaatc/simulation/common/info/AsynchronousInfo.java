package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Model;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Model simul, double ts) {
		super(simul, ts);
	}

}
