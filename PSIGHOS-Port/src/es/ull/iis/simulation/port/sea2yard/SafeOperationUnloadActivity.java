/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.TreeSet;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.StructuredFlow;

/**
 * A flow representing the unload operation in a berth where cranes within the safety distance
 * cannot operate simultaneously. 
 * @author Iván Castilla Rodríguez
 *
 */
public class SafeOperationUnloadActivity extends StructuredFlow implements UnloadTask {
	final private int containerId;
	final private int nextContainerId;
	final private int resourcesId;
	final static public String FIRST_FLOW_NAME = "Req " + PortModel.ACT_UNLOAD;
	final static public String LAST_FLOW_NAME = "Rel " + PortModel.ACT_UNLOAD;

	/**
	 * @param model
	 * @param description
	 * @param priority
	 * @param exclusive
	 * @param interruptible
	 */
	public SafeOperationUnloadActivity(PortModel model, int containerId, int nextContainerId) {
		super(model);
		this.containerId = containerId;
		this.nextContainerId = nextContainerId;
		final Vessel ship = model.getPlan().getVessel();
		this.resourcesId = ship.getNBays() + containerId + 1;
		final RequestResourcesFlow reqUnload = new RequestResourcesFlow(model, FIRST_FLOW_NAME + containerId, resourcesId);
		final ReleaseResourcesFlow relSide = new ReleaseResourcesFlow(model, "Release side positions" + containerId, resourcesId, model.getWgOpPositionsSides(ship.getContainerBay(containerId)));
		final long newTime = model.getTimeWithError(ship.getContainerProcessingTime(containerId) * PortModel.T_OPERATION);
//		System.out.println(containerId + "\t" + newTime);
		final DelayFlow delUnload = new DelayFlow(model, "Delay " + PortModel.ACT_UNLOAD, newTime);
		final ReleaseResourcesFlow relAll = new ReleaseResourcesFlow(model, LAST_FLOW_NAME + containerId, resourcesId);
		if (model.hasGenericVehicles()) {
			reqUnload.addWorkGroup(model.getContainerWorkGroup(containerId));
			relAll.addResourceCancellation(model.getGenericVehicleResourceType(), model.getTimeWithError(PortModel.T_TRANSPORT));
		}
		if (model.hasSpecificVehicles()) {
			reqUnload.addWorkGroup(model.getSpecificContainerWorkGroup(containerId));
			relAll.addResourceCancellation(model.getSpecificVehicleResourceType(model.getPlan().getCraneDoTask(containerId)), model.getTimeWithError(PortModel.T_TRANSPORT));
		}
		reqUnload.link(relSide).link(delUnload).link(relAll);
		this.initialFlow = reqUnload;
		this.finalFlow = relAll;
		final TreeSet<Flow> visited = new TreeSet<Flow>(); 
		initialFlow.setRecursiveStructureLink(this, visited);
	}

	public SafeOperationUnloadActivity(PortModel model, int containerId) {
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
