/**
 * 
 */
package es.ull.isaatc.simulation.common.inforeceiver;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class CpuTimeView extends View {
	protected long iniT;
	protected long endT;
	
	public CpuTimeView(Simulation simul) {
		super(simul, "CPU Time viewer");
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			iniT = ((SimulationStartInfo)info).getCpuTime();
		}
		else if (info instanceof SimulationEndInfo) {
			endT = ((SimulationEndInfo)info).getCpuTime();
			System.out.println("" + (endT - iniT));
		}
	}

}