/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.model.Model;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CpuTimeView extends View {
	protected long iniT;
	protected long endT;
	
	public CpuTimeView(Model model) {
		super(model, "CPU Time viewer");
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
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
