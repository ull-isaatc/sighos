package es.ull.iis.simulation.info;

import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.core.flow.ResourcesFlow;

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
	final private WorkThread<?> wt;
	final private ResourcesFlow act;
	final private Type type;
	
	public ResourceUsageInfo(Simulation<?> simul, Resource res, ResourceType rt, WorkThread<?> wt, Type type, long ts) {
		super(simul, ts);
		this.res = res;
		this.rt = rt;
		this.wt =wt;
		this.act = (ResourcesFlow)wt.getCurrentFlow();
		this.type = type;
	}
	
	public Resource getResource() {
		return res;
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
	
	public WorkThread<?> getWorkThread() {
		return wt;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t";
		message += wt.getElement().toString() + " \t";
		message += type.getDescription() + "\t" + res.getDescription() + "\t";
		message += "ROLE: " + rt.getDescription() + "\t";	
		message += "ACT: " + act.getDescription();
		return message;
	}

	public ResourcesFlow getActivity() {
		return act;
	}
}
