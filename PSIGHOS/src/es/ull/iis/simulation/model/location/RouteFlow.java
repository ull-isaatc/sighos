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
 * @author Iván Castilla
 *
 */
public class RouteFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the delay */
    private final String description;

    private final Location destination;
    private Location nextLocation;
    
    private final Router router;
    
	/**
	 * 
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
    
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.flow.Flow#addPredecessor(es.ull.iis.simulation.model.flow.Flow)
	 */
	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public void afterFinalize(ElementInstance fe) {
	}

	@Override
	public void request(ElementInstance wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread)) {
					final MovableElement elem = (MovableElement) wThread.getElement(); 
					elem.debug("Start route\t" + this + "\t" + getDescription());
					final Location loc = elem.getLocation();
					nextLocation = router.getNextLocationTo(loc, destination);
					final long delay = loc.getDelayAtExit(elem); 
					if (delay > 0L) {
						wThread.startDelay(delay);
					}
					else {
						finish(wThread);
					}
				}
				else {
					wThread.cancel(this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	@Override
	public void finish(ElementInstance wThread) {
		final MovableElement elem = (MovableElement) wThread.getElement();
		final Location currentLocation = elem.getLocation();
		if (nextLocation.tryMove(wThread)) {
			currentLocation.leave(elem);
			if (nextLocation.equals(destination)) {
				elem.debug("Finishes route\t" + this + "\t" + getDescription());
				afterFinalize(wThread);
				next(wThread);
			}
			else {
				request(wThread);
			}
		}
	}


	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

}
