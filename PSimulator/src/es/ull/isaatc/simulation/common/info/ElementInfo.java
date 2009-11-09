package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.AsynchronousInfo;

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
	
	private Element elem;
	private Type type;
	
	public ElementInfo(Simulation simul, Element elem, Type type, double ts) {
		super(simul, ts);
		this.elem = elem;
		this.type = type;
	}
	
	public Element getElem() {
		return elem;
	}
	public void setElem(Element elem) {
		this.elem = elem;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		return "" + simul.double2SimulationTime(getTs()) + "\t" + elem.toString() + " \t" + type.getDescription() + "\tET: " + elem.getType().getDescription();
	}
}
