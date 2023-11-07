/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.EnumSet;
import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
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
	private final double deterministicValue;
	private final RandomVariate probabilisticValue;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ValuableWrapper(OSDiWrapper wrap, String paramId) throws MalformedOSDiModelException {
		this.wrap = wrap;
		this.paramId = paramId;
		source = parseHasSourceProperty(paramId);
		
		final String detValue = OSDiWrapper.DataProperty.HAS_EXPECTED_VALUE.getValue(paramId);
		final String strExpression = OSDiWrapper.ObjectProperty.HAS_EXPRESSION.getValue(paramId, true);
		
		if (detValue == null && strExpression == null)
			throw new MalformedOSDiModelException("Neither a " + OSDiWrapper.ObjectProperty.HAS_EXPRESSION.getShortName() + " or a " + OSDiWrapper.DataProperty.HAS_EXPECTED_VALUE.getShortName() + " properties were defined for valueable " + paramId);
		if (detValue != null && strExpression != null)
			throw new MalformedOSDiModelException("The Valuable " + paramId + " defines a " + OSDiWrapper.ObjectProperty.HAS_EXPRESSION.getShortName() + " and a " + OSDiWrapper.DataProperty.HAS_EXPECTED_VALUE.getShortName() + " properties at the same time");
		if (detValue != null) {
			deterministicValue = Double.parseDouble(detValue);
			expression = null; 
		}
		else {
			deterministicValue = Double.NaN;
			expression = new ExpressionWrapper(wrap, strExpression); 
		}
			
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
		return deterministicValue;
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
	
	public ValuableWrapper getInstanceForUncertainty(String uncertainParamId) throws MalformedOSDiModelException {
		return new ValuableWrapper(wrap, uncertainParamId);
	}
	
	// FIXME: Currently this is inconsistent. Change hierarchy in ontology or check here whether uncertainty individuals are valuable or expressions.
	protected RandomVariate initProbabilisticValue(OSDiWrapper.ObjectProperty uncertaintyProperty) throws MalformedOSDiModelException {
		// Takes the uncertainty that characterizes this parameter, but only if it's included in the working model 
		final Set<String> uncertaintyParams = uncertaintyProperty.getValues(paramId, true);
		if (uncertaintyParams.size() == 1) {
			final String uncertainParamId = (String)uncertaintyParams.toArray()[0];
			final Set<String> clazzes = wrap.getClassesForIndividual(uncertainParamId);
			if (clazzes.contains(OSDiWrapper.Clazz.VALUABLE.getShortName())) {
				final ValuableWrapper paramWrap = getInstanceForUncertainty(uncertainParamId);
				// If the uncertainty is characterized by a standard deviation, then we use a normal distribution
				if (paramWrap.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_STANDARD_DEVIATION)) {
					return RandomVariateFactory.getInstance("NormalVariate", getDeterministicValue(), paramWrap.getDeterministicValue());
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, uncertainParamId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type not supported for characterizing uncertainty.");
				}
			}
			else if (clazzes.contains(OSDiWrapper.Clazz.PROBABILITY_DISTRIBUTION_EXPRESSION.getShortName())) {
				final ExpressionWrapper expWrap = new ExpressionWrapper(wrap, uncertainParamId);
				return expWrap.getRnd();
			}
			else {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "Class for the uncertainty characterization of individual " + uncertainParamId + " not supported. Currently " + (OSDiWrapper.Clazz)clazzes.toArray()[0]);
			}
		}
		if (uncertaintyParams.size() == 2) {
			final ValuableWrapper paramWrap1 = getInstanceForUncertainty((String)uncertaintyParams.toArray()[0]); 
			final ValuableWrapper paramWrap2 = getInstanceForUncertainty((String)uncertaintyParams.toArray()[1]); 
			if (paramWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap1.getDeterministicValue(), paramWrap2.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramWrap2.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT);
				}				
			}
			else if (paramWrap1.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap2.getDeterministicValue(), paramWrap1.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramWrap2.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT);
				}				
			}
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "Unsupported combination of valuables (" + paramWrap1.getParamId() + ", " + paramWrap2.getParamId() + ") to define the uncertainty");
		}
		
		if (uncertaintyParams.size() > 2)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, uncertaintyProperty, "More than two uncertainty characterization for a parameter not supported. Currently " + uncertaintyParams.size());
		return null;
	}
	
}
