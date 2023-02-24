/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Añadir capacidad para cada muelle
 */
public enum QuayType {
	QUAY1("QUAY_1", new Node("QUAY_1", Vessel.SIZE), TimeFunctionFactory.getInstance("ConstantVariate", 50)),
	QUAY2("QUAY_2", new Node("QUAY_2", Vessel.SIZE), TimeFunctionFactory.getInstance("ConstantVariate", 60)),
	QUAY3("QUAY_3", new Node("QUAY_3", Vessel.SIZE), TimeFunctionFactory.getInstance("ConstantVariate", 70)),
	QUAY4("QUAY_4", new Node("QUAY_4", Vessel.SIZE), TimeFunctionFactory.getInstance("ConstantVariate", 80));
	
	private final String description;
	private final Node location;	
	private final TimeFunction timeFromAnchorage;
	
	private QuayType(String description, Node location, TimeFunction timeFromAnchorage) {
		this.description = description;
		this.location = location;
		this.timeFromAnchorage = timeFromAnchorage;
	}

	public String getDescription() {
		return description;
	}

	public Node getLocation() {
		return location;
	}

	public TimeFunction getTimeFromAnchorage() {
		return timeFromAnchorage;
	}
}
