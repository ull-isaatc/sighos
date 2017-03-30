package es.ull.iis.simulation.info;

import java.util.EnumSet;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;

public class ResourceInfo extends AsynchronousInfo {

	/** Possible types of resource information */
	public enum Type {
			START	("RESOURCE START"), 
			FINISH	("RESOURCE FINISH"), 
			ROLON	("ROLE ON"), 
			ROLOFF	("ROLE OFF"), 
			CANCELON	("CANCELATION PERIOD START"), 
			CANCELOFF	("CANCELATION PERIOD FINISH");
			
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
	final private Type type;
	
	public ResourceInfo(Simulation model, Resource res, ResourceType rt, Type type, long ts) {
		super(model, ts);
		this.res = res;
		this.rt = rt;
		this.type = type;
	}
	
	public Resource getResource() {
		return res;
	}
	
	public ResourceType getResourceType() {
		return rt;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		String message = "" + simul.long2SimulationTime(getTs()) + "\t" + res.toString() + "\t" + type.getDescription() + "\t" + res.getDescription();
		if ((EnumSet.of(type).equals(EnumSet.of(Type.ROLON))) || (EnumSet.of(type).equals(EnumSet.of(Type.ROLOFF)))) {
			message += "\tRT: " + rt.getDescription(); 
		}
		return message;
	}
}
