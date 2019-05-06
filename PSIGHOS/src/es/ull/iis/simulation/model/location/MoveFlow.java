/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;

/**
 * A workflow step that allows {@link Element elements} to move from one {@link Location} to another.
 * The route flow uses a {@link Router} to define the path of the element, ensures that the destination is reachable, and moves the 
 * element from one location to another until reaching the destination.
 * @author Iván Castilla
 *
 */
public class MoveFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the route */
    private final String description;
    /** Final destination of the element */ 
    private final Location destination;
    /** Instance that returns the path for the element */
    private final Router router;

    /**
     * Creates a flow to move an element
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     */
	public MoveFlow(Simulation model, String description, Location destination, Router router) {
		super(model);
		this.description = description;
		this.destination = destination;
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
					// If already at destination, just finish the flow
					if (destination.equals(ei.getElement().getLocation())) {
						afterFinalize(ei);
						next(ei);
					}
					else {
						ei.getElement().keepMoving(this, ei);
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

	/**
	 * Performs a partial step to the final destination
	 * @param ei Element instance moving
	 */
	public void move(final ElementInstance ei) {
		final Element elem = ei.getElement();
		final Location nextLocation = router.getNextLocationTo(elem, destination);
		if (Router.isUnreachableLocation(nextLocation)) {
			ei.cancel(this);
			next(ei);
    		error("Destination unreachable. Current: " + elem.getLocation() + "; destination: " + destination);
		}
		else if (Router.isConditionalWaitLocation(nextLocation)) {
			simul.notifyInfo(new EntityLocationInfo(simul, elem, elem.getLocation(), EntityLocationInfo.Type.COND_WAIT, getTs()));			
		}
		else if (!nextLocation.fitsIn(elem)) {
			nextLocation.waitFor(elem);
			simul.notifyInfo(new EntityLocationInfo(simul, elem, elem.getLocation(), EntityLocationInfo.Type.WAIT_FOR, getTs()));
		}
		else {
			nextLocation.enter(elem);
			if (nextLocation.equals(destination)) {
				finish(ei);
			}
			else {
				elem.keepMoving(this, ei);
			}
		}
				
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
