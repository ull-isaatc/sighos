package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.flow.ResourceHandlerFlow;

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
	final private ElementInstance instance;
	final private ResourceHandlerFlow act;
	final private Type type;
	final private Element elem;
	
	public ResourceUsageInfo(Simulation model, Resource res, ResourceType rt, ElementInstance instance, Element elem, ResourceHandlerFlow act, Type type, long ts) {
		super(model, ts);
		this.res = res;
		this.rt = rt;
		this.instance = instance;
		this.elem = elem;
		this.act = act;
		this.type = type;
	}
	
	public Resource getResource() {
		return res;
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
	
	public ElementInstance getElementInstance() {
		return instance;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t" + elem.toString() + "\t";
		message += type.getDescription() + "\t" + res.toString() + "\t" + res.getDescription() + "\t";
		message += "ROLE: " + rt.getDescription() + "\t";	
		message += "ACT: " + act.getDescription() + " \t";
		return message;
	}

	public ResourceHandlerFlow getActivity() {
		return act;
	}
}
