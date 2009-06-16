/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

/**
 * @author Iván
 *
 */
public class GSElementTypeWaitResults implements GSResult {
	private final double[][] averages;
	private final double[][] stddevs;
	private final int[][][] waitingDays;
	
	/**
	 * @param averages
	 * @param stddevs
	 * @param waitingDays
	 */
	public GSElementTypeWaitResults(double[][] averages, double[][] stddevs, int[][][] waitingDays) {
		this.averages = averages;
		this.stddevs = stddevs;
		this.waitingDays = waitingDays;
	}

	/**
	 * @return the averages
	 */
	public double[][] getAverages() {
		return averages;
	}

	/**
	 * @return the stddevs
	 */
	public double[][] getStddevs() {
		return stddevs;
	}

	/**
	 * @return the waitingDays
	 */
	public int[][][] getWaitingDays() {
		return waitingDays;
	}
}
