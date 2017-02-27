package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public abstract class UserInfo extends AsynchronousInfo {

	public UserInfo(Model model, long ts) {
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
