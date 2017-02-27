package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Model model, long ts) {
		super(model, ts);
	}

}
