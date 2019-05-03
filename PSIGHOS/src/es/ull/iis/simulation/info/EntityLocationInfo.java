/**
 * 
 */
package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class EntityLocationInfo extends AsynchronousInfo {
	/** Possible types of element information */
	public enum Type implements InfoType {
			ARRIVE	("ARRIVE AT LOCATION"),
			LEAVE	("LEAVE FROM LOCATION"),
			START	("START AT LOCATION"),
			WAIT_FOR	("WAIT FOR LOCATION"),
			COND_WAIT	("CONDITIONAL WAIT");
			
			private final String description;
			
			Type (String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}
			
		};
	
	final private Movable entity;
	final private Type type;
	final private Location location;
	

	/**
	 * @param model
	 * @param ts
	 */
	public EntityLocationInfo(final Simulation model, final Movable entity, final Location location, final Type type, final long ts) {
		super(model, ts);
		this.entity = entity;
		this.location = location;
		this.type = type;
	}



	/**
	 * @return the entity
	 */
	public Movable getEntity() {
		return entity;
	}


	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}


	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + entity.toString() + "\t" + type.getDescription() + "\t" + location; 
	}
}
