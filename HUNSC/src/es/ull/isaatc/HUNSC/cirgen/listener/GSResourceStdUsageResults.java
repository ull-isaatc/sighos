/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

/**
 * @author Iván
 *
 */
public class GSResourceStdUsageResults implements GSResult {
	private final double []resUsageSummary;
	private final double []resAvailabilitySummary;

	/**
	 * @param resUsageSummary
	 * @param resAvailabilitySummary
	 */
	public GSResourceStdUsageResults(double[] resUsageSummary, double[] resAvailabilitySummary) {
		this.resUsageSummary = resUsageSummary;
		this.resAvailabilitySummary = resAvailabilitySummary;
	}

	/**
	 * @return the resUsage
	 */
	public double[] getResUsageSummary() {
		return resUsageSummary;
	}

	/**
	 * @return the resAvailability
	 */
	public double[] getResAvailabilitySummary() {
		return resAvailabilitySummary;
	}
	
}
