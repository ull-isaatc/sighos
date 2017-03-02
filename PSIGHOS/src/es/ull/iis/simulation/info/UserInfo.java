package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public abstract class UserInfo extends AsynchronousInfo {

	public UserInfo(Simulation model, long ts) {
		super(model, ts);
	}

	public boolean finalInfo = false;

	public boolean isFinalInfo() {
		return finalInfo;
	}

	public void setFinalInfo(boolean finalInfo) {
		this.finalInfo = finalInfo;
	}
		
}
