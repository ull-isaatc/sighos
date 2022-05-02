package es.ull.iis.simulation.hta.osdi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface CollectionUtils {
	/**
	 * @param <T>
	 * @param c
	 * @return
	 */
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}
	
	/**
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty (Collection<?> collection) {
		return !(collection != null && !collection.isEmpty());		
	}

	/**
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmpty (Collection<?> collection) {
		return (collection != null && !collection.isEmpty());		
	}
	
	/**
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmptyAndOnlyOneElement (Collection<?> collection) {
		return (collection != null && collection.size() == 1);		
	}
	
	/**
	 * @param size
	 * @param initValue
	 * @return
	 */
	public static List<Double> createInitializeDoubleList (Integer size, Double initValue) {
		List<Double> result = new ArrayList<Double>();
		if (size != null && size > 0) {
			for (int i = 0; i < size; i++) {
				result.add(initValue);
			}
		}
		return result;
	}
	
	/**
	 * @param list
	 * @param initialIndex
	 * @return
	 */
	public static List<String> transformDoubleListToFormattedStringList (List<Double> list, Integer initialIndex) {
		return transformDoubleListToFormattedStringList(list, null, initialIndex);
	}

	/**
	 * @param list
	 * @param patterTransform
	 * @return
	 */
	public static List<String> transformDoubleListToFormattedStringList (List<Double> list, String patterTransform, Integer initialIndex) {
		List<String> result = new ArrayList<String>();
		if (isNotEmpty(list)) {
			String pattern = "%.4f";
			if (patterTransform != null) {
				pattern = patterTransform;
			}
			if (initialIndex != null && initialIndex < list.size()) {
				for (int i = 0; i < initialIndex; i++) {
					result.add(list.get(i).toString());				
				}				
				for (int i = initialIndex; i < list.size(); i++) {
					result.add(String.format(pattern, list.get(i)));				
				}				
			}				
		}
		return result;
	}

	/**
	 * @param list
	 * @param initialIndex
	 * @return
	 */
	public static List<String> transformDoubleValueStringListToFormattedStringList (List<String> list, Integer initialIndex) {
		return transformDoubleValueStringListToFormattedStringList(list, null, initialIndex);
	}

	/**
	 * @param doubleList
	 * @param patterTransform
	 * @return
	 */
	public static List<String> transformDoubleValueStringListToFormattedStringList (List<String> list, String patterTransform, Integer initialIndex) {
		List<String> result = new ArrayList<String>();
		if (isNotEmpty(list)) {
			String pattern = "%.4f";
			if (patterTransform != null) {
				pattern = patterTransform;
			}
			if (initialIndex != null && initialIndex < list.size()) {
				for (int i = 0; i < initialIndex; i++) {
					result.add(list.get(i));				
				}				
				for (int i = initialIndex; i < list.size(); i++) {
					result.add(String.format(pattern, Double.parseDouble(list.get(i))));				
				}				
			}
		}
		return result;
	}
}
