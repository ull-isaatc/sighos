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
			newWorkGroupAdder(model.getContainerWorkGroup(containerId)).withDelay(newTime).add();
			addResourceCancellation(model.getGenericVehicleResourceType(), model.getTimes().getTransportTime(containerId));
		}
		if (model.hasSpecificVehicles()) {
			newWorkGroupAdder(model.getSpecificContainerWorkGroup(containerId)).withDelay(newTime).add();
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
			final Resource[] res = ((PortModel)simul).getContainerResourceType(nextContainerId).addGenericResources(1);
			simul.addEvent(res[0].onCreate(simul.getSimulationEngine().getTs()));			
		}
	}
}
