/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionWrapper.SupportedType;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * TODO: Analize {@link OSDiWrapper.ObjectProperty.USES_VALUE_FROM}  property to detect and parse expressions that depend on other parameters
 * @author Iván Castilla Rodríguez
 *
 */
public class ValuableWrapper {
	protected final OSDiWrapper wrap;
	protected final String paramId;
	private final String source;
	private final Set<OSDiWrapper.DataItemType> dataItemTypes;
	private final ExpressionWrapper expression;
	private final RandomVariate probabilisticValue;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ValuableWrapper(OSDiWrapper wrap, String paramId, Set<ExpressionWrapper.SupportedType> supportedTypes) throws MalformedOSDiModelException {
		this.wrap = wrap;
		this.paramId = paramId;
		source = parseHasSourceProperty(paramId);
		final ArrayList<String> detValues = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValues(paramId);
		if (detValues.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression for valuable not defined.");
		if (detValues.size() > 1)
			wrap.printWarning(paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "More than one expression found for parameter. Using " + detValues.get(0) + " by default");

		final Set<String> referencedInstances = OSDiWrapper.ObjectProperty.USES_VALUE_FROM.getValues(paramId, true);
		if (referencedInstances.size() > 0 && !supportedTypes.contains(ExpressionWrapper.SupportedType.EXPRESSION_LANGUAGE))
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, OSDiWrapper.ObjectProperty.USES_VALUE_FROM, "The valuable declares that uses other valuables in its expression. However, either a constant or probability was expected");
			
		expression = new ExpressionWrapper(detValues.get(0)); 
		if (!supportedTypes.contains(expression.getType()))
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression type for valuable not supported: " + expression.getType());

		dataItemTypes = EnumSet.noneOf(OSDiWrapper.DataItemType.class); 
		Set<String> types = OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE.getValues(paramId);
		// Type assumed to be undefined if not specified
		if (types.size() == 0) {
			wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type not specified for parameter. Assigning UNDEFINED");
			dataItemTypes.add(OSDiWrapper.DataItemType.DI_UNDEFINED);
		}
		else {
			for (String type : types)
				dataItemTypes.add(OSDiWrapper.getDataItemType(type));			
		}
		probabilisticValue = initProbabilisticValue();
	}
	
	/**
	 * @return the paramId
	 */
	public String getParamId() {
		return paramId;
	}

	/**
	 * @return the dataItemType
	 */
	public Set<OSDiWrapper.DataItemType> getDataItemTypes() {
		return dataItemTypes;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the expression
	 */
	public ExpressionWrapper getExpression() {
		return expression;
	}

	/**
	 * Processes and returns the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @return the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 */
	public String parseHasSourceProperty(String individualIRI) {
		return OSDiWrapper.DataProperty.HAS_SOURCE.getValue(individualIRI, "Unknown");
	}
	
	private static RandomVariate getRandomVariateFromAvgAndCIs(double avg, double[] ci) {
		final double sd = Statistics.sdFrom95CI(ci);
		final double []paramsBeta = Statistics.betaParametersFromNormal(avg, sd);
		return RandomVariateFactory.getInstance("BetaVariate", paramsBeta[0], paramsBeta[1]);
	}

	public RandomVariate getProbabilisticValue()  {
		return probabilisticValue;
	}
	
	/**
	 * Returns a default probabilistic value to be used for this wrapper in case it is not defined 
	 * @return a default probabilistic value to be used for this wrapper in case it is not defined
	 */
	public RandomVariate getDefaultProbabilisticValue() {
		return RandomVariateFactory.getInstance("ConstantVariate", getDeterministicValue());
	}
	
	public double getDeterministicValue() {
		if (ExpressionWrapper.SupportedType.CONSTANT.equals(expression.getType()))
			return expression.getConstantValue();
		return Double.NaN;
	}
	
	protected RandomVariate initProbabilisticValue() throws MalformedOSDiModelException {
		RandomVariate stochasticUncertainty = initProbabilisticValue(OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY);
		RandomVariate heterogeneity = initProbabilisticValue(OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY);
		if (stochasticUncertainty != null) {
			if (heterogeneity != null) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Parameter defined both stochastic uncertainty and heterogeneity. Using only stochastic uncertainty.");
			}
			return stochasticUncertainty;
		}
		if (heterogeneity == null)
			return getDefaultProbabilisticValue();
		return heterogeneity;

	}
	
	protected RandomVariate initProbabilisticValue(OSDiWrapper.ObjectProperty uncertaintyProperty) throws MalformedOSDiModelException {
		// Takes the uncertainty that characterizes this parameter, but only if it's included in the working model 
		final Set<String> uncertaintyParams = uncertaintyProperty.getValues(paramId, true);
		if (uncertaintyParams.size() == 1) {
			// Currently only probabilities allowed
			final ValuableWrapper paramWrap = new ValuableWrapper(wrap, (String)uncertaintyParams.toArray()[0], EnumSet.of(SupportedType.PROBABILITY_DISTRIBUTION));
			return paramWrap.getExpression().getRnd();
		}
		if (uncertaintyParams.size() == 2) {
			final ValuableWrapper paramWrap1 = new ValuableWrapper(wrap, (String)uncertaintyParams.toArray()[0], EnumSet.of(SupportedType.CONSTANT)); 
			final ValuableWrapper paramWrap2 = new ValuableWrapper(wrap, (String)uncertaintyParams.toArray()[1], EnumSet.of(SupportedType.CONSTANT)); 
			if (paramWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER95CONFIDENCELIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER95CONFIDENCELIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap1.getDeterministicValue(), paramWrap2.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramWrap2.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiWrapper.DataItemType.DI_UPPER95CONFIDENCELIMIT);
				}				
			}
			else if (paramWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER95CONFIDENCELIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER95CONFIDENCELIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap2.getDeterministicValue(), paramWrap1.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramWrap2.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiWrapper.DataItemType.DI_LOWER95CONFIDENCELIMIT);
				}				
			}
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "Unsupported combination of valuables (" + paramWrap1.getParamId() + ", " + paramWrap2.getParamId() + ") to define the uncertainty");
		}
		
		if (uncertaintyParams.size() > 2)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "More than two uncertainty characterization for a parameter not supported. Currently " + uncertaintyParams.size());
		return null;
	}
	
}
