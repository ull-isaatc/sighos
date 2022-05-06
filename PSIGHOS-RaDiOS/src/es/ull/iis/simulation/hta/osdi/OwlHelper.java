package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.service.DataStoreService;


public class OwlHelper {
	private static Map<String, Map<String, PropertyData>> dataPropertyValues = null;
	private static Map<String, Map<String, List<String>>> objectPropertyValues = null;
	private static Map<String, String> instanceToClazz = new HashMap<String, String>();
	
	public static void initilize (Ontology radios) {
		DataStoreService.eTLClassIndividuals(radios, null, instanceToClazz, "#Disease");
		dataPropertyValues = DataStoreService.eTLDataPropertyValues(radios);
		objectPropertyValues = DataStoreService.eTLObjectProperties(radios);
	}

	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology. 
	 * If the data property is not defined, returns the default value.
	 * In any case, returns null if the instance is not defined.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @param defaultValue Value that is returned when not defined 
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static String getDataPropertyValue (String instanceName, String propertyName, String defaultValue) {
		final Map<String, PropertyData> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return null;
		if (data.get(propertyName) == null)
			return defaultValue;
		return data.get(propertyName).getValue();
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
	 * Returns the list of objects associated to another object by means of the specified object property  
	 * @param objectName Name of the original object
	 * @param objectPropertyName Name of the property
	 * @return the list of objects associated to another object by means of the specified object property
	 */
	public static List<String> getObjectPropertiesByName(String objectName, String objectPropertyName) {
		return objectPropertyValues.get(objectName) != null ? objectPropertyValues.get(objectName).get(objectPropertyName) : new ArrayList<>();
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
