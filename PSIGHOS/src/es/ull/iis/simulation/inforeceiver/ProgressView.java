/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ProgressView extends Listener {
	long nextMsg = 0;
	final long gap;
	int percentage = 0;
	public ProgressView(long endTs) {
		super("Progress");
		addEntrance(SimulationTimeInfo.class);
		gap = endTs / 100;
		nextMsg = gap;
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		final SimulationTimeInfo tInfo = (SimulationTimeInfo)info;
		
		if (SimulationTimeInfo.Type.START.equals(tInfo.getType())) {
			System.out.println("Starting!!");
		}
		else if (SimulationTimeInfo.Type.TICK.equals(tInfo.getType())) {
			if (tInfo.getTs() >= nextMsg) {
				System.out.println("" + (++percentage) + "%");
				nextMsg += gap;
			}
		}	
	}
}
