package es.ull.iis.simulation.hta.osdi.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface MapUtils {
	public static Map<String, Map<String, PropertyData>> cloneComplexHashMap(Map<String, Map<String, PropertyData>> original) {
		Map<String, Map<String, PropertyData>> copy = new HashMap<String, Map<String, PropertyData>>();

		for (String key : original.keySet()) {
			copy.put(new String(key), new HashMap<String, PropertyData>(original.get(key)));
		}

		return copy;
	}

	public static Map<String, PropertyData> cloneSimpleHashMap(Map<String, PropertyData> original) {
		Map<String, PropertyData> copy = new HashMap<String, PropertyData>();

		for (String key : original.keySet()) {
			copy.put(new String(key), original.get(key).clone());
		}

		return copy;
	}

	public static Map<String, Map<String, PropertyData>> subSetComplexHashMap(Map<String, Map<String, PropertyData>> original, List<String> toRemove) {
		Map<String, Map<String, PropertyData>> copy = new HashMap<String, Map<String, PropertyData>>();

		for (String key : original.keySet()) {
			if (!toRemove.contains(key)) {
				copy.put(new String(key), new HashMap<String, PropertyData>(original.get(key)));
			}
		}

		return copy;
	}

	public static Map<String, PropertyData> subSetSimpleHashMap(Map<String, PropertyData> original, List<String> toRemove) {
		Map<String, PropertyData> copy = new HashMap<String, PropertyData>();

		for (String key : original.keySet()) {
			if (!toRemove.contains(key)) {
				copy.put(new String(key), original.get(key).clone());
			}
		}

		return copy;
	}
	
	public static Map<String, Map<String, PropertyData>> transformHashMapToLinkedHashMap (Map<String, Map<String, PropertyData>> manifestations, List<String> sortedKeyList, String splitPattern, Integer splittedIndex) {
		Map<String, Map<String, PropertyData>> result = new LinkedHashMap<String, Map<String,PropertyData>>();
		for (String item : sortedKeyList) {
			String key = item; 
			if (splitPattern != null) {
				key = item.split(splitPattern)[splittedIndex];
			}			
			result.put(key, manifestations.get(key));
		}
		return result;
	}
}
