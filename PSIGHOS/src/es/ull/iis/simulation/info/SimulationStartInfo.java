package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public class SimulationStartInfo extends AsynchronousInfo {

	final private long cpuTime;
	
	public SimulationStartInfo(Simulation model, long cpuTime, long ts) {
		super(model, ts);
		this.cpuTime = cpuTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public String toString() {
		return simul.long2SimulationTime(getTs()) + "\t[SIM]\tSIMULATION START\t " + simul.getDescription();
	}
}
