/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * A class to update costs according to the Spanish Consumer Price Index (CPI). Uses the values of CPI in January for each year. 
 * Currently updated up to the 2021 CPI. Future uses require adding extra values to both GENERAL_INDEX and HEALTHCARE_INDEX
 * @author Iván Castilla Rodríguez
 *
 */
public class SpanishCPIUpdate {
	/** Last year that we have collected data */
	final static private int LAST_YEAR = 2021;
	/** General indexes for the Spanish IPC, taken from January. The last value corresponds to the last (most updated) year */
	final static private double[] GENERAL_INDEX = {74.585, 77.354, 79.137, 81.575, 84.994, 87.027, 90.749, 91.481, 92.422, 95.444, 97.351, 99.963, 100.162, 98.841, 98.556, 101.488, 102.071, 103.071, 104.202, 104.678};
	final static private double[] HEALTHCARE_INDEX = {88.844, 90.976, 92.757, 92.986, 93.613, 95.127, 93.327, 93.392, 92.168, 90.855, 88.212, 98.848, 100.124, 100.025, 99.668, 100.411, 100.718, 101.588, 102.056, 102.503};
	/** First year that we have collected data */
	final static private int FIRST_YEAR = LAST_YEAR - GENERAL_INDEX.length + 1;

	/**
	 * Updates a cost from originalYear to its newYear value using the general CPI 
	 * @param cost Cost collected in a specified year
	 * @param originalYear Year when the cost was collected
	 * @param newYear Year the cost needs to be updated to
	 * @return The cost updated to newYear
	 */
	public static double updateCost(double cost, int originalYear, int newYear) {
		return updateCost(cost, originalYear, newYear, true); 
	}
	
	/**
	 * Updates a cost from originalYear to its newYear value using the general CPI or the healthcare-specific CPI 
	 * @param cost Cost collected in a specified year
	 * @param originalYear Year when the cost was collected
	 * @param newYear Year the cost needs to be updated to
	 * @param useGeneralIndex
	 * @return The cost updated to newYear
	 */
	public static double updateCost(double cost, int originalYear, int newYear, boolean useGeneralIndex) {
		if (originalYear == newYear) {
			return cost;
		}
		else if (originalYear > LAST_YEAR || originalYear < FIRST_YEAR) {
			return Double.NaN;
		}
		else if (newYear < FIRST_YEAR) {
			return Double.NaN;
		}
		// This condition prevents the method from throwing NaN when the CPI table is not updated 
		else if (newYear > LAST_YEAR) {
			return newYear = LAST_YEAR;
		}
		final double[] cpi = useGeneralIndex ? GENERAL_INDEX : HEALTHCARE_INDEX;
		final double original_cpi = cpi[originalYear - FIRST_YEAR];
		final double new_cpi = cpi[newYear - FIRST_YEAR];
		return cost * Math.round(1000 * new_cpi / original_cpi) / 1000d;
	}
	
	public static void main(String[] args) {
		double cost = 100;
		for (int i = 2014; i < LAST_YEAR; i++)
			System.out.println(cost + " -->(" + i + ") " + updateCost(cost, i, LAST_YEAR));
	}
}
