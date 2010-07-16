/**
 * 
 */
package es.ull.isaatc.simulation.hospital.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageFileSafeView extends View {
	private PrintWriter buffer = null;
	private final long periodUnit;
	private long timeSlot;
	private long lastTimeSlot;
	private final ResourceActivation[]lastActivation;

	public ResourceUsageFileSafeView(Simulation simul, String fileName, TimeStamp period) {
		super(simul, "ResourceUsage");
		periodUnit = simul.getTimeUnit().convert(period);
		// The time slot is initialized to the next day
		timeSlot = periodUnit;
		lastTimeSlot = 0;
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lastActivation = new ResourceActivation[simul.getResourceList().size()];
		for (int i = 0; i < lastActivation.length; i++) {
			lastActivation[i] = new ResourceActivation();
			buffer.print(simul.getResource(i).getDescription() + "\t");
		}
		buffer.println();
		addEntrance(ResourceUsageInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	private void printResults() {
		for (int i = 0; i < lastActivation.length; i++) {
			buffer.print(lastActivation[i].getAvailability() + "\t");
		}		
		buffer.println();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.common.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.common.info.SimulationInfo)
	 */
	@Override
	public synchronized void infoEmited(SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			final ResourceInfo rInfo = (ResourceInfo)info;
			final long ts = rInfo.getTs();
			final int resId = rInfo.getResource().getIdentifier();
			if (ts > timeSlot) {
				printResults();				
				lastTimeSlot = timeSlot;
				timeSlot += periodUnit;
			}
			switch(rInfo.getType()) {
			case ROLON: lastActivation[resId].addRoleOn(ts); break;
			case ROLOFF: lastActivation[resId].addRoleOff(ts); break;
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			final ResourceUsageInfo rInfo = (ResourceUsageInfo)info;
			final long ts = rInfo.getTs();
			final int resId = rInfo.getResource().getIdentifier();
			if (ts > timeSlot) {
				printResults();				
				lastTimeSlot = timeSlot;
				timeSlot += periodUnit;
			}
			switch(rInfo.getType()) {
			case CAUGHT: lastActivation[resId].addCaught(ts); break;
			case RELEASED: lastActivation[resId].addRelease(ts); break;
			}
			
		}
		else if (info instanceof SimulationEndInfo) {
//			long ts = ((SimulationEndInfo)info).getTs();
//			printResults(ts);
			buffer.close();			
		}

	}

	class ResourceActivation {
		private final ArrayDeque<Long> activations;
		private long available;
		private long lastCaught = -1;
		private long caught;
		
		public ResourceActivation() {
			activations = new ArrayDeque<Long>();
		}
		
		public void addRoleOn(long ts) {
			activations.push(ts);
		}
		
		public void addRoleOff(long ts) {
			final Long avTs = activations.pop();
			assert avTs != null : "Resource not previous role on";
			if (activations.isEmpty()) {
				final long av = avTs.longValue();
				if (av < lastTimeSlot)
					available += ts - lastTimeSlot;
				else
					available += ts - av;
			}				
		}
		
		public void addCaught(long ts) {
			assert (lastCaught == -1) : "Resource was already captured!";
			lastCaught = ts;
		}
		
		public void addRelease(long ts) {
			assert (lastCaught != -1) : "Resource was not previously captured!";
			if (lastCaught < lastTimeSlot)
				caught += ts - lastTimeSlot;
			else
				caught += ts - lastCaught;
			lastCaught = -1;
		}
		
		public long getAvailability() {
			if (!activations.isEmpty()) {
				long lastAv = activations.peekFirst();
				if (lastAv < lastTimeSlot)
					available += periodUnit;
				else
					available += timeSlot - lastAv;					
			}
			// If already captured
			if (lastCaught != -1) {
				if (lastCaught < lastTimeSlot)
					caught += periodUnit;
				else
					caught += timeSlot - lastCaught;
			}
			long av = available - caught;
			available = 0;
			caught = 0;
			return av;
		}
	}
}
