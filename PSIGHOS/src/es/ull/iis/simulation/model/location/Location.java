/**
 * 
 */
package es.ull.iis.simulation.model.location;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.ElementInstance;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Location implements Located {
	private final ArrayList<Location> linkedTo; 
	private final ArrayList<Location> linkedFrom; 
	private final int size;
	private int occupied;
	private Router router = null;
	private final ArrayList<Movable> entitiesIn;
	// TODO: Change by a customizable queue
	private final ArrayList<ElementInstance> entitiesWaiting;
	private final TimeFunction delayAtExit;
	private final int id;
	private static int counter = 0;
	private final String description;

	/**
	 * 
	 */
	public Location(String description, TimeFunction delayAtExit, int size) {
		id = counter++;
		this.description = description;
		linkedTo = new ArrayList<Location>();
		linkedFrom = new ArrayList<Location>();
		entitiesIn = new ArrayList<Movable>();
		entitiesWaiting = new ArrayList<ElementInstance>();
		this.size = size;
		this.occupied = 0;
		this.delayAtExit = delayAtExit;
	}

	/**
	 * A new location with no capacity constrains.
	 */
	public Location(String description, TimeFunction delayAtExit) {
		this(description, delayAtExit, Integer.MAX_VALUE);
	}
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.location.Located#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}

	/**
	 * @return the router
	 */
	public Router getRouter() {
		return router;
	}

	/**
	 * @param router the router to set
	 */
	public void setRouter(Router router) {
		this.router = router;
	}

	public Location linkTo(Location location) {
		linkedTo.add(location);
		location.linkFrom(this);
		return location;
	}

	private void linkFrom(Location location) {
		linkedFrom.add(location);
	}

	/**
	 * @return the delayAtExit
	 */
	public long getDelayAtExit(Movable entity) {
		return Math.round(delayAtExit.getValue(entity));
	}

	/**
	 * @return the linkedTo
	 */
	public ArrayList<Location> getLinkedTo() {
		return linkedTo;
	}

	/**
	 * @return the linkedFrom
	 */
	public ArrayList<Location> getLinkedFrom() {
		return linkedFrom;
	}

	public boolean fitsIn(Movable entity) {
		return (occupied + entity.getSize() <= size);
	}
	
	public boolean tryMove(ElementInstance wThread) {
		final Movable entity = (Movable)wThread.getElement();
		if (fitsIn(entity)) {
			occupied += entity.getSize();
			entitiesIn.add(entity);
			entity.setLocation(this);
			return true;
		}
		entitiesWaiting.add(wThread);
		return false;
	}
	
	public void leave(Movable entity) {
		occupied -= entity.getSize();
		Iterator<ElementInstance> iter = entitiesWaiting.iterator();
		while (iter.hasNext()) {
			ElementInstance wThread = iter.next();
			final RouteFlow flow = (RouteFlow)wThread.getCurrentFlow();
			final MovableElement elem = (MovableElement) wThread.getElement();
			final Location currentLocation = elem.getLocation();
			if (tryMove(wThread)) {
				currentLocation.leave(elem);
				iter.remove();
				if (equals(flow.getDestination())) {
					elem.debug("Finishes route\t" + this + "\t" + flow.getDescription());
					flow.afterFinalize(wThread);
					flow.next(wThread);
				}
				else {
					flow.request(wThread);
				}
			}

			flow.finish(wThread);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((Location)obj).id == id;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
