package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public class SimulationEndInfo extends AsynchronousInfo {

	final private long cpuTime;

	public SimulationEndInfo(Simulation model, long cpuTime, long ts) {
		super(model, ts);
		this.cpuTime = cpuTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public String toString() {
		return  simul.long2SimulationTime(getTs()) + "\t[SIM]\tSIMULATION END INFO";
	}
}
