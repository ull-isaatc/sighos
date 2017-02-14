package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.simulation.model.flow.ResourcesFlow;

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
	final private FlowExecutor fExecutor;
	final private ResourcesFlow act;
	final private Type type;
	
	public ResourceUsageInfo(Simulation simul, Resource res, ResourceType rt, FlowExecutor fExecutor, ResourcesFlow act, Type type, long ts) {
		super(simul, ts);
		this.res = res;
		this.rt = rt;
		this.fExecutor =fExecutor;
		this.act = act;
		this.type = type;
	}
	
	public Resource getResource() {
		return res;
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
	
	public FlowExecutor getFlowExecutor() {
		return fExecutor;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t";
		message += fExecutor.toString() + " \t";
		message += type.getDescription() + "\t" + res.getDescription() + "\t";
		message += "ROLE: " + rt.getDescription() + "\t";	
		message += "ACT: " + act.getDescription();
		return message;
	}

	public ResourcesFlow getActivity() {
		return act;
	}
}
