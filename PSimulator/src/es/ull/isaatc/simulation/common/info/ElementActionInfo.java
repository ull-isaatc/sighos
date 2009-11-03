package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Model;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.ActivityWorkGroup;
import es.ull.isaatc.simulation.common.info.AsynchronousInfo;

public class ElementActionInfo extends AsynchronousInfo {

	/** Possible types of element information */
	public enum Type {
			REQACT	("REQUEST ACTIVITY"), 
			STAACT	("START ACTIVITY"), 
			ENDACT	("END ACTIVITY"), 
			RESACT	("RESTART ACTIVITY"), 
			INTACT	("INTERRUPT ACTIVITY");
			
			private final String description;
			
			Type (String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}
			
		};
	
	private WorkItem sf;
	private Activity act;
	private ActivityWorkGroup wg;
	private Element elem;
	private Type type;
	
	public ElementActionInfo(Model simul, WorkItem sf, Element elem, Type type, double ts) {
		super(simul, ts);
		this.sf = sf;
		this.act = sf.getActivity();
		this.wg = sf.getExecutionWG();
		this.elem = elem;
		this.type = type;
	}
	
	public Element getElem() {
		return elem;
	}
	
	public void setElem(Element elem) {
		this.elem = elem;
	}
	
	public WorkItem getSf() {
		return sf;
	}
	
	public void setSf(WorkItem sf) {
		this.sf = sf;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		String message = "" + simul.double2SimulationTime(getTs()) + "\t";
		message += elem.toString() + " \t" + type.getDescription();
		message += "\tACT: " + act.getDescription();
		if (wg != null) {
			message += "\tWG: " + wg.getDescription();
		}
		message += "\tET: " + elem.getType().getDescription();
		return message;
	}

	public Activity getActivity() {
		return act;
	}

	public ActivityWorkGroup getWg() {
		return wg;
	}
}
