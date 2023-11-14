/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * Class that associates a relative risk to an intervention. If the patient loses the effect of the intervention, uses the
 * RR for intervention 0 (assumed to be base effect)
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderInterventionSpecificRR implements RRCalculator {
	/** The relative risk values associated to each intervention */
	final private SecondOrderParam[] rr;
	/** An offset to adjust the index of the RR */
	final private int interventionOffset;

	/**
	 * Creates a relative risk that associates a value to each intervention. In case the length of the passed array is lower than the number of interventions, the first interventions are assumed 
	 * to have RR = 1.0 
	 * @param rr The second order parameter that characterizes the relative risk values associated to each intervention
	 */
	public SecondOrderInterventionSpecificRR(SecondOrderParamsRepository secParams, SecondOrderParam[] rr) {
		this.interventionOffset = secParams.getNInterventions() - rr.length;
		this.rr = rr;
	}
	
	@Override
	public double getRR(Patient pat) {
		if (pat.getnIntervention() - interventionOffset < 0)
			return 1.0;
		return rr[pat.getnIntervention() - interventionOffset].getValue(pat);
	}
}
