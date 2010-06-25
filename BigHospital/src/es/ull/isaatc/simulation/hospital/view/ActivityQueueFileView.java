/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityQueueFileView extends View {
	private final Map<Activity, AtomicInteger> queues;
	private final AtomicBoolean busy;
	private PrintWriter buffer = null;
	private final long dayUnit;
	private long timeSlot;

	public ActivityQueueFileView(Simulation simul, String fileName, TimeStamp period) {
		super(simul, "Activity queues");
		dayUnit = simul.getTimeUnit().convert(period);
		// The time slot is initialized to the next day
		timeSlot = dayUnit;
		busy = new AtomicBoolean(false);
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		queues = new HashMap<Activity, AtomicInteger>();
		for (Activity act : simul.getActivityList().values())
			queues.put(act, new AtomicInteger(0));
		for (Activity act : queues.keySet())
			buffer.print(act.getDescription() + "\t");
		buffer.println();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.common.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.common.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			final long ts = eInfo.getTs();
			final Activity act = eInfo.getActivity();
			// A new day has passed
			if (ts > timeSlot) {
				while (!busy.compareAndSet(false, true));
				// Double check in case a different thread gained access to this area
				if (ts > timeSlot) {
					for (AtomicInteger val : queues.values())
						buffer.print(val.get() + "\t");
					buffer.println();
					timeSlot += dayUnit;
				}
				busy.set(false);
			}
			switch(eInfo.getType()) {
			case REQACT:
				queues.get(act).incrementAndGet();
				break;
			case STAACT:
				queues.get(act).decrementAndGet();
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			buffer.close();
		}
	}

}
