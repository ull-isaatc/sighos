/**
 * 
 */
package es.ull.iis.simulation.model.location;

import java.util.ArrayDeque;
import java.util.TreeMap;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;

/**
 * A workflow step that allows {@link Element elements} to move a set of resources from one {@link Location} to another.
 * The route flow uses a {@link Router} to define the path of the element, ensures that the destination is reachable, and moves the 
 * element from one location to another until reaching the destination.
 * @author Iván Castilla
 *
 */
public class MoveResourcesFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the route */
    private final String description;
    /** Final destination of the element */ 
    private final Location destination;
    /** Instance that returns the path for the element */
    private final Router router;
    /** A workgroup of resources to move */
    protected final WorkGroup wg;
    /** A unique identifier that sets which resources to move */
	protected final int resourcesId;
	/** Counter of resources left to arrive to destination */
	protected final TreeMap<ElementInstance, Integer> resourcesLeft;

    /**
     * Creates a flow to move all the resources under the specified identifier
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     * @param resourcesId A unique identifier of the set of resources
     */
	public MoveResourcesFlow(Simulation model, String description, Location destination, Router router, int resourcesId) {
		this(model, description, destination, router, resourcesId, null);
	}

    /**
     * Creates a flow to move a set of resources from the default set of resources, specified as a workgroup
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     * @param wg A workgroup of resources
     */
	public MoveResourcesFlow(Simulation model, String description, Location destination, Router router, WorkGroup wg) {
		this(model, description, destination, router, 0, wg);
	}

    /**
     * Creates a flow to move an set of resources
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     * @param resourcesId A unique identifier of the set of resources
     * @param wg A workgroup of resources
     */
	public MoveResourcesFlow(Simulation model, String description, Location destination, Router router, int resourcesId, WorkGroup wg) {
		super(model);
		this.description = description;
		this.destination = destination;
		this.router = router;
		this.resourcesId = resourcesId;
		this.wg = wg;
		this.resourcesLeft = new TreeMap<ElementInstance, Integer>();
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

	/**
	 * {@inheritDoc}
	 * 
	 * The element creates {@link MoveEvent move events} for each resource 
	 */
	public void request(final ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					final ArrayDeque<Resource> list = ei.getElement().getCaughtResources(resourcesId, wg); 
					resourcesLeft.put(ei, list.size());
					for (final Resource res : list) {
						res.startMove(ei);
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

	public void notifyArrival(final ElementInstance ei, boolean success) {
		if (!success)
			ei.cancel(this);
		final int left = resourcesLeft.get(ei);
		if (left > 1) {
			resourcesLeft.put(ei, left - 1);
		}
		else {
			finish(ei);
		}
	}
	/**
	 * {@inheritDoc}
	 * 
	 * This method is invoked after each resource arrive at destination finishing the delay to abandon the element's current location. The element try to move to the next location in its
	 * path to its final destination. The element only leaves its current location if there is enough free space for the element in the new location; 
	 * otherwise, it waits. 
	 */
	public void finish(final ElementInstance ei) {
		if (ei.isExecutable()) {
			ei.getElement().debug("All resources arrived at destination\t" + this + "\t" + getDescription());
			afterFinalize(ei);			
		}
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
