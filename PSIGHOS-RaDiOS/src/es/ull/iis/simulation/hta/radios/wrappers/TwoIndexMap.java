package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.HashMap;
import java.util.Map;

public class TwoIndexMap<R, C, V> {
	private final Map<R, Map<C, V>> backingMap;

	public TwoIndexMap() {
		this.backingMap = new HashMap<>();
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
}