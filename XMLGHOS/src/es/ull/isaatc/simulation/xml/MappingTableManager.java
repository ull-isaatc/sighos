/**
 * 
 */
package es.ull.isaatc.simulation.xml;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * @author Roberto Muñoz
 *
 */
public class MappingTableManager implements Iterable<ModelMappingTable> {

    /** Mapping table */
    TreeMap<Integer, ModelMappingTable> modelList = new TreeMap<Integer, ModelMappingTable>();

    public TreeMap<Integer, ModelMappingTable> getModelList() {
    	return modelList;
    }
    
    public void add(ModelMappingTable mappingTable) {
    	modelList.put(mappingTable.getId(), mappingTable);
    }

    public void add(int index, ModelMappingTable mappingTable) {
    	modelList.put(index, mappingTable);
    }

    public ModelMappingTable get(int index) {
    	return modelList.get(index);
    }
    
    /**
     * Removes and returns the ModelMappingTable with lowest key 
     * @return the ModelMappingTable with lowest key
     */
    public ModelMappingTable pop() {
    	return modelList.remove(modelList.firstKey());
    }

    public Iterator<ModelMappingTable> iterator() {
    	return modelList.values().iterator();
    }
}
