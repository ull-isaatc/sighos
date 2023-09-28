/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

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
	private final String source;
	private double deterministicValue;
	private RandomVariate probabilisticValue;
	private final OSDiWrapper.DataItemType dataItemType;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ValuableWrapper(OSDiWrapper wrap, String paramId, double defaultDetValue) throws MalformedOSDiModelException {
		this.wrap = wrap;
		source = parseHasSourceProperty(paramId);
		deterministicValue = parseExpressionPropertyAsValue(paramId, defaultDetValue);
		// Takes the stochastic uncertainty that characterizes this parameter, but only if it's included in the working model 
		final Set<String> stochasticUncertainty = OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY.getValues(wrap, paramId, true);
		final Set<String> heterogeneity = OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY.getValues(wrap, paramId, true);
		if (stochasticUncertainty.size() > 0) {
			if (stochasticUncertainty.size() > 1) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Found more than one stochastic uncertainty characterization for a parameter. Using " + stochasticUncertainty.toArray()[0]);
			}
			probabilisticValue = parseExpressionPropertyAsProbabilityDistribution((String)stochasticUncertainty.toArray()[0]);
			if (heterogeneity.size() > 0) {
				wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY, "Parameter defined both stochastic uncertainty and heterogeneity. Using only stochastic uncertainty.");
			}
		}
		else {
			if (heterogeneity.size() > 0) {
				if (heterogeneity.size() > 1) {
					wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_HETEROGENEITY, "Found more than one heterogeneity characterization for a parameter. Using " + heterogeneity.toArray()[0]);
				}
				probabilisticValue = parseExpressionPropertyAsProbabilityDistribution((String)heterogeneity.toArray()[0]);
			}
			else {
				probabilisticValue = null;
			}
		}

		Set<String> types = OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE.getValues(wrap, paramId);
		// Type assumed to be utility if not specified
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
	 * @param deterministicValue the deterministicValue to set
	 */
	public void setDeterministicValue(double deterministicValue) {
		this.deterministicValue = deterministicValue;
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
		if (OSDiWrapper.DataProperty.HAS_EXPRESSION.getValues(wrap, individualIRI).size() == 0) {
			wrap.printWarning(individualIRI, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Expression for parameter not defined. Using " + defaultValue + " instead");
			return defaultValue;
		}
		final String strValue = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue(wrap, individualIRI, "" + defaultValue);
		try {
			return Double.parseDouble(strValue);
		} catch(NumberFormatException ex) {
			wrap.printWarning(individualIRI, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Wrong expression format. Found " + strValue + ". Using " + defaultValue + " instead");
			return defaultValue;
		}
	}
	
	public RandomVariate parseExpressionPropertyAsProbabilityDistribution(String individualIRI) throws MalformedOSDiModelException {
		final String strValue = OSDiWrapper.DataProperty.HAS_EXPRESSION.getValue(wrap, individualIRI, "");
		if (strValue == "")
			return null;
		return ProbabilityDistribution.getInstanceFromExpression(strValue);		
	}
	
	/**
	 * Processes and returns the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @return the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 */
	public String parseHasSourceProperty(String individualIRI) {
		return OSDiWrapper.DataProperty.HAS_SOURCE.getValue(wrap, individualIRI, "Unknown");
	}
	
}
