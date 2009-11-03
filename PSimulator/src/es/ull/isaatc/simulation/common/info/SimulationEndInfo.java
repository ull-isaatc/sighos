package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Model;

public class SimulationEndInfo extends AsynchronousInfo {

	private long cpuTime;

	public SimulationEndInfo(Model simul, long cpuTime, double ts) {
		super(simul, ts);
		this.cpuTime = cpuTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public String toString() {
		return "SIMULATION END INFO";
	}
}
