/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimultaneousEventFileSafeView extends View {
	private PrintWriter buffer = null;
	private final AtomicInteger events = new AtomicInteger(0);
	private long lastTs;

	public SimultaneousEventFileSafeView(Simulation simul, String fileName) {
		super(simul, "Simultaneous event view");
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(TimeChangeInfo.class);
		lastTs = simul.getInternalStartTs();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.common.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.common.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		// This method assumes that TimeChangeInfo is emmited safely
		if (info instanceof TimeChangeInfo) {
			buffer.println("" + lastTs + "\t" + events);
			lastTs = ((TimeChangeInfo)info).getTs();
			events.set(0);
		}
		else if ((info instanceof ElementInfo) || (info instanceof ResourceInfo)) {
			events.incrementAndGet();
		}
		else if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			if ((eInfo.getType() == ElementActionInfo.Type.ENDACT) || (eInfo.getType() == ElementActionInfo.Type.INTACT))
				events.incrementAndGet();				
		}
		else if (info instanceof SimulationEndInfo) {
			buffer.close();
		}
		else {
			Error err = new Error("Incorrect info received: " + info.toString());
			err.printStackTrace();
		}	
	}

}
