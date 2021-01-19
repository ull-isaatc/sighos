package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Matrix {
	private final Map<String, Map<String, CostMatrixElement>> backingMap;

	public Matrix() {
		this.backingMap = new HashMap<>();
	}

	public Set<String> keySetR () {
		return backingMap.keySet();		
	}
	
	public Set<String> keySetC (String key) {
		return backingMap.get(key).keySet();		
	}
	
	public CostMatrixElement get(String row, String column) {
		Map<String, CostMatrixElement> innerMap = backingMap.get(row);
		return innerMap != null ? innerMap.get(column) : null;
	}

	public void put(String row, String column, CostMatrixElement value) {
		Map<String, CostMatrixElement> innerMap = backingMap.get(row);
		if (innerMap == null) {
			innerMap = new HashMap<>();
			backingMap.put(row, innerMap);
		}
		innerMap.put(column, value);
	}
	
	public Matrix clone () {
		Matrix cloned = new Matrix();
		for (String keyR : this.keySetR()) {
			for (String keyC : this.keySetC(keyR)) {
				cloned.put(keyR, keyC, get(keyR, keyC).clone());
			}
		}
		return cloned;		
	}
}