package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

public abstract class UserInfo extends AsynchronousInfo {

	public UserInfo(Simulation simul, long ts) {
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
