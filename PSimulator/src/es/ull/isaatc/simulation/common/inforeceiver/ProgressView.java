/**
 * 
 */
package es.ull.isaatc.simulation.common.inforeceiver;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ProgressView extends View {
	long nextMsg = 0;
	final long gap;
	int percentage = 0;
	public ProgressView(Simulation simul) {
		super(simul, "Progress");
		addEntrance(TimeChangeInfo.class);
		addEntrance(SimulationStartInfo.class);
		gap = simul.getInternalEndTs() / 100;
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
