package es.ull.iis.simulation.info;

import java.util.ArrayDeque;

import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
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
	final private ArrayDeque<Resource> resources;
	
	public ElementActionInfo(Simulation model, ElementInstance instance, Element elem, ActionFlow act, ActivityWorkGroup wg, ArrayDeque<Resource> resources, Type type, long ts) {
		super(model, ts);
		this.instance = instance;
		this.act = act;
		this.wg = wg;
		this.elem = elem;
		this.type = type;
		this.resources = resources;
	}
	
	/**
	 * Returns the element that performs the last action
	 * @return the element that performs the last action
	 */
	public Element getElement() {
		return elem;
	}
	
	/**
	 * Returns the element instance that performs the last action
	 * @return the element instance that performs the last action
	 */
	public ElementInstance getElementInstance() {
		return instance;
	}
	
	/**
	 * Returns the type of information received
	 * @return the type of information received
	 */
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

	/**
	 * Returns the last activity/releaseFlow/requestFlow used
	 * @return the last activity/releaseFlow/requestFlow used
	 */
	public ActionFlow getActivity() {
		return act;
	}

	/**
	 * Returns the last workgroup used to seize/release resources
	 * @return the last workgroup used to seize/release resources
	 */
	public ActivityWorkGroup getWorkGroup() {
		return wg;
	}
	
	/**
	 * Returns the list of resources seized/released
	 * @return the list of resources seized/released
	 */
	public ArrayDeque<Resource> getResources() {
		return resources;
	}
}
