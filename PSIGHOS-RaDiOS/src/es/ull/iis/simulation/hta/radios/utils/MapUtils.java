package es.ull.iis.simulation.hta.radios.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;

public class MapUtils {

	public static Map<String, Double[][]> cloneCostMapMatrix(Map<String, Double[][]> source) {
		if (source == null) {
			return null;
		}

		Map<String, Double[][]> result = new HashMap<>();
		for (String key : source.keySet()) {
			String newKey = new String(key);
			Double[][] newValue = null;
			if (source.get(key).length > 0) {
				newValue = new Double[source.get(key).length][source.get(key)[0].length];
				for (int i = 0; i < source.get(key).length; i++) {
					newValue[i] = Arrays.copyOf(source.get(key)[i], source.get(key)[i].length);
				}
			}
			result.put(newKey, newValue);
		}
		return result;
	}

	public static Map<String, CostMatrixElement> cloneCostMapList(Map<String, CostMatrixElement> source) {
		if (source == null) {
			return null;
		}

		Map<String, CostMatrixElement> result = new HashMap<>();
		for (String key : source.keySet()) {
			result.put(new String(key), CostMatrixElement.clone(source.get(key)));
		}
		return result;
	}
}
