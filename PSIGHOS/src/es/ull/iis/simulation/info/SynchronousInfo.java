package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Model model, long ts) {
		super(model, ts);
	}

}
