package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.AsynchronousInfo;

public class ElementInfo extends AsynchronousInfo {

	/** Possible types of element information */
	public enum Type {
			START ("ELEMENT START"), 
			FINISH ("ELEMENT FINISH");
			
			private final String description;
			
			Type(String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}

		};
	
	final private Element elem;
	final private Type type;
	
	public ElementInfo(Simulation simul, Element elem, Type type, long ts) {
		super(simul, ts);
		this.elem = elem;
		this.type = type;
	}
	
	public Element getElement() {
		return elem;
	}

	public Type getType() {
		return type;
	}
	
	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + elem.toString() + " \t" + type.getDescription() + "\tET: " + elem.getType().getDescription();
	}
}
