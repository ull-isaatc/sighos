/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.util.Output;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class Model {
	private final static TimeUnit defTimeUnit = TimeUnit.MINUTE; 
	private int elemCounter = 0;
	/** Output for printing debug and error messages */
	private static Output out = new Output();
//	private final ArrayList<EventSource> eventSourceList = new ArrayList<EventSource>();
	private final ArrayList<ElementType> elementTypeList = new ArrayList<ElementType>();
	private final ArrayList<Resource> resourceList = new ArrayList<Resource>();
	private final ArrayList<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
	private final ArrayList<WorkGroup> workGroupList = new ArrayList<WorkGroup>();
	private final ArrayList<Flow> flowList = new ArrayList<Flow>();
//	private final ArrayList<Element> elemList = new ArrayList<Element>();
	private final ArrayList<ElementGenerator> genList = new ArrayList<ElementGenerator>();
	private final TimeUnit unit;

	/**
	 * 
	 */
	public Model() {
		this(defTimeUnit);
	}

	public Model(TimeUnit unit) {
		this.unit = unit;
	}
	/**
	 * @return the defTimeUnit
	 */
	public static TimeUnit getDefTimeUnit() {
		return defTimeUnit;
	}

	/**
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	public static void debug(String description) {
		out.debug(description);
	}

	public static void error(String description) {
		out.error(description);
	}

	public static boolean isDebugEnabled() {
		return out.isDebugEnabled();
	}
	
	public int getNewElementId() {
		return elemCounter++;
	}
	
	/**
	 * Resets variables or contents of the model. It should be invoked by the user when the same model is used for multiple replicas
	 * and contains variables that must be initialized among replicas.
	 */
	public void reset() {		
	}
	
//	public void add(EventSource ev) { 
//		eventSourceList.add(ev);
//	}
	public void add(ElementType et) { 
		elementTypeList.add(et);
	}
	public void add(Resource res) { 
		resourceList.add(res);
	}
	public void add(ResourceType rt) { 
		resourceTypeList.add(rt);
	}
	public void add(WorkGroup wg) { 
		workGroupList.add(wg);
	}
	public void add(Flow f) { 
		flowList.add(f);
	}
	public void add(ElementGenerator gen) {
	genList.add(gen);
}
//	public void add(Element elem) {
//		elemList.add(elem);
//	}

//	public List<EventSource> getEventSourceList() { 
//		return eventSourceList;
//	}
	public List<ElementType> getElementTypeList() { 
		return elementTypeList;
	}
	public List<Resource> getResourceList() { 
		return resourceList;
	}
	public List<ResourceType> getResourceTypeList() { 
		return resourceTypeList;
	}
	public List<WorkGroup> getWorkGroupList() { 
		return workGroupList;
	}
	public List<Flow> getFlowList() { 
		return flowList;
	}
	public List<ElementGenerator> getElementGeneratorList() {
		return genList;
	}
//	public List<Element> getElementList() {
//	return elemList;
//}
}
