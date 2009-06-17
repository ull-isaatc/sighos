package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public class SimulationEndInfo extends AsynchronousInfo {

	private long cpuTime;
	private long lpTime;
	private long evTime;
	private long evRawTime;

	public SimulationEndInfo(Simulation simul, long cpuTime, long lpTime, long evRawTime, long evTime, double ts) {
		super(simul, ts);
		this.cpuTime = cpuTime;
		this.evTime = evTime;
		this.lpTime = lpTime;
		this.evRawTime = evRawTime;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	/**
	 * @return the lpTime
	 */
	public long getLpTime() {
		return lpTime;
	}

	/**
	 * @return the evTime
	 */
	public long getEvTime() {
		return evTime;
	}

	/**
	 * @return the evTime
	 */
	public long getEvRawTime() {
		return evRawTime;
	}

	public String toString() {
		return "SIMULATION END INFO";
	}
}
