package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.ActionFlow;

public class ElementActionInfo extends AsynchronousInfo {
	
	/** Possible types of element information */
	public enum Type {
			REQ		("REQUEST RESOURCES"),
			ACQ		("ACQUIRE RESOURCES"),
			START	("START DELAY"), 
			END		("END DELAY"),
			REL		("RELEASE RESOURCES"),
			RESACT	("RESUME ACTIVITY"), 
			INTACT	("INTERRUPT ACTIVITY");
			
			private final String description;
			
			Type (String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}
			
		};
	
	final private ElementInstance instance;
	final private ActionFlow act;
	final private ActivityWorkGroup wg;
	final private Element elem;
	final private Type type;
	
	public ElementActionInfo(Simulation model, ElementInstance instance, Element elem, ActionFlow act, ActivityWorkGroup wg, Type type, long ts) {
		super(model, ts);
		this.instance = instance;
		this.act = act;
		this.wg = wg;
		this.elem = elem;
		this.type = type;
	}
	
	public Element getElement() {
		return elem;
	}
	
	public ElementInstance getElementInstance() {
		return instance;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t";
		message += elem.toString() + " \t" + type.getDescription();
		message += "\tACT: " + act.getDescription();
		if (wg != null) {
			message += "\tWG: " + wg.getDescription();
		}
		return message;
	}

	public ActionFlow getActivity() {
		return act;
	}

	public ActivityWorkGroup getWorkGroup() {
		return wg;
	}
}
