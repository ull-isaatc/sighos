package es.ull.iis.simulation.hta.osdi.ontology;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;

/**
 * A wrapper for the OSDi ontology that allows to modify it by adding new instances and properties
 * @author Iván Castilla Rodríguez
 */
public class ModifiableOSDiWrapper extends OSDiWrapper {
    /**
     * Creates a new wrapper for the OSDi ontology that can create new instances and properties
     * @param file The file containing the ontology
     * @param workingModelName The name of the working model instance
     * @param instancePrefix The prefix for the IRIs of the new instances
     * @throws OWLOntologyCreationException If the ontology cannot be loaded
     */
    public ModifiableOSDiWrapper(File file, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
        super(file, workingModelName, instancePrefix);
    }
    
    /**
     * Creates a new wrapper for the OSDi ontology that can create new instances and properties
     * @param path The path to the file containing the ontology
     * @param workingModelName The name of the working model instance
     * @param instancePrefix The prefix for the IRIs of the new instances
     * @throws OWLOntologyCreationException If the ontology cannot be loaded
     */
    public ModifiableOSDiWrapper(String path, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
        super(path, workingModelName, instancePrefix);
    }


	/**
	 * Common part of creating a parameter, independently of whether it is created by defining an expected value or an expression
	 * @param paramIRI Name of the parameter in the ontology
	 * @param clazz Specific class of the parameter in the ontology
	 * @param description Description of the parameter
	 * @param source Source of the value or expression
	 * @param year Year of the parameter
	 * @param dataType Data type of the parameter
	 */
	private void createCommonPartOfParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType) {
		clazz.add(paramIRI);
		OSDiObjectProperties.HAS_DATA_ITEM_TYPE.add(paramIRI, dataType.getInstanceName());
		OSDiDataProperties.HAS_SOURCE.add(paramIRI, source);
		OSDiDataProperties.HAS_DESCRIPTION.add(paramIRI, description);
		OSDiDataProperties.HAS_YEAR.add(paramIRI, "" + year);
		includeInModel(paramIRI);
	}

    /**
     * Creates a deterministic parameter with an expected value
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param value The expected value of the parameter
     */
	public void createParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value) {
		createCommonPartOfParameter(paramIRI, clazz, description, source, year, dataType);
		ParameterUncertaintyType.DETERMINISTIC.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPECTED_VALUE.add(paramIRI, Double.toString(value));
	}

    /**
     * Creates a first-order parameter characterized by an expression. If the expression IRI is null, the parameter is still created, but its values should 
     * be initialized later.
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param expressionIRI The IRI of the expression that characterizes the parameter (or null if the values will be defined later)
     */
	public void createParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, String expressionIRI) {
		createCommonPartOfParameter(paramIRI, clazz, description, source, year, dataType);
		ParameterUncertaintyType.FIRST_ORDER.getClazz().add(paramIRI);
		if (expressionIRI != null)
			OSDiObjectProperties.HAS_EXPRESSION.add(paramIRI, expressionIRI);
	}
	
    /**
     * Creates a second-order parameter characterized by an expected value and an expression. If the expression IRI is null, the parameter is still created, but its values should 
     * be initialized later.
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param value The expected value of the parameter
     * @param expressionIRI The IRI of the expression that characterizes the parameter (or null if the values will be defined later)
     */
	public void createParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value, String expressionIRI) {
		createCommonPartOfParameter(paramIRI, clazz, description, source, year, dataType);
		ParameterUncertaintyType.SECOND_ORDER.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPECTED_VALUE.add(paramIRI, Double.toString(value));
		if (expressionIRI != null)
			OSDiObjectProperties.HAS_EXPRESSION.add(paramIRI, expressionIRI);
	}

	/**
	 * Creates a second-order parameter characterized as an average and confidence interval values
	 * @param paramIRI The IRI of the parameter
	 * @param clazz The corresponding ontology class for the parameter
	 * @param description The description of the parameter
	 * @param source The source of the parameter
	 * @param year The year of the parameter
	 * @param dataType The data type of the parameter
	 * @param values An array of three values: the average, the lower 95% confidence interval and the upper 95% confidence interval
	 */
	public void createParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double[] values) {
		createParameter(paramIRI, clazz, description, source, year, dataType, values[0], null);
		addCIParameters(paramIRI, clazz, description, source, new double[] {values[1], values[2]}, year);
	}

    /**
     * Creates and adds a deterministic parameter to the ontology, and relates it to an instance by using a property
     * @param instanceIRI The IRI of the instance to which the parameter is added
     * @param property The property that relates the instance to the parameter
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param value The expected value of the parameter
     */
	public void addParameter(String instanceIRI, OSDiObjectProperties property, String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value) {
		createParameter(paramIRI, clazz, description, source, year, dataType, value);
		property.add(instanceIRI, paramIRI);
	}

    /**
     * Creates and adds a first-order parameter to the ontology, and relates it to an instance by using a property. If the expression IRI is null, the parameter is still created, 
     * but its values should be initialized later.
     * @param instanceIRI The IRI of the instance to which the parameter is added
     * @param property The property that relates the instance to the parameter
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param expressionIRI The IRI of the expression that characterizes the parameter (or null if the values will be defined later)
     */
	public void addParameter(String instanceIRI, OSDiObjectProperties property, String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, String expressionIRI) {
		createParameter(paramIRI, clazz, description, source, year, dataType, expressionIRI);
		property.add(instanceIRI, paramIRI);
	}

    /**
     * Creates and adds a second-order parameter to the ontology, and relates it to an instance by using a property. If the expression IRI is null, the parameter is still created,
     * but its values should be initialized later.
     * @param instanceIRI The IRI of the instance to which the parameter is added
     * @param property The property that relates the instance to the parameter
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param value The expected value of the parameter
     * @param expressionIRI The IRI of the expression that characterizes the parameter (or null if the values will be defined later)
     */
	public void addParameter(String instanceIRI, OSDiObjectProperties property, String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value, String expressionIRI) {
		createParameter(paramIRI, clazz, description, source, year, dataType, value, expressionIRI);
		property.add(instanceIRI, paramIRI);
	}

	/**
	 * Adds an utility parameter to the ontology characterized as an average and confidence interval values
	 * @param instanceIRI The IRI of the instance to which the parameter is added
	 * @param property The property that relates the instance to the parameter
	 * @param paramIRI The IRI of the parameter
	 * @param clazz The corresponding ontology class for the parameter
	 * @param description The description of the parameter
	 * @param source The source of the parameter
	 * @param year The year of the parameter
	 * @param dataType The data type of the parameter
	 * @param values An array of three values: the average, the lower 95% confidence interval and the upper 95% confidence interval
	 */
	public void addParameter(String instanceIRI, OSDiObjectProperties property, String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double[] values) {
		createParameter(paramIRI, clazz, description, source, year, dataType, values);
		property.add(instanceIRI, paramIRI);
	}

	public void addAttributeValue(String instanceIRI, OSDiObjectProperties property, String attributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, double value) {
		addParameter(instanceIRI, property, attributeValueIRI, clazz, "Value of " + attributeIRI, source, year, dataType, value);
		OSDiObjectProperties.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
		OSDiObjectProperties.HAS_VALUE.add(attributeIRI, attributeValueIRI);
	}

	public void addAttributeValue(String instanceIRI, OSDiObjectProperties property, String attributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, String expressionIRI) {
		addParameter(instanceIRI, property, attributeValueIRI, clazz, "Value of " + attributeIRI, source, year, dataType, expressionIRI);
		OSDiObjectProperties.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
		OSDiObjectProperties.HAS_VALUE.add(attributeIRI, attributeValueIRI);
	}

	public void addAttributeValue(String instanceIRI, OSDiObjectProperties property, String attributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, double value, String expressionIRI) {
		addParameter(instanceIRI, property, attributeValueIRI, clazz, "Value of " + attributeIRI, source, year, dataType, value, expressionIRI);
		OSDiObjectProperties.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
		OSDiObjectProperties.HAS_VALUE.add(attributeIRI, attributeValueIRI);
	}
	
	private void createCommonPartOfModification(String modificationIRI, String interventionIRI, String modifiedInstanceIRI) {
		OSDiObjectProperties.MODIFIES.add(modificationIRI, modifiedInstanceIRI);
		OSDiObjectProperties.IS_MODIFIED_BY.add(modifiedInstanceIRI, modificationIRI);
	}
	
	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, double value) {
		addAttributeValue(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, attributeIRI, source, year, dataType, value);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedAttributeValueIRI);
	}

	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, String expressionIRI) {
		addAttributeValue(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, attributeIRI, source, year, dataType, expressionIRI);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedAttributeValueIRI);
	}
	
	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType, double value, String expressionIRI) {
		addAttributeValue(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, attributeIRI, source, year, dataType, value, expressionIRI);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedAttributeValueIRI);
	}
	
	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value) {
		addParameter(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, dataType, value);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedParameterIRI);
	}

	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, String expressionIRI) {
		addParameter(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, dataType, expressionIRI);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedParameterIRI);
	}
	
	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType, double value, String expressionIRI) {
		addParameter(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, dataType, value, expressionIRI);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedParameterIRI);
	}

	public void addCost(String instanceIRI, OSDiObjectProperties property, String paramIRI, String description, String source, int year, boolean appliesOneTime, OSDiDataItemTypes currency, double value) {
		addParameter(instanceIRI, property, paramIRI, OSDiClasses.COST, description, source, year, currency, value);
		OSDiDataProperties.APPLIES_ONE_TIME.add(paramIRI, appliesOneTime ? "true" : "false");
	}
	
	public void addCost(String instanceIRI, OSDiObjectProperties property, String paramIRI, String description, String source, int year, boolean appliesOneTime, OSDiDataItemTypes currency, String expressionIRI) {
		addParameter(instanceIRI, property, paramIRI, OSDiClasses.COST, description, source, year, currency, expressionIRI);
		OSDiDataProperties.APPLIES_ONE_TIME.add(paramIRI, appliesOneTime ? "true" : "false");
	}
	
	public void addCost(String instanceIRI, OSDiObjectProperties property, String paramIRI, String description, String source, int year, boolean appliesOneTime, OSDiDataItemTypes currency, double value, String expressionIRI) {
		addParameter(instanceIRI, property, paramIRI, OSDiClasses.COST, description, source, year, currency, value, expressionIRI);
		OSDiDataProperties.APPLIES_ONE_TIME.add(paramIRI, appliesOneTime ? "true" : "false");
	}

	public void addUtility(String instanceIRI, OSDiObjectProperties property, String utilityParamIRI, String description, String source, int year, boolean appliesOneTime, boolean isDisutility, double value) {
		addParameter(instanceIRI, property, utilityParamIRI, OSDiClasses.UTILITY, description, source, year, isDisutility ? OSDiDataItemTypes.DI_DISUTILITY : OSDiDataItemTypes.DI_UTILITY, value);
		OSDiDataProperties.APPLIES_ONE_TIME.add(utilityParamIRI, appliesOneTime ? "true" : "false");
	}

	public void addUtility(String instanceIRI, OSDiObjectProperties property, String utilityParamIRI, String description, String source, int year, boolean appliesOneTime, boolean isDisutility, String expressionIRI) {
		addParameter(instanceIRI, property, utilityParamIRI, OSDiClasses.UTILITY, description, source, year, isDisutility ? OSDiDataItemTypes.DI_DISUTILITY : OSDiDataItemTypes.DI_UTILITY, expressionIRI);
		OSDiDataProperties.APPLIES_ONE_TIME.add(utilityParamIRI, appliesOneTime ? "true" : "false");
	}

	public void addUtility(String instanceIRI, OSDiObjectProperties property, String utilityParamIRI, String description, String source, int year, boolean appliesOneTime, boolean isDisutility, double value, String expressionIRI) {
		addParameter(instanceIRI, property, utilityParamIRI, OSDiClasses.UTILITY, description, source, year, isDisutility ? OSDiDataItemTypes.DI_DISUTILITY : OSDiDataItemTypes.DI_UTILITY, value, expressionIRI);
		OSDiDataProperties.APPLIES_ONE_TIME.add(utilityParamIRI, appliesOneTime ? "true" : "false");
	}

	/**
	 * Adds an utility parameter to the ontology characterized as an average and confidence interval values
	 * @param instanceIRI The IRI of the instance to which the parameter is added
	 * @param property The property that relates the instance to the parameter
	 * @param utilityParamIRI The IRI of the parameter
	 * @param description The description of the parameter
	 * @param source The source of the parameter
	 * @param year The year of the parameter
	 * @param tmpBehavior The temporal behavior of the utility
	 * @param utilityType The type of utility (utility or disutility)
	 * @param values An array of three values: the average, the lower 95% confidence interval and the upper 95% confidence interval
	 */
	public void addUtility(String instanceIRI, OSDiObjectProperties property, String utilityParamIRI, String description, String source, int year, boolean appliesOneTime, boolean isDisutility, double[] values) {
		addUtility(instanceIRI, property, utilityParamIRI, description, source, year, appliesOneTime, isDisutility, values[0], null);
		final String[] params = addCIParameters(utilityParamIRI, OSDiClasses.UTILITY, description, source, new double[] {values[1], values[2]}, year);
		OSDiDataProperties.APPLIES_ONE_TIME.add(params[0], appliesOneTime ? "true" : "false");
		OSDiDataProperties.APPLIES_ONE_TIME.add(params[1], appliesOneTime ? "true" : "false");
	}
	
	/**
	 * Adds two parameters that represent the lower and upper 95% confidence intervals for a parameter
	 * @param mainParamIRI The IRI of the parameter to which the confidence intervals are added
	 * @param clazz The class of the parameter
	 * @param description The description of the parameter
	 * @param source The source of the parameter
	 * @param values An array of two values: the lower 95% confidence interval and the upper 95% confidence interval
	 * @param year The year of the parameter
	 * @return An array of two strings with the IRIs of the parameters representing the lower and upper 95% confidence intervals
	 */
	public String[] addCIParameters(String mainParamIRI, OSDiClasses clazz, String description, String source, double[] values, int year) {
		String []params = new String[2];
		String paramUncertaintyIRI = InstanceIRI.UNCERTAINTY_L95CI.getIRI(mainParamIRI, false);
		addParameter(mainParamIRI, OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION, paramUncertaintyIRI, clazz, "Lower 95% confidence interval for " + description, 
				source, year, OSDiDataItemTypes.DI_LOWER_95_CONFIDENCE_LIMIT, values[0]);
		params[0] = paramUncertaintyIRI;
		paramUncertaintyIRI = InstanceIRI.UNCERTAINTY_U95CI.getIRI(mainParamIRI, false);
		addParameter(mainParamIRI, OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION, paramUncertaintyIRI, clazz, "Upper 95% confidence interval for " + description, 
				source, year, OSDiDataItemTypes.DI_UPPER_95_CONFIDENCE_LIMIT, values[1]);
		params[1] = paramUncertaintyIRI;
		return params;
	}

	public void createAdHocExpression(String expressionIRI, String expression, Set<String> dependentAttributes, Set<String> dependentParameters) {
		// final String expInstanceName = InstanceIRI.EXPRESSION.getIRI(paramIRI, false); 
		OSDiClasses.AD_HOC_EXPRESSION.add(expressionIRI);
		OSDiDataProperties.HAS_EXPRESSION_VALUE.add(expressionIRI, expression);
		for (String attributeName : dependentAttributes) {
			OSDiObjectProperties.DEPENDS_ON_ATTRIBUTE.add(expressionIRI, InstanceIRI.ATTRIBUTE.getIRI(attributeName, false));
		}
		for (String parameterName : dependentParameters) {
			OSDiObjectProperties.DEPENDS_ON_PARAMETER.add(expressionIRI, InstanceIRI.PARAMETER.getIRI(parameterName));
		}
		includeInModel(expressionIRI);			
	}

	public void createProbabilityDistributionExpression(String expressionIRI, OSDiProbabilityDistributionExpressions type, double[] parameters) {
		if (parameters.length != type.getnParameters())
			throw new IllegalArgumentException("Creating a " + type.name() + " probability distribution requires " + type.getnParameters() + " parameters. Passed " + parameters.length);
		type.getClazz().add(expressionIRI);
		switch(type) {
		case BERNOULLI:
			OSDiDataProperties.HAS_PROBABILITY_PARAMETER.add(expressionIRI, "" + parameters[0]);		
			break;			
		case BETA:
			OSDiDataProperties.HAS_ALFA_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiDataProperties.HAS_BETA_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case POISSON:
		case EXPONENTIAL:
			OSDiDataProperties.HAS_LAMBDA_PARAMETER.add(expressionIRI, "" + parameters[0]);		
			break;
		case GAMMA:
			OSDiDataProperties.HAS_ALFA_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiDataProperties.HAS_LAMBDA_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case UNIFORM:
			OSDiDataProperties.HAS_LOWER_LIMIT_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiDataProperties.HAS_UPPER_LIMIT_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case NORMAL:
		default:
			OSDiDataProperties.HAS_AVERAGE_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiDataProperties.HAS_STANDARD_DEVIATION_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;		
		}
		includeInModel(expressionIRI);			
	}

	/**
	 * Creates a model by using the working model instance specified 
	 * @param modelName
	 * @param type
	 * @param author
	 * @param description
	 * @param geoContext
	 * @param year
	 * @param reference
	 */
	public void createWorkingModel(ModelType type, String author, String description, String geoContext, int year, String reference, DisutilityCombinationMethod combinationMethod) {
        final String workingModelInstance = getWorkingModelInstance();
		type.getClazz().add(workingModelInstance);
		OSDiDataProperties.HAS_AUTHOR.add(workingModelInstance, author);
		OSDiDataProperties.HAS_DESCRIPTION.add(workingModelInstance, description);
		OSDiDataProperties.HAS_GEOGRAPHICAL_CONTEXT.add(workingModelInstance, geoContext);
		OSDiDataProperties.HAS_REF_TO.add(workingModelInstance, reference);
		OSDiDataProperties.HAS_YEAR.add(workingModelInstance, "" + year);		
		OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD.add(workingModelInstance, combinationMethod.name());
	}

	public void createDisease(String instanceName, String description, String refToDO, String refToICD, String refToOMIM, String refToSNOMED) {
		OSDiClasses.DISEASE.add(instanceName);		
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		OSDiDataProperties.HAS_REF_TO_DO.add(instanceName,  refToDO);
		OSDiDataProperties.HAS_REF_TO_ICD.add(instanceName,  refToICD);
		OSDiDataProperties.HAS_REF_TO_OMIM.add(instanceName,  refToOMIM);
		OSDiDataProperties.HAS_REF_TO_SNOMED.add(instanceName,  refToSNOMED);
		includeInModel(instanceName);
	}
	
	public void createManifestation(String instanceName, DiseaseProgressionType type, String description, Set<String> exclusions, String diseaseInstanceName) {
		type.getClazz().add(instanceName);		
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		OSDiObjectProperties.HAS_MANIFESTATION.add(diseaseInstanceName, instanceName);
		includeInModel(instanceName);
		for (String excludedManif : exclusions) {
			OSDiObjectProperties.EXCLUDES_MANIFESTATION.add(instanceName, InstanceIRI.MANIFESTATION.getIRI(excludedManif));
		}
	}
	
	public void createGroupOfManifestations(String instanceName, Set<String> manifestationNames) {
		OSDiClasses.GROUP.add(instanceName);
		includeInModel(instanceName);
		for (String manifestation : manifestationNames) {
			OSDiObjectProperties.HAS_COMPONENT.add(instanceName, InstanceIRI.MANIFESTATION.getIRI(manifestation));			
			OSDiObjectProperties.BELONGS_TO_GROUP.add(InstanceIRI.MANIFESTATION.getIRI(manifestation), instanceName);			
		}
	}
	
	public void createPopulation(String instanceName, String description, int minAge, int maxAge, int size, int year) {
		OSDiClasses.POPULATION.add(instanceName);
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		OSDiDataProperties.HAS_MIN_AGE.add(instanceName, "" + minAge);
		OSDiDataProperties.HAS_MAX_AGE.add(instanceName, "" + maxAge);
		OSDiDataProperties.HAS_SIZE.add(instanceName, "" + size);
		OSDiDataProperties.HAS_YEAR.add(instanceName, "" + year);
		
		includeInModel(instanceName);
	}
	
	public void createIntervention(String instanceName, InterventionType type, String description) {
		type.getClazz().add(instanceName);
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		
		includeInModel(instanceName);
	}
	
	private void includeInModel(String instanceName) {
        final String workingModelInstance = getWorkingModelInstance();
		OSDiObjectProperties.INCLUDED_BY_MODEL.add(instanceName, workingModelInstance);
		OSDiObjectProperties.INCLUDES_MODEL_ITEM.add(workingModelInstance, instanceName);
	}
	

}
