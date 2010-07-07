package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.ActivityWorkGroup;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

public class ExecutionCounterFileSafeView extends View {
	final private static int REQ = 0;
	final private static int STA = 1;
	final private static int END = 2;
	
	final private ActivityCounters[][] actExCounter;
	final private AtomicIntegerArray[] etExCounter;
	private final AtomicBoolean busy;
	private PrintWriter buffer = null;
	private final long dayUnit;
	private long timeSlot;
	private long warmUp;
	
	public ExecutionCounterFileSafeView(Simulation simul, String fileName, TimeStamp warmUp, TimeStamp period) {
		super(simul, "executionCounter");
		this.dayUnit = simul.getTimeUnit().convert(period);
		this.warmUp = simul.getTimeUnit().convert(warmUp);
		// The time slot is initialized to the next day
		this.timeSlot = dayUnit;
		busy = new AtomicBoolean(false);
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < simul.getActivityList().size(); i++)
			buffer.print("\tA:" + simul.getActivity(i).getDescription() + "\t\t");
		for (int i = 0; i < simul.getElementTypeList().size(); i++)
			buffer.print("\tET:" + simul.getElementType(i).getDescription() + "\t\t");
		buffer.println();

		actExCounter = new ActivityCounters[3][];
		for (int i = 0; i < 3; i++) {
			actExCounter[i] = new ActivityCounters[simul.getActivityList().size()];
			for (int j = 0; j < simul.getActivityList().size(); j++)
				actExCounter[i][j] = new ActivityCounters(simul.getActivity(j));
		}
		etExCounter = new AtomicIntegerArray[3];
		for (int i = 0; i < 3; i++)
			etExCounter[i] = new AtomicIntegerArray(simul.getElementTypeList().size());
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	private void checkPeriod(long ts) {
		// A new day has passed
		if (ts > timeSlot) {
			while (!busy.compareAndSet(false, true));
			// Double check in case a different thread gained access to this area
			if (ts > timeSlot) {
				for (int j = 0; j < getSimul().getActivityList().size(); j++)
					for (int i = 0; i < 3; i++)
						buffer.print(actExCounter[i][j].exCounter + "\t");
				for (int j = 0; j < getSimul().getElementTypeList().size(); j++)
					for (int i = 0; i < 3; i++)
						buffer.print(etExCounter[i].get(j) + "\t");
				buffer.println();
				timeSlot += dayUnit;
			}
			busy.set(false);
		}		
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo elemInfo = (ElementActionInfo) info;
			final long ts = elemInfo.getTs();
			// Only saves information after the warmUp period has passed
			if (ts > warmUp) {
				checkPeriod(ts);

				int infoType;
				switch(elemInfo.getType()) {
				case ENDACT: infoType = END; break;
				case STAACT: infoType = STA; break;
				case REQACT: infoType = REQ; break;
				default: infoType = -1; break;
				}
				if (infoType != -1) {
					final Activity act = elemInfo.getActivity();
					final Element elem = elemInfo.getElement();
					final ElementType et = elem.getType();
					actExCounter[infoType][act.getIdentifier()].addExecution(elemInfo.getWorkGroup());
					etExCounter[infoType].incrementAndGet(et.getIdentifier());
					if (isDebugMode()) {
						String message = new String();
						message += elemInfo.toString() + "\n";
						final ActivityCounters counters = actExCounter[infoType][act.getIdentifier()];
						message += act.getDescription() + " executed " + counters.exCounter + " times\n";
						for (int i = 0; i < act.getWorkGroupSize(); i++)
							message += "\t with workgroup " + act.getWorkGroup(i).getDescription() + " " + counters.wgExCounter[i] + " times\n"; 
						message += "Type " + et.getDescription() + " executed " + etExCounter[infoType].get(et.getIdentifier()) + " times\n";
						debug(message);
					}
				}
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
	
	private class ActivityCounters {
		public int exCounter;
		final public int[] wgExCounter;
		
		public ActivityCounters(Activity act) {
			wgExCounter = new int[act.getWorkGroupSize()];
		}
		
		public synchronized void addExecution(ActivityWorkGroup wg) {
			exCounter++;
			if (wg != null)
				wgExCounter[wg.getIdentifier()]++;
		}
	}
}
