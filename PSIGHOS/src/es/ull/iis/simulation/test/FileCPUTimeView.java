/**
 * 
 */
package es.ull.iis.simulation.test;

import java.io.PrintWriter;

import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class FileCPUTimeView extends View {
	PrintWriter buf;
	protected long iniT;
	protected long endT;
	
	public FileCPUTimeView(PrintWriter buf) {
		super("CPU Time viewer");
		this.buf = buf;
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
			buf.print("\t" + ((endT - iniT) / 1000000));
			System.out.print("\t" + ((endT - iniT) / 1000000));
			buf.flush();
		}
	}
	
}
