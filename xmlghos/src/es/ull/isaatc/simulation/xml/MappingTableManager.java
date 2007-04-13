/**
 * 
 */
package es.ull.isaatc.simulation.xml;

import java.util.Iterator;

import es.ull.isaatc.util.OrderedList;

/**
 * @author Roberto Muñoz
 *
 */
public class MappingTableManager implements Iterable<ModelMappingTable> {

    /** Mapping table */
    OrderedList<ModelMappingTable> modelList = new OrderedList<ModelMappingTable>();

    public OrderedList<ModelMappingTable> getModelList() {
    	return modelList;
    }
    
    public void add(ModelMappingTable mappingTable) {
    	modelList.add(mappingTable);
    }

    public void add(int index, ModelMappingTable mappingTable) {
    	modelList.add(index, mappingTable);
    }

    public ModelMappingTable get(int index) {
    	return modelList.get(Integer.valueOf(index));
    }
    
    public ModelMappingTable remove(int index) {
    	return modelList.remove(index);
    }

    public Iterator<ModelMappingTable> iterator() {
    	return modelList.iterator();
    }
}
