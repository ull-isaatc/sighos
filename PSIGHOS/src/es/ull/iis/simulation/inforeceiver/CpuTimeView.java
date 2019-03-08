/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;

/**
 * A listener to compute the CPU time of the simulation 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class CpuTimeView extends Listener {
	protected long iniT = Long.MAX_VALUE;
	protected long endT = Long.MAX_VALUE;
	private final boolean print;
	private final PrintStream out;
	
	public CpuTimeView(final PrintStream out, final boolean print) {
		super("CPU Time viewer");
		addEntrance(SimulationTimeInfo.class);
		this.print = print;
		this.out = out;
	}

	@Override
	public void infoEmited(final SimulationInfo info) {
		final SimulationTimeInfo tInfo = (SimulationTimeInfo)info;
		
		if (SimulationTimeInfo.Type.START.equals(tInfo.getType())) {
			iniT = tInfo.getCpuTime();
		}
		else if (SimulationTimeInfo.Type.END.equals(tInfo.getType())) {
			endT = tInfo.getCpuTime();
			if (print)
				out.println(this);
		}
	}

	@Override
	public String toString() {
		if (endT == Long.MAX_VALUE)
			return "CPU time not available (simulation not finished)";
		return "" + getCPUTime() + "ms";
	}
	
	/**
	 * Returns the CPU time that took the simulation; -1 in case the simulation has not finished 
	 * @return the CPU time that took the simulation; -1 in case the simulation has not finished
	 */
	public long getCPUTime() {
		if (endT == Long.MAX_VALUE)
			return -1L;
		return ((endT - iniT) / 1000000);
	}
}
