/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.TreeMap;

import es.ull.isaatc.simulation.Describable;
import es.ull.isaatc.simulation.VariableStore;
import es.ull.isaatc.simulation.flow.Flow;
import es.ull.isaatc.simulation.variable.BooleanVariable;
import es.ull.isaatc.simulation.variable.ByteVariable;
import es.ull.isaatc.simulation.variable.CharacterVariable;
import es.ull.isaatc.simulation.variable.DoubleVariable;
import es.ull.isaatc.simulation.variable.FloatVariable;
import es.ull.isaatc.simulation.variable.IntVariable;
import es.ull.isaatc.simulation.variable.LongVariable;
import es.ull.isaatc.simulation.variable.ShortVariable;
import es.ull.isaatc.simulation.variable.UserVariable;
import es.ull.isaatc.simulation.variable.Variable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Model implements VariableStore, Describable, VariableHandler {
	/** A short text describing this simulation. */
	protected String description;

	/** List of resource types present in the model. */
	protected final TreeMap<Integer, ResourceType> resourceTypeList = new TreeMap<Integer, ResourceType>();

	/** List of activities present in the simulation. */
	protected final TreeMap<Integer, Activity> activityList = new TreeMap<Integer, Activity>();

	/** List of resource types present in the simulation. */
	protected final TreeMap<Integer, ElementType> elementTypeList = new TreeMap<Integer, ElementType>();
	
	/** List of flows present in the simulation */
	protected final TreeMap<Integer, Flow> flowList = new TreeMap<Integer, Flow>();

	/** List of resources present in the simulation. */
	protected final TreeMap<Integer, Resource> resourceList = new TreeMap<Integer, Resource>();

	/** Timestamp of simulation's start */
	protected Time startTs;

	/** Timestamp of Simulation's end */
	protected Time endTs;
	
	/** Time unit to be used to simulate this model */
	protected TimeUnit unit = null;
	
    /** Variable warehouse */
	protected final TreeMap<String, Variable> varCollection = new TreeMap<String, Variable>();

    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();
	
	/**
	 * Creates a new instance of Model
	 */
	public Model() {
	}
	
	/**
	 * Creates a new instance of Model
	 *
	 * @param description
	 *            A short text describing this model.
	 * @param unit
	 * 			  Time unit to be used during the simulation of this model.
	 */
	public Model(String description, TimeUnit unit) {
		this.description = description;
		this.unit = unit;
		userMethods.put("init", "");
		userMethods.put("end", "");
		userMethods.put("beforeClockTick", "");
		userMethods.put("afterClockTick", "");
	}

	/**
	 * Creates a new instance of Model
	 *
	 * @param description
	 *            A short text describing this model.
	 * @param unit
	 * 			  Time unit to be used during the simulation of this model.
	 * @param startTs
	 *            Timestamp of simulation's start
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public Model(String description, TimeUnit unit, Time startTs, Time endTs) {
		this(description, unit);
		this.startTs = startTs;
		this.endTs = endTs;
	}

	/**
	 * Creates a new instance of Model
	 *
	 * @param description
	 *            A short text describing this model.
	 * @param unit
	 * 			  Time unit to be used during the simulation of this model.
	 * @param startTs
	 *            Timestamp of simulation's start expressed in Simulation Time Units
	 * @param endTs
	 *            Timestamp of simulation's end expressed in Simulation Time Units
	 */
	public Model(String description, TimeUnit unit, double startTs, double endTs) {
		this(description, unit);
		this.startTs = new Time(unit, startTs);;
		this.endTs = new Time(unit, endTs);;
	}

	/**
	 * Contains the specifications of the model. All the components of the model
	 * must be declared here.
	 * <p>
	 * The components are added simply by invoking their constructors. For
	 * example: <code>
	 * Activity a1 = new Activity(0, this, "Act1");
	 * ResourceType rt1 = new ResourceType(0, this, "RT1");
	 * </code>
	 */
	protected abstract void createModel();

	/**
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	public double simulationTime2Double(Time source) {
		return unit.convert(source) * Double.MIN_VALUE;
	}
	
	public Time double2SimulationTime(double sourceValue) {
		return new Time(unit, sourceValue / Double.MIN_VALUE);
	}
	
	/**
	 * Returns the simulation start timestamp.
	 * 
	 * @return Returns the startTs.
	 */
	public Time getStartTs() {
		return startTs;
	}

	/**
	 * @param startTs the startTs to set
	 */
	public void setStartTs(Time startTs) {
		this.startTs = startTs;
	}

	/**
	 * Returns the simulation end timestamp.
	 * 
	 * @return Value of property endTs.
	 */
	public Time getEndTs() {
		return endTs;
	}

	/**
	 * @param endTs the endTs to set
	 */
	public void setEndTs(Time endTs) {
		this.endTs = endTs;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Adds an {@link es.ull.isaatc.simulation.model.ResourceType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param rt
	 *            Resource Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ResourceType add(ResourceType rt) {
		return resourceTypeList.put(rt.getIdentifier(), rt);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.model.Activity} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param act
	 *            Activity that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected Activity add(Activity act) {
		return activityList.put(act.getIdentifier(), act);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.model.ElementType} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param et
	 *            Element Type that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	protected ElementType add(ElementType et) {
		return elementTypeList.put(et.getIdentifier(), et);
	}
	
	/**
	 * Adds an {@link es.ull.isaatc.simulation.flow.Flow} to the model. These method
	 * is invoked from the object's constructor.
	 * 
	 * @param f
	 *            Flow that's added to the model.
	 * @return previous value associated with the key of specified object, or <code>null</code>
	 *  if there was no previous mapping for key.
	 */
	public Flow add(Flow f) {
		return flowList.put(f.getIdentifier(), f);
		
	}
	
	/**
	 * Adds a resoruce to the simulation. The resources are automatically added
	 * from their constructor.
	 * 
	 * @param res
	 *            Resource.
	 */
	protected void add(Resource res) {
		resourceList.put(res.getIdentifier(), res);
	}

	/**
	 * Returns a list of the resource types of the model.
	 * 
	 * @return Resource types of the model.
	 */
	public TreeMap<Integer, ResourceType> getResourceTypeList() {
		return resourceTypeList;
	}

	/**
	 * Returns a list of the activities of the model.
	 * 
	 * @return Activities of the model.
	 */
	public TreeMap<Integer, Activity> getActivityList() {
		return activityList;
	}

	/**
	 * Returns a list of the element types of the model.
	 * 
	 * @return element types of the model.
	 */
	public TreeMap<Integer, ElementType> getElementTypeList() {
		return elementTypeList;
	}

	/**
	 * Returns a list of the flows of the model.
	 * 
	 * @return flows of the model.
	 */
	public TreeMap<Integer, Flow> getFlowList() {
		return flowList;
	}

	/**
	 * Returns a list of the resources of the model.
	 * 
	 * @return Resources of the model.
	 */
	public TreeMap<Integer, Resource> getResourceList() {
		return resourceList;
	}

	/**
	 * Returns the resource type with the corresponding identifier.
	 * 
	 * @param id
	 *            Resource type identifier.
	 * @return A resource type with the indicated identifier.
	 */
	public ResourceType getResourceType(int id) {
		return resourceTypeList.get(id);
	}

	/**
	 * Returns the activity with the corresponding identifier.
	 * 
	 * @param id
	 *            Activity identifier.
	 * @return An activity with the indicated identifier.
	 */
	public Activity getActivity(int id) {
		return activityList.get(id);
	}

	/**
	 * Returns the element type with the corresponding identifier.
	 * 
	 * @param id
	 *            element type identifier.
	 * @return An element type with the indicated identifier.
	 */
	public ElementType getElementType(int id) {
		return elementTypeList.get(id);
	}

	/**
	 * Returns the flow with the corresponding identifier.
	 * 
	 * @param id
	 *            flow identifier.
	 * @return A flow with the indicated identifier.
	 */
	public Flow getFlow(int id) {
		return flowList.get(id);
	}

	/**
	 * Returns the resource with the corresponding identifier.
	 * 
	 * @param id
	 *            resource identifier.
	 * @return A resource with the indicated identifier.
	 */
	public Resource getResource(int id) {
		return resourceList.get(id);
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public Variable getVar(String varName) {
		return varCollection.get(varName);
	}
	
	@Override
	public void putVar(String varName, Variable value) {
		varCollection.put(varName, value);
	}
	
	@Override
	public void putVar(String varName, double value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new DoubleVariable(value));
	}
	
	@Override
	public void putVar(String varName, int value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new IntVariable(value));
	}

	@Override
	public void putVar(String varName, boolean value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new BooleanVariable(value));
	}

	@Override
	public void putVar(String varName, char value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new CharacterVariable(value));
	}
	
	@Override
	public void putVar(String varName, byte value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ByteVariable(value));
	}

	@Override
	public void putVar(String varName, float value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new FloatVariable(value));
	}
	
	@Override
	public void putVar(String varName, long value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new LongVariable(value));
	}
	
	@Override
	public void putVar(String varName, short value) {
		UserVariable v = (UserVariable) varCollection.get(varName);
		if (v != null) {
			v.setValue(value);
			varCollection.put(varName, v);
		} else
			varCollection.put(varName, new ShortVariable(value));
	}
	
}
