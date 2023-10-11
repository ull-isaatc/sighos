/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.ArrayList;
import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import simkit.random.RandomVariate;

/**
 * TODO: Analize {@link OSDiWrapper.ObjectProperty.USES_VALUE_FROM}  property to detect and parse expressions that depend on other parameters
 * @author Iván Castilla Rodríguez
 *
 */
public class ValuableWrapper {
	private final OSDiWrapper wrap;
	private final String paramId;
	private final String source;
	private final double deterministicValue;
	private RandomVariate probabilisticValue;
	private final OSDiWrapper.DataItemType dataItemType;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ValuableWrapper(OSDiWrapper wrap, String paramId) throws MalformedOSDiModelException {
		this.wrap = wrap;
		this.paramId = paramId;
		source = parseHasSourceProperty(paramId);
		final ArrayList<String> detValues = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValues(paramId);
		if (detValues.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression for valuable not defined.");
		if (detValues.size() > 1)
			wrap.printWarning(paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "More than one expression found for parameter. Using " + detValues.get(0) + " by default");

		final ExpressionWrapper detExpression = new ExpressionWrapper(detValues.get(0)); 
		// TODO: Process expression language
		if (!ExpressionWrapper.SupportedType.CONSTANT.equals(detExpression.getType()))
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.VALUABLE, paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression for valuable is not a constant. Instead: " + detExpression.getType() + " found.");
		this.deterministicValue = detExpression.getConstantValue();
		this.probabilisticValue = processUncertainty(wrap);

		Set<String> types = OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE.getValues(paramId);
		// Type assumed to be undefined if not specified
		if (types.size() == 0) {
			wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type not specified for parameter. Assigning UNDEFINED");
			dataItemType = OSDiWrapper.DataItemType.DI_UNDEFINED;
		}
		else {
			if (types.size() > 1)
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "More than one data item type assigned to parameter. Using only " + types.toArray()[0]);
			dataItemType = OSDiWrapper.getDataItemType((String) types.toArray()[0]);
			
		}
	}

	public RandomVariate processUncertainty(OSDiWrapper wrap) throws MalformedOSDiModelException {
		RandomVariate stochasticUncertainty = UncertaintyProcessor.process(wrap, this, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY);
		RandomVariate heterogeneity = UncertaintyProcessor.process(wrap, this, OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY);
		if (stochasticUncertainty != null) {
			if (heterogeneity != null) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Parameter defined both stochastic uncertainty and heterogeneity. Using only stochastic uncertainty.");
			}
			return stochasticUncertainty;
		}
		return heterogeneity;
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
	public OSDiWrapper.DataItemType getDataItemType() {
		return dataItemType;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the deterministicValue
	 */
	public double getDeterministicValue() {
		return deterministicValue;
	}

	/**
	 * @return the probabilisticValue
	 */
	public RandomVariate getProbabilisticValue() {
		return probabilisticValue;
	}


	/**
	 * @param probabilisticValue the probabilisticValue to set
	 */
	public void setProbabilisticValue(RandomVariate probabilisticValue) {
		this.probabilisticValue = probabilisticValue;
	}

	/**
	 * Processes the hasExpression data property of an individual and returns a double representation of its value. If the property is not defined
	 * or its value has a wrong format, returns the specified default value  
	 * TODO: This method should be able of processing "expression language", mathML or similar sintaxis to specify calculations in the ontology instead of values
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @param defaultValue Default value to use in case the property is not defined or has wrong format
	 * @return a double representation of the hasExpression data property for an individual
	 */
	public double parseExpressionPropertyAsValue(String individualIRI, double defaultValue) {
		if (OSDiWrapper.DataProperty.HAS_EXPRESSION.getValues(individualIRI).size() == 0) {
			wrap.printWarning(individualIRI, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression for parameter not defined. Using " + defaultValue + " instead");
			return defaultValue;
		}
		final String strValue = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue(individualIRI, "" + defaultValue);
		try {
			return Double.parseDouble(strValue);
		} catch(NumberFormatException ex) {
			wrap.printWarning(individualIRI, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Wrong expression format. Found " + strValue + ". Using " + defaultValue + " instead");
			return defaultValue;
		}
	}
	
	/**
	 * Processes and returns the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @return the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 */
	public String parseHasSourceProperty(String individualIRI) {
		return OSDiWrapper.DataProperty.HAS_SOURCE.getValue(individualIRI, "Unknown");
	}
	
}
