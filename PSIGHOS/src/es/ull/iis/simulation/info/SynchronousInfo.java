package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public abstract class SynchronousInfo extends TimeStampedInfo {

	public SynchronousInfo(Simulation model, long ts) {
		super(model, ts);
	}

}
