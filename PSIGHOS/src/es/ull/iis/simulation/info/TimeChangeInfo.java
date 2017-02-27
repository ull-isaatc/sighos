package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public class TimeChangeInfo extends AsynchronousInfo {

	public TimeChangeInfo(Model model, long ts) {
		super(model, ts);
	}
	
	public String toString() {
		return model.long2SimulationTime(getTs()) + "\t[SIM]\tCLOCK AVANCED";
	}
}
