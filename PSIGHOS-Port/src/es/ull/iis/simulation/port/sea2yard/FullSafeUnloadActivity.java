/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * A flow representing the unload operation in a berth where cranes cannot move/operate if
 * there is another crane within the safety distance
 * @author Iván Castilla Rodríguez
 *
 */
public class FullSafeUnloadActivity extends ActivityFlow implements UnloadTask {
	/** Id of the container being unloaded */
	final private int containerId;
	/** The id of the container that becomes available after the objective container has been unloaded */
	final private int nextContainerId;

	/**
	 * @param model
	 * @param description
	 * @param priority
	 * @param exclusive
	 * @param interruptible
	 */
	public FullSafeUnloadActivity(PortModel model, int containerId, int nextContainerId) {
		super(model, PortModel.ACT_UNLOAD + containerId, 0, true, false);
		this.containerId = containerId;
		this.nextContainerId = nextContainerId;
		final long newTime = model.getTimes().getOperationTime(containerId);
		if (model.hasGenericVehicles()) {
			addWorkGroup(0, model.getContainerWorkGroup(containerId), newTime);
			addResourceCancellation(model.getGenericVehicleResourceType(), model.getTimes().getTransportTime(containerId));
		}
		if (model.hasSpecificVehicles()) {
			addWorkGroup(0, model.getSpecificContainerWorkGroup(containerId), newTime);
			addResourceCancellation(model.getSpecificVehicleResourceType(model.getPlan().getCraneDoTask(containerId)), model.getTimes().getTransportTime(containerId));
		}
	}

	public FullSafeUnloadActivity(PortModel model, int containerId) {
		this(model, containerId, -1);
	}

	/**
	 * @return the containerId
	 */
	public int getContainerId() {
		return containerId;
	}

	@Override
	public void afterFinalize(ElementInstance fe) {
		if (nextContainerId != -1) {
			final Resource res = new Resource(simul, PortModel.CONTAINER + nextContainerId);
			res.addTimeTableEntry(((PortModel)simul).getContainerResourceType(nextContainerId));
			simul.addEvent(res.onCreate(simul.getSimulationEngine().getTs()));			
		}
	}
}
