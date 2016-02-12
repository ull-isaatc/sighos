/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;

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
			System.out.println("" + ((endT - iniT) / 1000000) + "ms");
		}
	}

}
