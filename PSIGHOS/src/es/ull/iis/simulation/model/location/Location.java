/**
 * 
 */
package es.ull.iis.simulation.model.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.Identifiable;
import es.ull.iis.simulation.model.SimulationObject;

/**
 * A physical place where one or more entities can be at any time. Locations have a capacity, that determines how many entities fit in.
 * Entities leaving a location can suffer a delay. Such delay can also represents the time that the entity takes to go through the location.
 * Entities can move from one location to another linked location. Such connections are defined by using {@link #linkTo(Location)}, and have a direction,
 * i.e. an entity can move from a location to another, but not back, unless a explicit link is created in the opposite way.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Location implements Located, Identifiable, Comparable<Location> {
	/** An array of the locations that this location is linked to */
	private final ArrayList<Location> linkedTo; 
	/** An array of the locations that this location is linked from */
	private final ArrayList<Location> linkedFrom; 
	/** Total capacity of the location */
	private final int capacity;
	/** How much of the capacity is currently occupied */
	private int occupied;
	/** A list of the entities currently at this location */
	private final List<Movable> entitiesIn;
	// TODO: Change by a customizable queue
	/** A simple FIFO queue for entities waiting to enter into the location */
	private final List<Movable> entitiesWaiting;
	/** The time that it takes to exit (or go through) the location */ 
	private final TimeFunction delayAtExit;
	/** An internal unique identifier */
	private final int id;
	/** A counter to create unique identifiers */
	private static int counter = 0;
	/** A brief description of the location */
	private final String description;

	/**
	 * Creates a location with a specific capacity. Entities in this location have to wait a time before exiting it.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 * @param capacity Total capacity of the location
	 */
	public Location(String description, TimeFunction delayAtExit, int capacity) {
		id = counter++;
		this.description = description;
		linkedTo = new ArrayList<Location>();
		linkedFrom = new ArrayList<Location>();
		entitiesIn = new ArrayList<Movable>();
		entitiesWaiting = new ArrayList<Movable>();
		this.capacity = capacity;
		this.occupied = 0;
		this.delayAtExit = delayAtExit;
	}

	/**
	 * Creates a location with no capacity constrains. Entities in this location have to wait a time before exiting it.
	 * @param description A brief description of the location
-	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 */
	public Location(String description, TimeFunction delayAtExit) {
		this(description, delayAtExit, Integer.MAX_VALUE);
	}

	/**
	 * Creates a location with no capacity constrains. Entities in this location have to wait a time before exiting it.
	 * @param description A brief description of the location
-	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 */
	public Location(String description, long delayAtExit) {
		this(description, delayAtExit, Integer.MAX_VALUE);
	}

	/**
	 * Creates a location with a specific capacity. Entities in this location do not have to wait a time before exiting it.
	 * @param description A brief description of the location
	 * @param capacity Total capacity of the location
	 */
	public Location(String description, int capacity) {
		this(description, TimeFunctionFactory.getInstance("ConstantVariate", 0), capacity);
	}

	/**
	 * Creates a location with a specific capacity. Entities in this location do not have to wait a time before exiting it.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 * @param capacity Total capacity of the location
	 */
	public Location(String description, long delayAtExit, int capacity) {
		this(description, TimeFunctionFactory.getInstance("ConstantVariate", delayAtExit), capacity);
	}

	/**
	 * Creates a location with no capacity constrains. Entities in this location do not have to wait a time before exiting it.
	 * @param description A brief description of the location
	 */
	public Location(String description) {
		this(description, TimeFunctionFactory.getInstance("ConstantVariate", 0), Integer.MAX_VALUE);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns the available capacity left in the location
	 * @return the available capacity left in the location
	 */
	public int getAvailableCapacity() {
		return capacity - occupied;
	}

	/**
	 * Returns true if the specified entity fits into this location
	 * @param entity A movable entity
	 * @return true if the specified entity fits into this location
	 */
	public boolean fitsIn(Movable entity) {
		return getAvailableCapacity() >= entity.getCapacity();
	}
	
	/**
	 * Connects this location to another
	 * @param location Another location
	 * @return The other location (useful for concatenate calls to this method) 
	 */
	public Location linkTo(Location location) {
		linkedTo.add(location);
		location.linkFrom(this);
		return location;
	}

	/**
	 * Creates the link back to the other location. Useful for created a double linked graph of connections
	 * @param location Another location
	 */
	private void linkFrom(Location location) {
		linkedFrom.add(location);
	}

	/**
	 * Returns the time that it takes an element to exit (or go through) the location
	 * @param obj The simulation object requesting the delay
	 * @return the time that it takes an element to exit (or go through) the location
	 */
	public long getDelayAtExit(SimulationObject obj) {
		return Math.round(delayAtExit.getValue(obj));
	}

	/**
	 * Returns a list of the locations this location is linked to
	 * @return a list of the locations this location is linked to
	 */
	public ArrayList<Location> getLinkedTo() {
		return linkedTo;
	}

	/**
	 * Returns a list of the locations this location is linked from
	 * @return a list of the locations this location is linked from
	 */
	public ArrayList<Location> getLinkedFrom() {
		return linkedFrom;
	}

	/**
	 * Returns a collection with the entities currently in this location
	 * @return a collection with the entities currently in this location
	 */
	public List<Movable> getEntitiesIn() {
		return entitiesIn;
	}
	
	/**
	 * Puts the entity into a waiting queue until the location has enough available capacity
	 * @param entity Entity currently trying to arrive at the location
	 */
	public void waitFor(Movable entity) {
		entitiesWaiting.add(entity);
	}
	
	/**
	 * Moves an entity into the location and updates the available capacity
	 * @param entity Entity moving into the location
	 */
	public void enter(Movable entity) {
		occupied += entity.getCapacity();
		entitiesIn.add(entity);
		final Location currentLocation = entity.getLocation();
		entity.setLocation(this);
		if (currentLocation != null) {
			currentLocation.leave(entity);
		}
	}
	
	/**
	 * Moves an entity out of the location and checks whether there are waiting entities. If there is any that fits into the available capacity,
	 * the entity moves into the location.
	 * @param entity Entity leaving the location
	 */
	private void leave(Movable entity) {
		occupied -= entity.getCapacity();
		entitiesIn.remove(entity);
		// Goes through the waiting queue
		Iterator<Movable> iter = entitiesWaiting.iterator();
		while (iter.hasNext()) {
			final Movable waitingEntity = iter.next();
			if (fitsIn(waitingEntity)) {
				waitingEntity.notifyLocationAvailable(this);
				iter.remove();
			}
		}
	}

	@Override
	public int compareTo(Location o) {
		if (id < o.id)
			return -1;
		if (id > o.id)
			return 1;
		return 0;
	}

	@Override
	public int getIdentifier() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return ((Location)obj).id == id;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
