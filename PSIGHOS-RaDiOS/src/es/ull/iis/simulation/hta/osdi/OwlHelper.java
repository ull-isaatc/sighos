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
	
	public static String getDataPropertyValue (String object, String property) {
		return dataPropertyValues.get(object) != null && dataPropertyValues.get(object).get(property) != null ? dataPropertyValues.get(object).get(property).getValue() : null;
	}
	
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
