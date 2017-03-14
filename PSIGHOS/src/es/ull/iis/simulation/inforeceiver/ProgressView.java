/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.info.TimeChangeInfo;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ProgressView extends View {
	long nextMsg = 0;
	final long gap;
	int percentage = 0;
	public ProgressView(long endTs) {
		super("Progress");
		addEntrance(TimeChangeInfo.class);
		addEntrance(SimulationStartInfo.class);
		gap = endTs / 100;
		nextMsg = gap;
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			System.out.println("Starting!!");
		}
		else if (info instanceof TimeChangeInfo) {
			if (((TimeChangeInfo) info).getTs() >= nextMsg) {
				System.out.println("" + (++percentage) + "%");
				nextMsg += gap;
			}
		}	
	}
}
