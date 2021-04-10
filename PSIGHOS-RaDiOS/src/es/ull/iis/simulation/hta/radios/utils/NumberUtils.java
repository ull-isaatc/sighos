package es.ull.iis.simulation.hta.radios.utils;

public class NumberUtils {

	public static Double asDouble(String str) {
		Double result = null;
		try {
			result = Double.parseDouble(str);
		} catch (NumberFormatException e) {
		}
		return result;
	}
}
