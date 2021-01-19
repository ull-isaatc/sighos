package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TwoIndexMap<R, C, V> {
	private final Map<R, Map<C, V>> backingMap;

	public TwoIndexMap() {
		this.backingMap = new HashMap<>();
	}

	public Set<R> keySetR () {
		return backingMap.keySet();		
	}
	
	public Set<C> keySetC (R key) {
		return backingMap.get(key).keySet();		
	}
	
	public V get(R row, C column) {
		Map<C, V> innerMap = backingMap.get(row);
		return innerMap != null ? innerMap.get(column) : null;
	}

	public void put(R row, C column, V value) {
		Map<C, V> innerMap = backingMap.get(row);
		if (innerMap == null) {
			innerMap = new HashMap<C, V>();
			backingMap.put(row, innerMap);
		}
		innerMap.put(column, value);
	}
	
	public TwoIndexMap<R, C, V> clone () {
		TwoIndexMap<R, C, V> cloned = new TwoIndexMap<>();
		for (R keyR : this.keySetR()) {
			for (C keyC : this.keySetC(keyR)) {
				cloned.put(keyR, keyC, get(keyR, keyC));
			}
		}
		return cloned;		
	}
	
	public static void main(String[] args) {
		TwoIndexMap<String, String, CostMatrixElement> a = new TwoIndexMap<>();
		a.put("a", "a.1", new CostMatrixElement("a", "a.1", 0.0, new ArrayList<>()));
		a.put("a", "a.2", new CostMatrixElement("a", "a.2", 2.0, new ArrayList<>()));
		a.put("b", "b.1", new CostMatrixElement("b", "b.1", 4.0, new ArrayList<>()));
		TwoIndexMap<String, String, CostMatrixElement> b = a.clone(); 

		for (String keyR : a.keySetR()) {
			for (String keyC : a.keySetC(keyR)) {
				System.out.println(String.format("%s %s %s", keyR, keyC, a.get(keyR, keyC)));
			}
		}
		
		System.out.println();

		for (String keyR : b.keySetR()) {
			for (String keyC : b.keySetC(keyR)) {
				System.out.println(String.format("%s %s %s", keyR, keyC, b.get(keyR, keyC)));
			}
		}

		System.out.println();

		a.get("a", "a.1").setCondition("AAAAAAAAAAAAA");
		for (String keyR : a.keySetR()) {
			for (String keyC : a.keySetC(keyR)) {
				System.out.println(String.format("%s %s %s", keyR, keyC, a.get(keyR, keyC)));
			}
		}

		System.out.println();

		for (String keyR : b.keySetR()) {
			for (String keyC : b.keySetC(keyR)) {
				System.out.println(String.format("%s %s %s", keyR, keyC, b.get(keyR, keyC)));
			}
		}
	}
}