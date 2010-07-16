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
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class NurseUsageFileSafeView extends View {
	private PrintWriter buffer = null;
	private final ResourceActivation[]lastActivation;
	boolean hey = false;

	public NurseUsageFileSafeView(Simulation simul, String fileName) {
		super(simul, "ResourceUsage");
		try {
			buffer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lastActivation = new ResourceActivation[simul.getResourceList().size()];
		for (int i = 0; i < lastActivation.length; i++)
			lastActivation[i] = new ResourceActivation();
		addEntrance(ResourceUsageInfo.class);
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
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
			switch(rInfo.getType()) {
			case ROLON:
				lastActivation[resId].addRoleOn(ts); 
				break;
			case ROLOFF: 
				lastActivation[resId].addRoleOff(rInfo);
				break;
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			final ResourceUsageInfo rInfo = (ResourceUsageInfo)info;
			final long ts = rInfo.getTs();
			final int resId = rInfo.getResource().getIdentifier();
			switch(rInfo.getType()) {
			case CAUGHT:
				lastActivation[resId].addCaught(ts); 
				break;
			case RELEASED: 
				lastActivation[resId].addRelease(rInfo); 
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			buffer.close();			
		}

	}

	class ResourceActivation {
		private final ArrayDeque<Long> activations;
		private long lastCaught = -1;
		private boolean outOfTime = false;
		
		public ResourceActivation() {
			activations = new ArrayDeque<Long>();
		}
		
		public void addRoleOn(long ts) {
			activations.push(ts);
		}
		
		public void addRoleOff(ResourceInfo rInfo) {
			final Long avTs = activations.pop();
			assert avTs != null : "Resource not previous role on";
			// I out of time
			if (activations.isEmpty() && lastCaught != -1) {
				buffer.println(rInfo.getTs() + "\tEXTRA\t" + rInfo.getResource().getDescription() + "\t" + rInfo.getResourceType().getDescription());
				outOfTime = true;
			}
		}
		
		public void addCaught(long ts) {
			assert (lastCaught == -1) : "Resource was already captured!";
			lastCaught = ts;
		}
		
		public void addRelease(ResourceUsageInfo rInfo) {
			assert (lastCaught != -1) : "Resource was not previously captured!";
			lastCaught = -1;
			if (outOfTime) {
				buffer.println(rInfo.getTs() + "\tEND\t" + rInfo.getResource().getDescription() + "\t" + rInfo.getResourceType().getDescription() + "\t" + rInfo.getActivity().getDescription());
				outOfTime = false;				
			}
				
		}
	}
}
