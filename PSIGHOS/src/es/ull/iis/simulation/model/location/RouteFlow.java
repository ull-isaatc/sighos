/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;

/**
 * A workflow step that allows {@link MovableElement Movable elements} to move from one {@link Location} to another.
 * The route flow uses a {@link Router} to define the path of the element, ensures that the destination is reachable, and moves the 
 * element from one location to another until reaching the destination.
 * @author Iván Castilla
 *
 */
public class RouteFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the route */
    private final String description;
    /** Final destination of the element */ 
    private final Location destination;
    /** Instance that returns the path for the element */
    private final Router router;

    /**
     * Creates a route flow
     * @param model Model this flow belongs to
     * @param description A brief description of the route
     * @param destination Final destination of the element
     * @param router Instance that returns the path for the element
     */
	public RouteFlow(Simulation model, String description, Location destination, Router router) {
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

	/**
	 * {@inheritDoc}
	 * 
	 * The element uses the router to select the next step in the path for the destination. If the destination is unreachable, the flow is 
	 * cancelled. Otherwise, the element starts a delay as defined in the current location's {@link Location#getDelayAtExit(Movable)} 
	 */
	public void request(final ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					if (ei.getElement() instanceof MovableElement) {
						final MovableElement elem = (MovableElement) ei.getElement(); 
						elem.debug("Start route\t" + this + "\t" + getDescription());
						final Location loc = elem.getLocation();
						final Location nextLoc = router.getNextLocationTo(elem, destination);
						if (nextLoc == null) {
							elem.error("Destination unreachable. Current: " + loc + "; destination: " + destination);
							ei.cancel(this);
							next(ei);
						}
						else {								
							elem.setNextLocation(nextLoc);
							final long delay = loc.getDelayAtExit(elem); 
							if (delay > 0L) {
								ei.startDelay(delay);
							}
							else {
								finish(ei);
							}
						}
					}
					else {
						ei.getElement().error("Only Movable Elements can use route flows");
						next(ei);
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
	 * {@inheritDoc}
	 * 
	 * This method is invoked after finishing the delay to abandon the element's current location. The element try to move to the next location in its
	 * path to its final destination. The element only leaves its current location if there is enough free space for the element in the new location; 
	 * otherwise, it waits. 
	 */
	public void finish(final ElementInstance ei) {
		final MovableElement elem = (MovableElement) ei.getElement();
		final Location currentLocation = elem.getLocation();
		final Location nextLocation = elem.getNextLocation();
		if (nextLocation.getAvailableCapacity() >= elem.getCapacity()) {
			nextLocation.move(elem);
			currentLocation.leave(elem);
			if (nextLocation.equals(destination)) {
				elem.debug("Finishes route\t" + this + "\t" + getDescription());
				afterFinalize(ei);
				next(ei);
			}
			else {
				request(ei);
			}
		}
		else {
			nextLocation.waitFor(ei);
		}
	}


	/**
	 * Returns the final destination 
	 * @return the final destination
	 */
	public Location getDestination() {
		return destination;
	}

}
