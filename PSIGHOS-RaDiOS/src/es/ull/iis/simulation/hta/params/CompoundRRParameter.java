/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.Collection;


import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;

/**
 * A relative risk paramete that combines different relative risks
 * @author Iván Castilla
 *
 */
public class CompoundRRParameter extends Parameter {
	/** The method used to combine the relative risks */
	public enum DEF_COMBINATION implements CombinationMethod {
		ADD {
			@Override
			public double combine(HTAModel model, Collection<String> combinedParameterNames, Patient pat) {
				double finalRR = 1.0;
				for (String rrName : combinedParameterNames) {
					double rr = model.getParameterValue(rrName, pat);
					finalRR += rr - 1;
				}
				return finalRR;
			}			
		},
		MAX {
			@Override
			public double combine(HTAModel model, Collection<String> combinedParameterNames, Patient pat) {
				double finalRR = -1.0;
				for (String rrName : combinedParameterNames) {
					final double newRR = model.getParameterValue(rrName, pat);
					finalRR = (newRR > finalRR) ? newRR : finalRR;
				}
				return finalRR;
			}			
		}
		
	}
	/** The set of parameters to be combined */
	protected final Collection<String> combinedParameterNames;
	/** Combination method used to combine the relative risks */
	private final CombinationMethod combMethod;
    /** The model this component belongs to */
    protected final HTAModel model;

	/**
	 * Creates a relative risk parameter that combines several parameters
	 * @param combinedParameterNames The collection of parameters to be combined 
	 * @param combMethod Combination method used to combine the relative risks
	 */
	public CompoundRRParameter(HTAModel model, String paramName, String description, String source, int year, Collection<String> combinedParameterNames, CombinationMethod combMethod) {
		super(paramName, description, source, year, ParameterType.RISK);
		this.model = model;
		this.combinedParameterNames = combinedParameterNames;
		this.combMethod = combMethod;
	}

	@Override
	public double getValue(Patient pat) {
		return combMethod.combine(model, combinedParameterNames, pat);
	}

	/**
	 * A generic interface to implement other combination methods
	 * @author Iván Castilla Rodríguez
	 */
	public static interface CombinationMethod {
		double combine(HTAModel model, Collection<String> combinedParameterNames, Patient pat); 
	}
}
