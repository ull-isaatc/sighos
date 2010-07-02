/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * This view is thread-safe
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityQueueFileSafeView extends View {
	private final AtomicIntegerArray queues;
	private final AtomicBoolean busy;
	private PrintWriter buffer = null;
	private final long dayUnit;
	private long timeSlot;

	public ActivityQueueFileSafeView(Simulation simul, String fileName, TimeStamp period) {
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
		final int size = simul.getActivityList().size();
		queues = new AtomicIntegerArray(size);
		for (int i = 0; i < size; i++)
			buffer.print(simul.getActivity(i).getDescription() + "\t");
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
					for (int i = 0; i < queues.length(); i++)
						buffer.print(queues.get(i) + "\t");
					buffer.println();
					timeSlot += dayUnit;
				}
				busy.set(false);
			}
			switch(eInfo.getType()) {
			case REQACT:
				queues.incrementAndGet(act.getIdentifier());
				break;
			case STAACT:
				queues.decrementAndGet(act.getIdentifier());
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			buffer.close();
		}
	}

}
