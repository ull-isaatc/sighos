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
 * @author Iván Castilla
 *
 */
public class UnloadActivity extends StructuredFlow {
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
	public UnloadActivity(PortModel model, int containerId, int nextContainerId) {
		super(model);
		this.containerId = containerId;
		this.nextContainerId = nextContainerId;
		final Vessel ship = model.getPlan().getVessel();
		this.resourcesId = ship.getNBays() + containerId + 1;
		final RequestResourcesFlow reqUnload = new RequestResourcesFlow(model, FIRST_FLOW_NAME + containerId, resourcesId);
		final ReleaseResourcesFlow relSide = new ReleaseResourcesFlow(model, "Release side positions" + containerId, resourcesId, model.getWgOpPositionsSides(ship.getContainerBay(containerId)));
		final long newTime = model.getTimeWithError(model.getTimeUnit().convert(ship.getContainerProcessingTime(containerId), ship.getUnit()));
//		System.out.println(containerId + "\t" + newTime);
		final DelayFlow delUnload = new DelayFlow(model, "Delay " + PortModel.ACT_UNLOAD, newTime);
		final ReleaseResourcesFlow relAll = new ReleaseResourcesFlow(model, LAST_FLOW_NAME + containerId, resourcesId);
		reqUnload.addWorkGroup(0, model.getContainerWorkGroup(containerId));
		relAll.addResourceCancellation(model.getTruckResourceType(), model.getTimeWithError(CalculateNTrucksExperiment.T_TRANSPORT));
		reqUnload.link(relSide).link(delUnload).link(relAll);
		this.initialFlow = reqUnload;
		this.finalFlow = relAll;
		final TreeSet<Flow> visited = new TreeSet<Flow>(); 
		initialFlow.setRecursiveStructureLink(this, visited);
	}

	public UnloadActivity(PortModel model, int containerId) {
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
