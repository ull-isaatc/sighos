package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public class SimulationStartInfo extends AsynchronousInfo {

	private long cpuTime;
	
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
