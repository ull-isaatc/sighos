/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.DefinesSensitivityAndSpecificity;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author masbe
 *
 */
public class DiagnosisStrategy extends Strategy implements DefinesSensitivityAndSpecificity {

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public DiagnosisStrategy(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param cond
	 */
	public DiagnosisStrategy(SecondOrderParamsRepository secParams, String name, String description,
			Condition<Patient> cond) {
		super(secParams, name, description, cond);
	}

	public double getSensitivity(Patient pat) {
		double sens = DefinesSensitivityAndSpecificity.super.getSensitivity(pat);
		if (!Double.isNaN(sens))
			return sens;
		// If just one level
		if (getParts().size() == 1) {
			// If just one child
			if (getParts().get(0).size() == 1) {
				return ((DefinesSensitivityAndSpecificity) getParts().get(0).get(0)).getSensitivity(pat);
			}
			else {
				// TODO
			}
		}
		else {
			// TODO
		}
		return 0.0;
	}

	public double getSpecificity(Patient pat) {
		double sens = DefinesSensitivityAndSpecificity.super.getSpecificity(pat);
		if (!Double.isNaN(sens))
			return sens;
		// If just one level
		if (getParts().size() == 1) {
			// If just one child
			if (getParts().get(0).size() == 1) {
				return ((DefinesSensitivityAndSpecificity) getParts().get(0).get(0)).getSpecificity(pat);
			}
			else {
				// TODO
			}
		}
		else {
			// TODO
		}
		return 0.0;
	}
}
