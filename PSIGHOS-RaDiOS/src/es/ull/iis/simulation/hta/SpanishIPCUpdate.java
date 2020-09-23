/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * A class to update costs according to the Spanish IPC. Uses the values of IPC in January for each year. 
 * Currently updated up to the 2018 IPC. Future uses require adding extra values to both GENERAL_INDEX and HEALTHCARE_INDEX
 * @author Iván Castilla Rodríguez
 *
 */
public class SpanishIPCUpdate {
	/** Last year that we have collected data */
	final static private int LAST_YEAR = 2019;
	/** General indexes for the Spanish IPC, taken from January. The last value corresponds to the last (most updated) year */
	final static private double[] GENERAL_INDEX = {74.585, 77.354, 79.137, 81.575, 84.994, 87.027, 90.749, 91.481, 92.422, 95.444, 97.351, 99.963, 100.162, 98.841, 98.556, 101.488, 102.071, 103.071};
	final static private double[] HEALTHCARE_INDEX = {88.844, 90.976, 92.757, 92.986, 93.613, 95.127, 93.327, 93.392, 92.168, 90.855, 88.212, 98.848, 100.124, 100.025, 99.668, 100.411, 100.718, 101.588};
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
		for (int i = 2014; i < 2019; i++)
			System.out.println(cost + " -->(" + i + ") " + updateCost(cost, i, 2019));
	}
}
