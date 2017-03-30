package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;

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
	final private ElementType et;
	
	public ElementInfo(Simulation model, Element elem, ElementType et, Type type, long ts) {
		super(model, ts);
		this.elem = elem;
		this.type = type;
		this.et = et;
	}
	
	public Element getElement() {
		return elem;
	}

	public Type getType() {
		return type;
	}
	
	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + elem.toString() + " \t" + type.getDescription() + "\tET: " + et.getDescription();
	}
}
