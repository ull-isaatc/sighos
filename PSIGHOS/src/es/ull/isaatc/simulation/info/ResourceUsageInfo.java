package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.WorkItem;
import es.ull.isaatc.simulation.info.AsynchronousInfo;

public class ResourceUsageInfo extends AsynchronousInfo {

	/** Possible types of resource information */
	public enum Type {
			CAUGHT	("CAUGHT RESOURCE"), 
			RELEASED	("RELEASED RESOURCE");
			
			private final String description;
			
			Type (String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}		
			
		};
	
	final private Resource res;
	final private ResourceType rt;
	final private WorkItem wi;
	final private Activity act;
	final private Type type;
	
	public ResourceUsageInfo(Simulation simul, Resource res, ResourceType rt, WorkItem wi, Type type, long ts) {
		super(simul, ts);
		this.res = res;
		this.rt = rt;
		this.wi =wi;
		this.act = wi.getActivity();
		this.type = type;
	}
	
	public Resource getResource() {
		return res;
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
	
	public WorkItem getWorkItem() {
		return wi;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t";
		message += wi.getElement().toString() + " \t";
		message += type.getDescription() + "\t" + res.getDescription() + "\t";
		message += "ROLE: " + rt.getDescription() + "\t";	
		message += "ACT: " + act.getDescription();
		return message;
	}

	public Activity getActivity() {
		return act;
	}
}
