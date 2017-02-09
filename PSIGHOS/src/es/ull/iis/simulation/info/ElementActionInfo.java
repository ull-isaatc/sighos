package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.ActivityWorkGroup;
import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.core.flow.RequestResourcesFlow;

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
	
	final private WorkThread<?> wThread;
	final private RequestResourcesFlow<?, ?> act;
	final private ActivityWorkGroup wg;
	final private Element elem;
	final private Type type;
	
	public ElementActionInfo(Simulation simul, WorkThread<?> wThread, Type type, long ts) {
		super(simul, ts);
		this.wThread = wThread;
		this.act = (RequestResourcesFlow<?, ?>)wThread.getCurrentFlow();
		this.wg = wThread.getExecutionWG();
		this.elem = wThread.getElement();
		this.type = type;
	}
	
	public Element getElement() {
		return elem;
	}
	
	public WorkThread<?> getWorkThread() {
		return wThread;
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
		message += "\tET: " + elem.getType().getDescription();
		return message;
	}

	public RequestResourcesFlow<?, ?> getActivity() {
		return act;
	}

	public ActivityWorkGroup getWorkGroup() {
		return wg;
	}
}
