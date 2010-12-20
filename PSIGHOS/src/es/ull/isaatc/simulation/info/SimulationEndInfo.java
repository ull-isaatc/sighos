package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public class SimulationEndInfo extends AsynchronousInfo {

	final private long cpuTime;

	public SimulationEndInfo(Simulation simul, long cpuTime, long ts) {
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
