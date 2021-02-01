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
}