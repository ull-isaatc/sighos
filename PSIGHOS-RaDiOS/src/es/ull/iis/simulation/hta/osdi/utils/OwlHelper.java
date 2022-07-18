package es.ull.iis.simulation.hta.osdi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.service.DataStoreService;


public class OwlHelper {
	private static Map<String, Map<String, List<PropertyData>>> dataPropertyValues = null;
	private static Map<String, Map<String, List<String>>> objectPropertyValues = null;
	private static Map<String, String> instanceToClazz = new HashMap<String, String>();
	
	public static void initilize (Ontology radios) {
		DataStoreService.eTLClassIndividuals(radios, null, instanceToClazz, "#Disease");
		dataPropertyValues = DataStoreService.eTLDataPropertyValues(radios);
		objectPropertyValues = DataStoreService.eTLObjectProperties(radios);
	}

	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology.
	 * If there are is than one value for that property, returns the first one. 
	 * If the data property or the instance are not defined, returns the default value. Be aware that the instance might be not defined simply because 
	 * it does not define any data property, and not because it does not exists.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @param defaultValue Value that is returned when not defined 
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static String getDataPropertyValue (String instanceName, String propertyName, String defaultValue) {
		final Map<String, List<PropertyData>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return defaultValue;
		if (data.get(propertyName) == null)
			return defaultValue;
		return data.get(propertyName).get(0).getValue();
	}
	
	/**
	 * Returns the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 * If the data instance is not defined, returns null. If the data property is not defined, returns an empty list.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static List<String> getDataPropertyValues(String instanceName, String propertyName) {
		final Map<String, List<PropertyData>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return null;
		final ArrayList<String> values = new ArrayList<>();
		if (data.get(propertyName) != null)
			for (PropertyData dataItem : data.get(propertyName)) {
				values.add(dataItem.getValue());
			}
		return values;
	}
	
	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology. 
	 * If the data property or the instance are not defined, returns null.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static String getDataPropertyValue (String instanceName, String propertyName) {
		return getDataPropertyValue(instanceName, propertyName, null);
	}
	
	/**
	 * Returns the object associated to another object by means of the specified object property. If there are more than 
	 * one, returns the first one.  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the object associated to another object by means of the specified object property
	 */
	public static String getObjectPropertyByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return null;
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return null;
		return list.get(0);
	}
	
	/**
	 * Returns the list of objects associated to another object by means of the specified object property  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the list of objects associated to another object by means of the specified object property
	 */
	public static List<String> getObjectPropertiesByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return new ArrayList<>();
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return new ArrayList<>();
		return list;
	}
	
	public static List<String> getChilds(String objectName) {
		List<String> result = null;
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				result.addAll(objectProperties.get(key));
			}
		}
		return result;
	}

	public static List<String> getChildsByClassName(String objectName, String className) {
		List<String> result = new ArrayList<>();
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				List<String> values = objectProperties.get(key);
				if (values != null && !values.isEmpty()) {
					for (String value: values) {
						if (className.equals(instanceToClazz.get(value))) {
							result.add(value);
						}
					}
				}
			}
		}
		return result;
	}
}
