/**
 * 
 */
package es.ull.iis.simulation.model.location;

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
					ei.getElement().startMove(ei);
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
		ei.getElement().endMove(ei);
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
