/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ParameterWrapper extends ValuableWrapper {
	private final String description;
	private final int year;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ParameterWrapper(OSDiWrapper wrap, String paramId, double defaultDetValue) throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDetValue);
		description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(paramId, "");
		year = wrap.parseHasYearProperty(paramId);
		
		// Takes the uncertainty that characterizes this parameter, but only if it's included in the working model 
		final Set<String> paramUncertainty = OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY.getValues(paramId, true);
		if (paramUncertainty.size() > 0) {
			if (getProbabilisticValue() != null) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY, "Parameter defined both parameter uncertainty and either heterogeneity or stochastic uncertainty. Using only parameter uncertainty");				
			}
			if (paramUncertainty.size() > 1) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY, "Found more than one parameter uncertainty characterization for a parameter. Using " + paramUncertainty.toArray()[0]);
			}
			setProbabilisticValue(parseExpressionPropertyAsProbabilityDistribution((String)paramUncertainty.toArray()[0]));
		}
	}

	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

}
