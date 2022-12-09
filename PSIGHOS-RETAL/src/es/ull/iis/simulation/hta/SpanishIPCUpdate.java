/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * A class to update costs according to the Spanish IPC. Uses the values of IPC in January for each year. 
 * Currently updated up to the 2021 IPC. Future uses require adding extra values to both GENERAL_INDEX and HEALTHCARE_INDEX
 * @author Iván Castilla Rodríguez
 *
 */
public class SpanishIPCUpdate {
	/** Last year that we have collected data */
	final static private int LAST_YEAR = 2022;
	/** General indexes for the Spanish IPC, taken from January. The last value corresponds to the last (most updated) year */
	final static private double[] GENERAL_INDEX = {69.53, 72.111, 73.773, 76.046, 79.234, 81.129, 84.598, 85.281, 86.158, 88.975, 90.753, 93.188, 93.373, 92.141, 91.876, 94.609, 95.153, 96.085, 97.139, 97.583, 103.567};
	final static private double[] HEALTHCARE_INDEX = {86.313, 88.385, 90.115, 90.337, 90.946, 92.417, 90.668, 90.732, 89.542, 88.267, 85.699, 96.032, 97.271, 97.175, 96.828, 97.551, 97.848, 98.694, 99.149, 99.62, 100.5};
	/** First year that we have collected data */
	final static private int FIRST_YEAR = LAST_YEAR - GENERAL_INDEX.length + 1;
	/**
	 * 
	 */
	public SpanishIPCUpdate() {
	}

	public static double updateCost(double cost, int originalYear, int newYear) {
		return updateCost(cost, originalYear, newYear, true); 
	}
	
	public static double updateCost(double cost, int originalYear, int newYear, boolean useGeneralIndex) {
		if (originalYear == newYear) {
			return cost;
		}
		else if (originalYear > LAST_YEAR || originalYear < FIRST_YEAR) {
			return Double.NaN;
		}
		else if (newYear > LAST_YEAR || newYear < FIRST_YEAR) {
			return Double.NaN;
		}
		final double[] ipc = useGeneralIndex ? GENERAL_INDEX : HEALTHCARE_INDEX;
		final double original_ipc = ipc[originalYear - FIRST_YEAR];
		final double new_ipc = ipc[newYear - FIRST_YEAR];
		return cost * Math.round(1000 * new_ipc / original_ipc) / 1000d;
	}
	
	public static void main(String[] args) {
		double cost = 100;
		for (int i = 2014; i < LAST_YEAR; i++)
			System.out.println(cost + " -->(" + i + ") " + updateCost(cost, i, LAST_YEAR));
	}
}
