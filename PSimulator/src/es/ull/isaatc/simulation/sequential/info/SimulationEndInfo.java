package es.ull.isaatc.simulation.sequential.info;

import es.ull.isaatc.simulation.sequential.Simulation;

public class SimulationEndInfo extends AsynchronousInfo {

	private long cpuTime;

	public SimulationEndInfo(Simulation simul, long cpuTime, double ts) {
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
