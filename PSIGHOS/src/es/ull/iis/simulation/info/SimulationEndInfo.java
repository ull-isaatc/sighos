package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public class SimulationEndInfo extends AsynchronousInfo {

	final private long cpuTime;

	public SimulationEndInfo(Model model, long cpuTime, long ts) {
		super(model, ts);
		this.cpuTime = cpuTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public String toString() {
		return  model.long2SimulationTime(getTs()) + "\t[SIM]\tSIMULATION END INFO";
	}
}
