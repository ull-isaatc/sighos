/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.osdi.builders.DiseaseBuilder;
import es.ull.iis.simulation.hta.osdi.builders.InterventionBuilder;
import es.ull.iis.simulation.hta.osdi.builders.PopulationBuilder;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OSDiGenericModel extends HTAModel {
	public static final JexlEngine JEXL = new JexlBuilder().create();
	private final OSDiWrapper wrap; 
	
	/**
	 * 
	 * @param nRuns
	 * @param nPatients
	 * @param path
	 * @param diseaseId
	 * @param populationId
	 * @param method
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 * @throws MalformedSimulationModelException 
	 * TODO: Convert all the bernouilli parameters into BernouilliParam (e.g. Population parameters such as sex)
	 * TODO: Parse and create ParamWrappers in constructors (to be able to throw MalformedOSDiModelException when creating the model and avoid them during the creation of parameters in the repository).
	 */
	public OSDiGenericModel(HTAExperiment experiment, String path, String modelId, String instancePrefix) throws OWLOntologyCreationException, MalformedSimulationModelException {
		super(experiment);
		wrap = new OSDiWrapper(path, modelId, instancePrefix);
		setStudyYear(wrap.parseHasYearProperty(wrap.getWorkingModelInstance()));
		
		final ArrayList<String> methods = OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD.getValues(wrap.getWorkingModelInstance());
		// Assuming that exactly one method was defined
		if (methods.size() != 1)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD, "Exactly one disutility combination method must be specified for the model. Instead, " + methods.size() + " defined.");
		try {
			experiment.setDisutilityCombinationMethod(DisutilityCombinationMethod.valueOf(methods.get(0)));
		}
		catch(IllegalArgumentException ex) {
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD, "Disutility combination method not valid. \"" + methods.get(0) + "\" not found.");			
		}

		// Find the diseases that belong to the model
		final Set<String> diseaseNames = OSDiClasses.DISEASE.getIndividuals(true); 
		if (diseaseNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiObjectProperties.INCLUDES_MODEL_ITEM, "The model does not include any disease.");
		final String diseaseName = (String)diseaseNames.toArray()[0];
		if (diseaseNames.size() > 1)
			wrap.printWarning("Found " + diseaseNames.size() + " diseases included in the model. Only " + diseaseName + " will be used");
		
		// Find the populations that belong to the model
		final Set<String> populationNames = OSDiClasses.POPULATION.getIndividuals(true); 
		if (populationNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiObjectProperties.INCLUDES_MODEL_ITEM, "The model does not include any population.");
		final String populationName = (String)populationNames.toArray()[0];
		if (populationNames.size() > 1)
			wrap.printWarning("Found " + populationNames.size() + " populations included in the model. Only " + populationName + " will be used");
		
		final Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseName, populationName);
		PopulationBuilder.getPopulationInstance(this, populationName, disease);
		
		// Build interventions that belong to the model
		final Set<String> interventionNames = OSDiClasses.INTERVENTION.getIndividuals(true); 
		if (interventionNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiObjectProperties.INCLUDES_MODEL_ITEM, "The model does not include any intervention.");
		for (String interventionName : interventionNames) {
			InterventionBuilder.getInterventionInstance(this, interventionName);
		}
		// TODO: Adapt the rest to use the wrapper

		
		
	}

	public OSDiWrapper getOwlWrapper() {
		return wrap;
	}

	/**
	 * Creates a cost wrapper associated to a specific model item by extracting the information from the ontology
	 * @param modelItemIRI The IRI of the model item (disease, intervention, etc.)
	 * @param modelItemClazz The class of the model item (disease, intervention, etc.)
	 * @param costProperty A specific cost property among those that can be used for a disease
	 * @param paramDescription The type of simulation parameter that should be used for that property 
	 * @param expectedOneTime If true, the cost should be one-time; otherwise, it should be annual
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 */
	public CostParameterWrapper createCostParam(String modelItemIRI, OSDiClasses modelItemClazz, OSDiObjectProperties costProperty, ParameterTemplate paramDescription, boolean expectedOneTime) throws MalformedOSDiModelException {
		Set<String> costs = costProperty.getValues(modelItemIRI, true);
		CostParameterWrapper costParam = null;
		if (costs.size() > 0) {
			String[] costsArray = new String[costs.size()];
			costsArray = costs.toArray(costsArray);
			// Looks for the first cost that fulfills the expected temporal behavior
			for (int i = 0; i < costsArray.length && costParam == null; i++) {
				costParam = new CostParameterWrapper(wrap, costsArray[i], paramDescription.getDefaultDescription());
				if (costParam.appliesOneTime() != expectedOneTime) {
					costParam = null;
				}
			}
			// There was at least one cost defined but no one fulfills the expected temporal behavior
			if (costParam == null) {
				throw new MalformedOSDiModelException(modelItemClazz, modelItemIRI, costProperty, "The cost was expected to be " + (expectedOneTime ? "one-time" : "annual") + "instead, " + (expectedOneTime ? "annual" :"one-time")+ " found");
			}
			else if (costs.size() > 1)
				wrap.printWarning(modelItemIRI, costProperty, "Found more than one valid cost for a property. Using only " + costParam.getOriginalIndividualIRI());
		}
		return costParam;
	}

	/**
	 * Creates the cost wrappers for both onset and annual costs by extracting the information from the ontology. Only one onset and one annual cost should be defined with
	 * the property {@link OSDiObjectProperties.HAS_COST}.
	 * @param modelItemIRI The IRI of the model item (disease, intervention, etc.)
	 * @return An array with two elements. The first one is the onset cost and the second one is the annual cost. If one of them is not defined, the corresponding element is null.	
	 * @throws MalformedOSDiModelException
	 */
	public CostParameterWrapper[] createOnsetAndAnnualCostParams(String modelItemIRI) throws MalformedOSDiModelException {
		final CostParameterWrapper[] costParams = new CostParameterWrapper[2];
		costParams[0] = null;
		costParams[1] = null;
		final Set<String> costs = OSDiObjectProperties.HAS_COST.getValues(modelItemIRI, true);
		if (costs.size() == 0) {
			wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_COST, "No cost defined for " + modelItemIRI + ". Using 0 as a default value");
		}
		else {
			String[] costsArray = new String[costs.size()];
			costsArray = costs.toArray(costsArray);

			for (int i = 0; i < costsArray.length && (costParams[0] == null || costParams[1] == null); i++) {
				CostParameterWrapper costParam = new CostParameterWrapper(wrap, costsArray[i], "Cost for " + modelItemIRI);
				if (costParam.appliesOneTime()) {
					if (costParams[0] != null)
						wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_COST, "Found more than one one-time cost for a model item. Using " + costParams[0].getOriginalIndividualIRI());
					else
						costParams[0] = costParam;
				}
				else {
					if (costParams[1] != null)
						wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_COST, "Found more than one annual cost for a model item. Using " + costParams[1].getOriginalIndividualIRI());
					else
						costParams[1] = costParam;
				}
			}
		}
		return costParams;
	}
		
	/**
	 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
	 * @param modelItemIRI The IRI of the model item (disease, intervention, etc.)
	 * @param modelItemClazz The class of the model item (disease, intervention, etc.)
	 * @param utilityProperty A specific utility property among those that can be used for a disease
	 * @param expectedOneTime If true, the (dis)utility should be one-time; otherwise, it should be annual
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 */
	public UtilityParameterWrapper createUtilityParam(String modelItemIRI, OSDiClasses modelItemClazz, OSDiObjectProperties utilityProperty, boolean expectedOneTime) throws MalformedOSDiModelException {
		final Set<String> utilities = utilityProperty.getValues(modelItemIRI, true);
		UtilityParameterWrapper utilityParam = null;
		if (utilities.size() > 0) {
			String[] utilitiesArray = new String[utilities.size()];
			utilitiesArray = utilities.toArray(utilitiesArray);
			// Looks for the first utility that fulfills the expected temporal behavior
			for (int i = 0; i < utilitiesArray.length && utilityParam == null; i++) {
				utilityParam = new UtilityParameterWrapper(wrap, utilitiesArray[i], "Utility for disease " + modelItemIRI);
				if (utilityParam.appliesOneTime() != expectedOneTime) {
					utilityParam = null;
				}
			}
			// There was at least one utility defined but no one fulfills the expected temporal behavior
			if (utilityParam == null) {
				throw new MalformedOSDiModelException(modelItemClazz, modelItemIRI, utilityProperty, "The (dis)utility was expected to be " + (expectedOneTime ? "one-time" : "annual") + "instead, " + (expectedOneTime ? "annual" :"one-time")+ " found");
			}
			else if (utilities.size() > 1)
				wrap.printWarning(modelItemIRI, utilityProperty, "Found more than one valid (dis)utility for a property. Using only " + utilityParam.getOriginalIndividualIRI());
		}
		return utilityParam;
	}

	/**
	 * Creates the utility wrappers for both onset and annual utilities by extracting the information from the ontology. Only one onset and one annual utility should be defined with
	 * the property {@link OSDiObjectProperties.HAS_UTILITY}.
	 * @param modelItemIRI The IRI of the model item (disease, intervention, etc.)
	 * @return An array with two elements. The first one is the onset utility and the second one is the annual utility. If one of them is not defined, the corresponding element is null.	
	 * @throws MalformedOSDiModelException
	 */
	public UtilityParameterWrapper[] createOnsetAndAnnualUtilityParams(String modelItemIRI) throws MalformedOSDiModelException {
		final UtilityParameterWrapper[] utilityParams = new UtilityParameterWrapper[2];
		utilityParams[0] = null;
		utilityParams[1] = null;
		final Set<String> utilities = OSDiObjectProperties.HAS_UTILITY.getValues(modelItemIRI, true);
		if (utilities.size() == 0) {
			wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_UTILITY, "No (dis)utility defined for " + modelItemIRI + ". Using 0 as a default value");
		}
		else {
			String[] utilitiesArray = new String[utilities.size()];
			utilitiesArray = utilities.toArray(utilitiesArray);

			for (int i = 0; i < utilitiesArray.length && (utilityParams[0] == null || utilityParams[1] == null); i++) {
				UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilitiesArray[i], "(Dis)utility for " + modelItemIRI);
				if (utilityParam.appliesOneTime()) {
					if (utilityParams[0] != null)
						wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_UTILITY, "Found more than one one-time (dis)utility for a model item. Using " + utilityParams[0].getOriginalIndividualIRI());
					else
						utilityParams[0] = utilityParam;
				}
				else {
					if (utilityParams[1] != null)
						wrap.printWarning(modelItemIRI, OSDiObjectProperties.HAS_UTILITY, "Found more than one annual (dis)utility for a model item. Using " + utilityParams[1].getOriginalIndividualIRI());
					else
						utilityParams[1] = utilityParam;
				}
			}
		}
		return utilityParams;
	}
	
}
