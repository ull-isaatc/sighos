package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Model;

public abstract class TimeStampedInfo extends SimulationInfo {

	public final double ts;
	
	TimeStampedInfo(Model simul, double ts) {
		super(simul);
		this.ts = ts;
	}

	public double getTs() {
		return ts;
	}

}
