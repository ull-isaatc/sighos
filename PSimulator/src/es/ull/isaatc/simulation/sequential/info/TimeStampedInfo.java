package es.ull.isaatc.simulation.sequential.info;

import es.ull.isaatc.simulation.sequential.Simulation;

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
