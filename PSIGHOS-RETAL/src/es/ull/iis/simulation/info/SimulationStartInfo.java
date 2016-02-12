package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Simulation;

public class SimulationStartInfo extends AsynchronousInfo {

	final private long cpuTime;
	
	public SimulationStartInfo(Simulation simul, long cpuTime, long ts) {
		super(simul, ts);
		this.cpuTime = cpuTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public String toString() {
		return "SIMULATION START INFO: " + simul.getDescription() + "\n";
	}
}
