/**
 * 
 */
package es.ull.iis.simulation.model.location;

import java.util.ArrayDeque;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;

/**
 * A workflow step that selects a resource type as a transport, and moves an {@link Element} from one {@link Location} to another on that transport.
 * The route flow uses a {@link Router} to define the path of the element, ensures that the destination is reachable, and moves the 
 * element and the transport from one location to another until reaching the destination. Only the size of the resource is used to check the capacity of 
 * the pathway. The element must have seized at least one resource belonging to the specified resource type.
 * @author Iván Castilla
 *
 */
public class TransportFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the route */
    private final String description;
    /** Final destination of the element */ 
    private final Location destination;
    /** Resource type that will act as a transport for the element */
    private final ResourceType rtTransport;
    /** Instance that returns the path for the element */
    private final Router router;

    /**
     * Creates a flow to move an element
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     */
	public TransportFlow(Simulation model, String description, Location destination, Router router, ResourceType rtTransport) {
		super(model);
		this.description = description;
		this.destination = destination;
		this.rtTransport = rtTransport;
		this.router = router;
	}


	@Override
	public String getDescription() {
		return description;
	}
    
	@Override
	public void addPredecessor(final Flow predecessor) {
	}

	@Override
	public void afterFinalize(final ElementInstance fe) {
	}

	@Override
	public void request(final ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					final ArrayDeque<Resource> resList = ei.getElement().getCaughtResources();
					Resource resFound = null;
					for (final Resource res : resList) {
						if (rtTransport.equals(res.getCurrentResourceType())) {
							resFound = res;
							break;
						}
					}
					if (resFound == null) {
						error("Element " + ei.getElement() + " has not seized any resource of type " + rtTransport + " before moving");
						ei.cancel(this);
						next(ei);
					}
					else {
						resFound.startTransport(ei);
					}
				}
				else {
					ei.cancel(this);
					next(ei);
				}
			}
			else {
				ei.updatePath(this);
				next(ei);
			}
		} else
			ei.notifyEnd();
	}

	@Override
	public void finish(final ElementInstance ei) {
		afterFinalize(ei);
		next(ei);
	}


	/**
	 * Returns the final destination 
	 * @return the final destination
	 */
	public Location getDestination() {
		return destination;
	}


	/**
	 * @return the router
	 */
	public Router getRouter() {
		return router;
	}

}
