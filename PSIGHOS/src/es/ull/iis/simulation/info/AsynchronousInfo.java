package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public abstract class AsynchronousInfo extends TimeStampedInfo {

	public AsynchronousInfo(Simulation model, long ts) {
		super(model, ts);
	}

}
