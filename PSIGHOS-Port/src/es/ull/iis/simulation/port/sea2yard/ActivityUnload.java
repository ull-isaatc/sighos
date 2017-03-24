/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * @author Iván Castilla
 *
 */
public class ActivityUnload extends ActivityFlow {
	final private int containerId;
	final private int nextContainerId;

	/**
	 * @param model
	 * @param description
	 * @param priority
	 * @param exclusive
	 * @param interruptible
	 */
	public ActivityUnload(PortModel model, int containerId, int processingTime, int nextContainerId) {
		super(model, PortModel.ACT_UNLOAD + containerId, 0, true, false);
		this.containerId = containerId;
		this.nextContainerId = nextContainerId;
		addWorkGroup(0, model.getContainerWorkGroup(containerId), processingTime);
		addResourceCancellation(model.getTruckResourceType(), PortModel.T_TRANSPORT);
	}

	public ActivityUnload(PortModel model, int containerId, int processingTime) {
		this(model, containerId, processingTime, -1);
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
			Resource res = new Resource(simul, PortModel.CONTAINER + nextContainerId);
			res.addTimeTableEntry(((PortModel)simul).getContainerResourceType(nextContainerId));
			simul.addEvent(res.onCreate(simul.getSimulationEngine().getTs()));			
		}
	}
}
