package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class UserInfo extends AsynchronousInfo {

	public UserInfo(SimulationEngine simul, long ts) {
		super(simul, ts);
	}

	public boolean finalInfo = false;

	public boolean isFinalInfo() {
		return finalInfo;
	}

	public void setFinalInfo(boolean finalInfo) {
		this.finalInfo = finalInfo;
	}
		
}
