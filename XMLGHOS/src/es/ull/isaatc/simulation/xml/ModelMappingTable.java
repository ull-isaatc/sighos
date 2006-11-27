/**
 * 
 */
package es.ull.isaatc.simulation.xml;

import java.util.HashMap;

import es.ull.isaatc.simulation.xml.validation.ModelValidator;
import es.ull.isaatc.util.Orderable;

/**
 * @author Roberto Muñoz
 *
 */
public class ModelMappingTable implements Orderable {
    
    /** XML model */
    Model model;
    
    /** Resource type mapping */
    HashMap<Integer, BaseComponent> rtMapping = new HashMap<Integer, BaseComponent>();
    private static Integer rtCounter = 1;
    
    /** Resource mapping */
    HashMap<Integer, BaseComponent> resMapping = new HashMap<Integer, BaseComponent>();
    private static Integer resCounter = 1;
    
    /** Activity mapping */
    HashMap<Integer, BaseComponent> actMapping = new HashMap<Integer, BaseComponent>();
    private static Integer actCounter = 1;
    
    /** Element type mapping */
    HashMap<Integer, BaseComponent> etMapping = new HashMap<Integer, BaseComponent>();
    private static Integer etCounter = 1;
    
    /** Root flow mapping */
    HashMap<Integer, BaseComponent> rfMapping = new HashMap<Integer, BaseComponent>();
    private static Integer rfCounter = 1;
    
    /**
     * Creates a new mapping table for a model
     * @param model the model
     */
    public ModelMappingTable(Model model) {
	this.model = model;
    }
    
    /**
     * Inserts a component in the mapping table.
     * @param component : original component
     * @param mappingTable : hashmap in which the component will be inserted
     */
    protected void addComponent(BaseComponent component, HashMap<Integer, BaseComponent> mappingTable, int newId) {
	mappingTable.put(component.getId(), component);
	component.setId(newId);
    }
    
    /**
     * Inserts a resource type in the mapping table. The new identifier is asigned automatically
     * @param rt : original resource type
     */
    public void addComponent(ResourceType rt) {
	addComponent(rt, rtMapping, rtCounter++);
    }
    
    /**
     * Inserts a resource in the mapping table. The new identifier is asigned automatically
     * @param res : original resource
     */
    public void addComponent(Resource res) {
	addComponent(res, resMapping, resCounter);
	resCounter += res.getUnits();
    }

    /**
     * Inserts an activity in the mapping table. The new identifier is asigned automatically
     * @param act : original activity
     */
    public void addComponent(Activity act) {
	addComponent(act, actMapping, actCounter++);
    }

    /**
     * Inserts an element type in the mapping table. The new identifier is asigned automatically
     * @param et : original elementType
     */
    public void addComponent(ElementType et) {
	addComponent(et, etMapping, etCounter++);
    }

    /**
     * Inserts a root flow in the mapping table. The new identifier is asigned automatically
     * @param rf : original root flow
     */
    public void addComponent(RootFlow rf) {
	addComponent(rf, rfMapping, rfCounter++);
    }

    public ResourceType getResourceType(int id) {
	return (ResourceType)rtMapping.get(id);
    }
    
    public Resource getResource(int id) {
	return (Resource)resMapping.get(id);
    }

    public Activity getActivity(int id) {
	return (Activity)actMapping.get(id);
    }

    public ElementType getElementType(int id) {
	return (ElementType)etMapping.get(id);
    }
    
    public RootFlow getRootFlow(int id) {
	return (RootFlow)rfMapping.get(id);
    }
    

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    public int compareTo(Orderable o) {
	return compareTo(o.getKey());
    }

    public Comparable getKey() {
	return model.getId();
    }

    public int compareTo(Object o) {
	return getKey().compareTo(o);
    }
}
