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
	 * Adds a deterministic value to a parameter
	 * @param paramIRI The IRI of the parameter
	 * @param value The deterministic value
	 */
	public void addDeterministicNature(String paramIRI, double value) {
		ParameterNature.DETERMINISTIC.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPECTED_VALUE.add(paramIRI, Double.toString(value));
	}

	/**
	 * Adds a first order uncertainty characterization to a parameter
	 * @param paramIRI The IRI of the parameter
	 * @param expressionIRI The IRI of the expression that characterizes the first order uncertainty on the parameter
	 */
	public void addFirstOrderNature(String paramIRI, String expressionIRI) {
		ParameterNature.FIRST_ORDER.getClazz().add(paramIRI);
		OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.add(paramIRI, expressionIRI);
	}

	/**
	 * Adds a second order uncertainty characterization to a parameter
	 * @param paramIRI The IRI of the parameter
	 * @param value The expected value of the parameter
	 * @param expressionIRI The IRI of the expression that characterizes the second order uncertainty on the parameter
	 */
	public void addSecondOrderNature(String paramIRI, double value, String expressionIRI) {
		ParameterNature.SECOND_ORDER.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPECTED_VALUE.add(paramIRI, Double.toString(value));
		OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION.add(paramIRI, expressionIRI);
	}

	/**
	 * Adds a second order uncertainty characterization to a parameter based on an average and confidence interval values
	 * @param paramIRI The IRI of the parameter
	 * @param values An array of three values: the average, the lower 95% confidence interval and the upper 95% confidence interval
	 * @param clazz The corresponding ontology class for the parameter
	 * @param description The description of the parameter
	 * @param source The source of the parameter
	 * @param year The year of the parameter
	 */
	public void addSecondOrderNature(String paramIRI, double[] values, OSDiClasses clazz, String description, String source, int year) {
		ParameterNature.SECOND_ORDER.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPECTED_VALUE.add(paramIRI, Double.toString(values[0]));
		addCIParameters(paramIRI, clazz, description, source, new double[] {values[1], values[2]}, year);
	}

	/**
	 * Adds a second order uncertainty characterization to a parameter based on an average and confidence interval values
	 * @param paramIRI The IRI of the parameter
	 * @param values An array of three values: the average, the lower 95% confidence interval and the upper 95% confidence interval
	 * @param clazz The corresponding ontology class for the parameter
	 */
	public void addSecondOrderNature(String paramIRI, double[] values, OSDiClasses clazz) {
		addSecondOrderNature(paramIRI, values, clazz, OSDiDataProperties.HAS_DESCRIPTION.getValue(paramIRI), OSDiDataProperties.HAS_SOURCE.getValue(paramIRI), Integer.parseInt(OSDiDataProperties.HAS_YEAR.getValue(paramIRI)));
	}

	public void addCalculatedNature(String paramIRI, String expression, ExpressionLanguage expLanguage, Set<String> dependentAttributes, Set<String> dependentParameters) {
		ParameterNature.CALCULATED.getClazz().add(paramIRI);
		OSDiDataProperties.HAS_EXPRESSION_VALUE.add(paramIRI, expression);
		for (String attributeName : dependentAttributes) {
			OSDiObjectProperties.DEPENDS_ON_ATTRIBUTE.add(paramIRI, InstanceIRI.ATTRIBUTE.getIRI(attributeName, false));
		}
		for (String parameterName : dependentParameters) {
			OSDiObjectProperties.DEPENDS_ON_PARAMETER.add(paramIRI, InstanceIRI.PARAMETER.getIRI(parameterName));
		}
		OSDiObjectProperties.HAS_EXPRESSION_LANGUAGE.add(paramIRI, expLanguage.getInstanceName());
	}

    /**
     * Creates a parameter
     * @param paramIRI The IRI of the parameter
     * @param clazz The corresponding ontology class for the parameter
     * @param description The description of the parameter
     * @param source The source of the parameter
     * @param year The year of the parameter
     * @param dataType The data type of the parameter
     * @param value The expected value of the parameter
     */
	public void createParameter(String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType) {
		clazz.add(paramIRI);
		OSDiObjectProperties.HAS_DATA_ITEM_TYPE.add(paramIRI, dataType.getInstanceName());
		OSDiDataProperties.HAS_SOURCE.add(paramIRI, source);
		OSDiDataProperties.HAS_DESCRIPTION.add(paramIRI, description);
		OSDiDataProperties.HAS_YEAR.add(paramIRI, "" + year);
		includeInModel(paramIRI);
	}

    /**
     * Creates and adds a parameter to the ontology, and relates it to an instance by using a property
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
	public void addParameter(String instanceIRI, OSDiObjectProperties property, String paramIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType) {
		createParameter(paramIRI, clazz, description, source, year, dataType);
		property.add(instanceIRI, paramIRI);
	}

	public void addAttributeValue(String instanceIRI, OSDiObjectProperties property, String attributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType) {
		addParameter(instanceIRI, property, attributeValueIRI, clazz, "Value of " + attributeIRI, source, year, dataType);
		OSDiObjectProperties.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
		OSDiObjectProperties.HAS_VALUE.add(attributeIRI, attributeValueIRI);
	}
	
	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, OSDiClasses clazz, String attributeIRI, String source, int year, OSDiDataItemTypes dataType) {
		addAttributeValue(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, attributeIRI, source, year, dataType);
		OSDiObjectProperties.MODIFIES.add(modificationIRI, modifiedAttributeValueIRI);
		OSDiObjectProperties.IS_MODIFIED_BY.add(modifiedAttributeValueIRI, modificationIRI);
	}

	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, OSDiClasses clazz, String description, String source, int year, OSDiDataItemTypes dataType) {
		addParameter(interventionIRI, OSDiObjectProperties.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, dataType);
		OSDiObjectProperties.MODIFIES.add(modificationIRI, modifiedParameterIRI);
		OSDiObjectProperties.IS_MODIFIED_BY.add(modifiedParameterIRI, modificationIRI);
	}

	public void addCost(String instanceIRI, OSDiObjectProperties property, String paramIRI, String description, String source, int year, boolean appliesOneTime, OSDiDataItemTypes currency) {
		addParameter(instanceIRI, property, paramIRI, OSDiClasses.COST, description, source, year, currency);
		OSDiDataProperties.APPLIES_ONE_TIME.add(paramIRI, appliesOneTime ? "true" : "false");
	}

	public void addUtility(String instanceIRI, OSDiObjectProperties property, String utilityParamIRI, String description, String source, int year, boolean appliesOneTime, boolean isDisutility) {
		addParameter(instanceIRI, property, utilityParamIRI, OSDiClasses.UTILITY, description, source, year, isDisutility ? OSDiDataItemTypes.DI_DISUTILITY : OSDiDataItemTypes.DI_UTILITY);
		OSDiDataProperties.APPLIES_ONE_TIME.add(utilityParamIRI, appliesOneTime ? "true" : "false");
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
				source, year, OSDiDataItemTypes.DI_LOWER_95_CONFIDENCE_LIMIT);
		addDeterministicNature(paramUncertaintyIRI, values[0]);
		params[0] = paramUncertaintyIRI;
		paramUncertaintyIRI = InstanceIRI.UNCERTAINTY_U95CI.getIRI(mainParamIRI, false);
		addParameter(mainParamIRI, OSDiObjectProperties.HAS_UNCERTAINTY_CHARACTERIZATION, paramUncertaintyIRI, clazz, "Upper 95% confidence interval for " + description, 
				source, year, OSDiDataItemTypes.DI_UPPER_95_CONFIDENCE_LIMIT);
		addDeterministicNature(paramUncertaintyIRI, values[1]);
		params[1] = paramUncertaintyIRI;
		return params;
	}

	public void createProbabilityDistributionExpression(String expressionIRI, OSDiProbabilityDistributionExpressions type, double[] parameters) {
		type.add(expressionIRI, parameters);
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
	
	public void createDiseaseProgression(String instanceName, DiseaseProgressionType type, String description, Set<String> exclusions, String diseaseInstanceName) {
		type.getClazz().add(instanceName);		
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		switch(type) {
			case STAGE:
				OSDiObjectProperties.HAS_STAGE.add(diseaseInstanceName, instanceName);
				break;
			case ACUTE_MANIFESTATION:
			case CHRONIC_MANIFESTATION:
			default:
				OSDiObjectProperties.HAS_MANIFESTATION.add(diseaseInstanceName, instanceName);
				break;
		}
		includeInModel(instanceName);
		for (String excludedManif : exclusions) {
			OSDiObjectProperties.EXCLUDES_MANIFESTATION.add(instanceName, InstanceIRI.MANIFESTATION.getIRI(excludedManif));
		}
	}
	
	public void createDiseaseProgressionPathway(String instanceName, String description, String diseaseProgressionInstanceName) {
		OSDiClasses.DISEASE_PROGRESSION_PATHWAY.add(instanceName);		
		OSDiDataProperties.HAS_DESCRIPTION.add(instanceName, description);
		OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.add(diseaseProgressionInstanceName, instanceName);
		includeInModel(instanceName);
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
