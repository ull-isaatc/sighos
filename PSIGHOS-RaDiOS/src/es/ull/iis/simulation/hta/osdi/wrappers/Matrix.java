package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.CostUtils;

public class Matrix {
	private final Map<String, Map<String, List<CostMatrixElement>>> backingMap;

	public Matrix() {
		this.backingMap = new HashMap<>();
	}

	public Set<String> keySetR () {
		return backingMap.keySet();		
	}
	
	public Set<String> keySetC (String key) {
		return backingMap.get(key).keySet();		
	}
	
	public List<CostMatrixElement> get(String row, String column) {
		Map<String, List<CostMatrixElement>> innerMap = backingMap.get(row);
		return innerMap != null ? innerMap.get(column) : null;
	}

	public void put(String row, String column, List<CostMatrixElement> value) {
		Map<String, List<CostMatrixElement>> innerMap = backingMap.get(row);
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
				List<CostMatrixElement> sourceList = get(keyR, keyC);
				List<CostMatrixElement> clonedList = new ArrayList<>();
				for (CostMatrixElement e: sourceList) {
					clonedList.add(e.clone());
				}				
				cloned.put(keyR, keyC, clonedList);
			}
		}
		return cloned;		
	}
	
	@Override
	public String toString() {
		return CostUtils.showCostMatrix(this, Constants.CONSTANT_EMPTY_STRING);
	}
}