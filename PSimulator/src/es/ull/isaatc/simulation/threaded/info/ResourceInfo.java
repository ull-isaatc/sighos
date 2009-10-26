package es.ull.isaatc.simulation.threaded.info;

import java.util.EnumSet;

import es.ull.isaatc.simulation.info.AsynchronousInfo;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;

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
	
	private Resource res;
	private ResourceType rt;
	private Type type;
	
	public ResourceInfo(Simulation simul, Resource res, ResourceType rt, Type type, double ts) {
		super(simul, ts);
		this.res = res;
		this.rt = rt;
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
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		String message = "" + simul.double2SimulationTime(getTs()) + "\t" + res.toString() + "\t" + res.getDescription() + "\t" + type.getDescription();
		if ((EnumSet.of(type).equals(EnumSet.of(Type.ROLON))) || (EnumSet.of(type).equals(EnumSet.of(Type.ROLOFF)))) {
			message += "\tRT: " + rt.getDescription(); 
		}
		return message;
	}
}
