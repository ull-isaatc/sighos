package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class TimeStampedInfo extends SimulationInfo {

	public final double ts;
	
	TimeStampedInfo(Simulation simul, double ts) {
		super(simul);
		this.ts = ts;
	}

	public double getTs() {
		return ts;
	}

}
