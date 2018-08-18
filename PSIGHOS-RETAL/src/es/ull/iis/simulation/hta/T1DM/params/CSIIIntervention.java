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
public class CSIIIntervention extends T1DMMonitoringIntervention {
	public final static String NAME = "CSII";
	private final double annualCost;
	/**
	 * @param id
	 * @param shortName
	 * @param description
	 */
	public CSIIIntervention(int id, double annualCost) {
		super(id, NAME, NAME);
		this.annualCost = annualCost;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getHBA1cLevel(T1DMPatient pat) {
		return pat.getBaselineHBA1c();
//		return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge()); // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3131116/
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getAnnualCost(T1DMPatient pat) {
		return annualCost;
	}

}
