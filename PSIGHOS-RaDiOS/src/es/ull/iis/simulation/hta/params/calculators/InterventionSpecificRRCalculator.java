/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * Class that associates a relative risk to an intervention. If the patient loses the effect of the intervention, uses the
 * RR for intervention 0 (assumed to be base effect)
 * @author Iván Castilla Rodríguez
 *
 */
public class InterventionSpecificRRCalculator implements ParameterCalculator {
	/** The names of the parameters that characterize the relative risks associated to each intervention */
	final private ArrayList<String> paramRRNames;
	/** An offset to adjust the index of the RR */
	final private int interventionOffset;
	private final SecondOrderParamsRepository secParams;

	/**
	 * Creates a relative risk that associates a value to each intervention. In case the length of the passed array is lower than the number of interventions, the first interventions are assumed 
	 * to have RR = 1.0 
	 * @param paramRRNames The names of the parameters that characterizes the relative risk values associated to each intervention
	 */
	public InterventionSpecificRRCalculator(SecondOrderParamsRepository secParams, ArrayList<String> paramRRNames) {
		this.interventionOffset = secParams.getNInterventions() - paramRRNames.size();
		this.paramRRNames = paramRRNames;
		this.secParams = secParams;
	}
	
	@Override
	public double getValue(Patient pat) {
		if (pat.getnIntervention() - interventionOffset < 0)
			return 1.0;
		return secParams.getParameterValue(paramRRNames.get(pat.getnIntervention() - interventionOffset), pat);
	}
}
