/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SAPIntervention extends T1DMMonitoringIntervention {
	public final static String NAME = "SAP";
	private final double annualCost;
	/**
	 * @param id
	 * @param shortName
	 * @param description
	 */
	public SAPIntervention(int id, double annualCost) {
		super(id, NAME, NAME);
		this.annualCost = annualCost;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getHBA1cLevel(T1DMPatient pat) {
		return pat.getBaselineHBA1c();
//		return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getAnnualCost(T1DMPatient pat) {
		return annualCost;
	}

}
