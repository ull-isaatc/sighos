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

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ParameterWrapper(OSDiWrapper wrap, String paramId, String defaultDescription) throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDescription);
		
		if (!wrap.addParameterWrapper(paramId, this))
			throw new MalformedOSDiModelException("Parameter with the same name (" + paramId + ") already defined");
	}
	
	@Override
	protected RandomVariate initProbabilisticValue() throws MalformedOSDiModelException {
		RandomVariate paramUncertainty = initProbabilisticValue(OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY);
		RandomVariate stochasticUncertainty = initProbabilisticValue(OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY);
		RandomVariate heterogeneity = initProbabilisticValue(OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY);
		// FIXME: They should be supported 
		if (stochasticUncertainty != null)
			wrap.printWarning(paramIRI, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Stochastic uncertainty not currently supported in Parameters.");
		if (heterogeneity != null)
			wrap.printWarning(paramIRI, OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY, "Heterogeneity not currently supported in Parameters.");
		if (paramUncertainty == null)
			return getDefaultProbabilisticValue();
		return paramUncertainty;
	}

}
