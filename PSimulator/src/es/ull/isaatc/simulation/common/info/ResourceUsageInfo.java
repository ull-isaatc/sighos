package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.AsynchronousInfo;

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
	
	private Resource res;
	private ResourceType rt;
	private WorkItem sf;
	private Activity act;
	private Type type;
	
	public ResourceUsageInfo(Simulation simul, Resource res, ResourceType rt, WorkItem sf, Type type, long ts) {
		super(simul, ts);
		this.res = res;
		this.rt = rt;
		this.sf =sf;
		this.act = sf.getActivity();
		this.type = type;
	}
	
	public Resource getRes() {
		return res;
	}
	
	public void setRes(Resource res) {
		this.res = res;
	}
	
	public ResourceType getRt() {
		return rt;
	}
	
	public void setRt(ResourceType rt) {
		this.rt = rt;
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
		String message = "" + simul.long2SimulationTime(getTs()) + "\t";
		message += sf.getElement().toString() + " \t";
		message += type.getDescription() + "\t" + res.getDescription() + "\t";
		message += "ROLE: " + rt.getDescription() + "\t";	
		message += "ACT: " + act.getDescription();
		return message;
	}

	public Activity getActivity() {
		return act;
	}
}
