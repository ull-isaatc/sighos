/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import simkit.random.RandomVariate;

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
	public ParameterWrapper(OSDiWrapper wrap, String paramId, double defaultDetValue, String defaultDescription) throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDetValue);
		description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(paramId, defaultDescription);
		year = wrap.parseHasYearProperty(paramId);
		
		if (!wrap.addParameterWrapper(paramId, this))
			// TODO: Define the error better
			throw new MalformedOSDiModelException("");
	}
	
	@Override
	public RandomVariate processUncertainty(OSDiWrapper wrap) throws MalformedOSDiModelException {
		String paramId = getParamId();
		RandomVariate paramUncertainty = UncertaintyProcessor.process(wrap, this, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY);
		RandomVariate stochasticUncertainty = UncertaintyProcessor.process(wrap, this, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY);
		RandomVariate heterogeneity = UncertaintyProcessor.process(wrap, this, OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY);
		if (stochasticUncertainty != null || heterogeneity != null) {
			// FIXME: They should be supported 
			wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Stochastic uncertainty or heterogeneity not currently supported in Parameters.");
		}
		return paramUncertainty;
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
