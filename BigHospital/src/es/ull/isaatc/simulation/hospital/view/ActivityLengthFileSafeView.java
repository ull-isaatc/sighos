/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityLengthFileSafeView extends View {
	private long warmUp;
	private PrintWriter buffer = null;
	private final ConcurrentHashMap<WorkItem, long[]> actLengths;

	public ActivityLengthFileSafeView(Simulation simul, String fileName, TimeStamp warmUp) {
		super(simul, "Flow-driven activities statistics");
		this.warmUp = simul.getTimeUnit().convert(warmUp);
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.println("E\tET\tA\tWI\tREQ\tSTART\tEND\tDIF\t");
		actLengths = new ConcurrentHashMap<WorkItem, long[]>();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.common.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.common.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo elemInfo = (ElementActionInfo) info;
			final long ts = elemInfo.getTs();
			// Only saves information after the warmUp period has passed
			if (ts > warmUp) {
				if (elemInfo.getType() == ElementActionInfo.Type.REQACT)
					addRequest(elemInfo.getWorkItem(), ts);
				else if (elemInfo.getType() == ElementActionInfo.Type.STAACT)
					addStart(elemInfo.getWorkItem(), ts);
				else if (elemInfo.getType() == ElementActionInfo.Type.ENDACT)
					addEnd(elemInfo.getWorkItem(), ts);
			}
		}
		else if (info instanceof SimulationEndInfo) {
			buffer.close();
		}
		else {
			Error err = new Error("Incorrect info received: " + info.toString());
			err.printStackTrace();
		}
	}

	private synchronized void addRequest(WorkItem wi, long ts) {
		assert !actLengths.containsKey(wi) : "Element " + wi.getElement() + "(" + wi.getIdentifier() + ") already started activity " + wi.getActivity() + "?";
		long[] temp = new long[2];
		temp[0] = ts;
		actLengths.put(wi, temp);
	}
	
	private synchronized void addStart(WorkItem wi, long ts) {
		assert actLengths.containsKey(wi) : "Element " + wi.getElement() + "(" + wi.getIdentifier() + ") never requested activity " + wi.getActivity() + "?";
		actLengths.get(wi)[1] = ts;
	}
	
	private synchronized void addEnd(WorkItem wi, long ts) {
		assert actLengths.containsKey(wi) : "Element " + wi.getElement() + "(" + wi.getIdentifier() + ") never started activity " + wi.getActivity().getDescription() + "?";
		long[] value = actLengths.remove(wi);
		final Element elem = wi.getElement();
		buffer.println("" + elem.getIdentifier() + "\t" + elem.getType().getIdentifier() + "\t" + wi.getActivity().getDescription()
				+ "\t" + wi.getIdentifier() + "\t" + value[0] + "\t" + value[1] + "\t" + ts + "\t" + (ts - value[1]));
	}
	
}
