/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintWriter;

import es.ull.isaatc.simulation.core.Simulation;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.inforeceiver.View;

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
			buf.print("\t" + ((endT - iniT) / 1000000));
			System.out.print("\t" + ((endT - iniT) / 1000000));
			buf.flush();
		}
	}
	
}
