/**
 * 
 */
package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.MovableElement;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementLocationInfo extends AsynchronousInfo {
	/** Possible types of element information */
	public enum Type {
			ARRIVE	("ARRIVE AT LOCATION"),
			LEAVE	("LEAVE FROM LOCATION"),
			START	("START AT LOCATION");
			
			private final String description;
			
			Type (String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}
			
		};
	
	final private MovableElement elem;
	final private Type type;
	final private Location location;
	

	/**
	 * @param model
	 * @param ts
	 */
	public ElementLocationInfo(Simulation model, MovableElement elem, Location location, Type type, long ts) {
		super(model, ts);
		this.elem = elem;
		this.location = location;
		this.type = type;
	}



	/**
	 * @return the elem
	 */
	public MovableElement getElem() {
		return elem;
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
		return "" + simul.long2SimulationTime(getTs()) + "\t" + elem.toString() + "\t" + type.getDescription() + "\t" + location; 
	}
}
