/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintWriter;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class FileCPUTimeView extends View {
	PrintWriter buf;
	protected long iniT;
	protected long endT;
	
	public FileCPUTimeView(Simulation simul, PrintWriter buf) {
		super(simul, "CPU Time viewer");
		this.buf = buf;
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
			buf.println("" + (endT - iniT));
			System.out.println("" + (endT - iniT));
			buf.flush();
		}
	}
	
}
