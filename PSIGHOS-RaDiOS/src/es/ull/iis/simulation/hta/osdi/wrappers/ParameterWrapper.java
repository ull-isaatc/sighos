/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.EnumSet;
import java.util.Set;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper.ParameterNature;
import es.ull.iis.simulation.hta.params.ConstantNatureParameter;
import es.ull.iis.simulation.hta.params.FirstOrderNatureParameter;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderNatureParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ParameterWrapper implements ExpressableWrapper {
	protected final OSDiWrapper wrap;
	protected final String paramIRI;
	private final String source;
	private final String description;
	private final int year;
	private final Set<OSDiDataItemTypes> dataItemTypes;
	private final String expression;
	private final double deterministicValue;
	private final RandomVariate probabilisticValue;
	private final ParameterNature nature;

	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ParameterWrapper(OSDiWrapper wrap, String paramIRI) throws MalformedOSDiModelException {
		this(wrap, paramIRI, "");
	}
	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public ParameterWrapper(OSDiWrapper wrap, String paramIRI, String defaultDescription) throws MalformedOSDiModelException {
		this.wrap = wrap;
		this.paramIRI = paramIRI;
		source = parseHasSourceProperty(paramIRI);
		description = OSDiDataProperties.HAS_DESCRIPTION.getValue(paramIRI, defaultDescription);
		year = wrap.parseHasYearProperty(paramIRI);
			
		dataItemTypes = EnumSet.noneOf(OSDiDataItemTypes.class); 
		Set<String> types = OSDiObjectProperties.HAS_DATA_ITEM_TYPE.getValues(paramIRI);
		// Type assumed to be undefined if not specified
		if (types.size() == 0) {
			wrap.printWarning(paramIRI, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Data item type not specified for parameter. Assigning UNDEFINED");
			dataItemTypes.add(OSDiDataItemTypes.DI_UNDEFINED);
		}
		else {
			for (String type : types)
				dataItemTypes.add(OSDiWrapper.getDataItemType(type));			
		}
		
		if (wrap.isInstanceOf(paramIRI, OSDiClasses.DETERMINISTIC_PARAMETER.getShortName())) {
			final String detValue = OSDiDataProperties.HAS_EXPECTED_VALUE.getValue(paramIRI);
			if (detValue == null)
				throw new MalformedOSDiModelException("Deterministic parameter " + paramIRI + " requires a value for the " + OSDiDataProperties.HAS_EXPECTED_VALUE.getShortName() + " property");
			deterministicValue = Double.parseDouble(detValue);
			expression = null;
			probabilisticValue = null;
			nature = ParameterNature.DETERMINISTIC;
		}
		else if (wrap.isInstanceOf(paramIRI, OSDiClasses.FIRST_ORDER_UNCERTAINTY_PARAMETER.getShortName())) {
			final String strExpression = OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.getValue(paramIRI, true);
			if (strExpression == null) {
				throw new MalformedOSDiModelException("First order parameter " + paramIRI + " requires a value for the " + OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.getShortName() + " property");
			}			
			probabilisticValue = new ExpressionWrapper(wrap, strExpression).getRnd();
			deterministicValue = Double.NaN;
			expression = null;
			nature = ParameterNature.FIRST_ORDER;
		}
		else if (wrap.isInstanceOf(paramIRI, OSDiClasses.SECOND_ORDER_UNCERTAINTY_PARAMETER.getShortName())) {
			final String detValue = OSDiDataProperties.HAS_EXPECTED_VALUE.getValue(paramIRI);
			if (detValue == null)
				throw new MalformedOSDiModelException("Second order parameter " + paramIRI + " requires a value for the " + OSDiDataProperties.HAS_EXPECTED_VALUE.getShortName() + " property");
			deterministicValue = Double.parseDouble(detValue);
			expression = null;
			final Set<String> uncertaintyParams = OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.getValues(paramIRI, true);
			if (uncertaintyParams.size() == 0)
				throw new MalformedOSDiModelException("Second order parameter " + paramIRI + " requires a value for the " + OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.getShortName() + " property");
			else if (uncertaintyParams.size() == 1) {
				if (wrap.getClassesForIndividual((String)uncertaintyParams.toArray()[0]).contains(OSDiClasses.PROBABILITY_DISTRIBUTION_EXPRESSION.getShortName())) {
					probabilisticValue = new ExpressionWrapper(wrap, (String)uncertaintyParams.toArray()[0]).getRnd();
				}
				else {
					probabilisticValue = initProbabilisticValue(uncertaintyParams);
				}
			}
			else {
				probabilisticValue = initProbabilisticValue(uncertaintyParams);	
			}
			nature = ParameterNature.SECOND_ORDER;
		}
		else if (wrap.isInstanceOf(paramIRI, OSDiClasses.CALCULATED_PARAMETER.getShortName())) {
			expression = OSDiDataProperties.HAS_EXPRESSION_VALUE.getValue(paramIRI, "");
			if (expression.equals("")) {			
				throw new MalformedOSDiModelException("Calculated parameter " + paramIRI + " requires a value for the " + OSDiDataProperties.HAS_EXPRESSION_VALUE.getShortName() + " property");
			}
			probabilisticValue = null;
			deterministicValue = Double.NaN;
			nature = ParameterNature.CALCULATED;
		}
		else {
			throw new MalformedOSDiModelException("Parameter " + paramIRI + " is not a valid parameter. It should be an instance of " + OSDiClasses.DETERMINISTIC_PARAMETER + ", " + OSDiClasses.FIRST_ORDER_UNCERTAINTY_PARAMETER + ", " + OSDiClasses.CALCULATED_PARAMETER + " or " + OSDiClasses.SECOND_ORDER_UNCERTAINTY_PARAMETER);
		}
	}
	
	/**
	 * Returns the nature of the parameter, i.e., deterministic, first or second order uncertainty.
	 * @return the nature of the parameter, i.e., deterministic, first or second order uncertainty.
	 */
	public ParameterNature getNature() {
		return nature;
	}

	/**
	 * @return the paramId
	 */
	@Override
	public String getOriginalIndividualIRI() {
		return paramIRI;
	}

	/**
	 * @return the dataItemType
	 */
	public Set<OSDiDataItemTypes> getDataItemTypes() {
		return dataItemTypes;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
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

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Processes and returns the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @return the hasSource data property of an individual. If the property is not defined, returns "Unknown"
	 */
	public String parseHasSourceProperty(String individualIRI) {
		return OSDiDataProperties.HAS_SOURCE.getValue(individualIRI, "Unknown");
	}
	
	/**
	 * Returns the random variate that characterizes the uncertainty of a parameter when it is defined by its average and confidence intervals
	 * @param avg The average value of the parameter
	 * @param ci The confidence intervals of the parameter
	 * @return the random variate that characterizes the uncertainty of a parameter when it is defined by its average and confidence intervals
	 */
	protected RandomVariate getRandomVariateFromAvgAndCIs(double avg, double[] ci) {
		if (dataItemTypes.contains(OSDiDataItemTypes.DI_RELATIVE_RISK)) {
			return RandomVariateFactory.getInstance("RRFromLnCIVariate", avg, ci[0], ci[1], 1);
		}
		final double sd = Statistics.sdFrom95CI(ci);
		final double []paramsBeta = Statistics.betaParametersFromNormal(avg, sd);
		return RandomVariateFactory.getInstance("BetaVariate", paramsBeta[0], paramsBeta[1]);
	}

	/**
	 * Returns the random variate that characterizes the uncertainty of a parameter when it is defined by its average and standard deviation
	 * @param avg The average value of the parameter
	 * @param sd The standard deviation of the parameter
	 * @return the random variate that characterizes the uncertainty of a parameter when it is defined by its average and standard deviation
	 */
	protected RandomVariate getRandomVariateFromAvgAndSD(double avg, double sd) {
		return RandomVariateFactory.getInstance("NormalVariate", avg, sd);
	}

	public RandomVariate getProbabilisticValue()  {
		return probabilisticValue;
	}
	
	public double getDeterministicValue() {
		return deterministicValue;
	}
	
	/**
	 * Creates a parameter from this wrapper
	 * @param model The model where the parameter will be created
	 * @param type The type of parameter to be created
	 * @return The created parameter
	 */
	public Parameter createParameter(HTAModel model, ParameterType type) {
		switch(nature) {
		case DETERMINISTIC:
			return new ConstantNatureParameter(model, paramIRI, description, source, year, type, deterministicValue);
		case FIRST_ORDER:
			return new FirstOrderNatureParameter(model, paramIRI, description, source, year, type, probabilisticValue);
		case SECOND_ORDER:
			return new SecondOrderNatureParameter(model, paramIRI, description, source, year, type, deterministicValue, probabilisticValue);
		case CALCULATED:
			return new ExpressionLanguageParameter(model, paramIRI, description, source, year, type, expression);
		default:
			return null;
		}
	}

	protected RandomVariate initProbabilisticValue(Set<String> uncertaintyParams) throws MalformedOSDiModelException {
		// Takes the uncertainty that characterizes this parameter, but only if it's included in the working model 
		if (uncertaintyParams.size() == 1) {
			final String uncertainParamId = (String)uncertaintyParams.toArray()[0];
			final ParameterWrapper paramWrap = new ParameterWrapper(wrap, uncertainParamId);
			// If the uncertainty is characterized by a standard deviation, then we use a normal distribution
			if (paramWrap.getDataItemTypes().contains(OSDiDataItemTypes.DI_STANDARD_DEVIATION)) {
				return getRandomVariateFromAvgAndSD(getDeterministicValue(), paramWrap.getDeterministicValue());
			}
			else {
				throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, uncertainParamId, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Data item type not supported for characterizing uncertainty.");
			}
		}
		if (uncertaintyParams.size() == 2) {
			final ParameterWrapper paramWrap1 = new ParameterWrapper(wrap, (String)uncertaintyParams.toArray()[0]); 
			final ParameterWrapper paramWrap2 = new ParameterWrapper(wrap, (String)uncertaintyParams.toArray()[1]); 
			if (paramWrap1.getDataItemTypes().contains(OSDiDataItemTypes.DI_LOWER_95_CONFIDENCE_LIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiDataItemTypes.DI_UPPER_95_CONFIDENCE_LIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap1.getDeterministicValue(), paramWrap2.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, paramWrap2.getOriginalIndividualIRI(), OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiDataItemTypes.DI_UPPER_95_CONFIDENCE_LIMIT);
				}				
			}
			else if (paramWrap1.getDataItemTypes().contains(OSDiDataItemTypes.DI_UPPER_95_CONFIDENCE_LIMIT)) {
				if (paramWrap2.getDataItemTypes().contains(OSDiDataItemTypes.DI_LOWER_95_CONFIDENCE_LIMIT)) {
					return getRandomVariateFromAvgAndCIs(getDeterministicValue(), new double[] {paramWrap2.getDeterministicValue(), paramWrap1.getDeterministicValue()});
				}
				else {
					throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, paramWrap2.getOriginalIndividualIRI(), OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Upper and lower confidence intervals required to represent uncertainty. This parameter should include data item type " + OSDiDataItemTypes.DI_LOWER_95_CONFIDENCE_LIMIT);
				}				
			}
			throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, paramIRI, OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION, "Unsupported combination of valuables (" + paramWrap1.getOriginalIndividualIRI() + ", " + paramWrap2.getOriginalIndividualIRI() + ") to define the uncertainty");
		}
		
		if (uncertaintyParams.size() > 2)
			throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, paramIRI, OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION, "More than two uncertainty characterization for a parameter not supported. Currently " + uncertaintyParams.size());
		return null;
	}
	
}
