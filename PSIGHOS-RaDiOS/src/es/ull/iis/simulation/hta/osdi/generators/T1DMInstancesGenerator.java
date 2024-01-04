/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.generators;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.ull.iis.simulation.hta.osdi.ontology.ModifiableOSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiProbabilityDistributionExpressions;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper.ModelType;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMInstancesGenerator {
	private final ModifiableOSDiWrapper wrap;
	private final static String INSTANCE_PREFIX = "T1DM_";
	private final static String STR_MODEL_NAME = "StdModelDES";
	protected final static String STR_DISEASE_NAME ="Disease";
	private final static String STR_POPULATION_NAME = "DCCT1";
	private final static String STR_INTERVENTION_NAME = "DCCTIntensive";
	private final static String STR_CONST_PREV_1 = "Constant_Prevalence1";
	
	/**
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * 
	 */
	public T1DMInstancesGenerator(String path) throws OWLOntologyCreationException, OWLOntologyStorageException {
		wrap = new ModifiableOSDiWrapper(path, STR_MODEL_NAME, INSTANCE_PREFIX);
		wrap.createWorkingModel(ModelType.DES, "ICR", "First example of T1DM model", "Spain", 2022, "", DisutilityCombinationMethod.ADD);
		
		generateDisease();
		for (DiseaseProgressionTemplate manif : DiseaseProgressionTemplate.values()) {
			manif.generate(wrap);
		}
		for (GroupOfManifestationsTemplate group : GroupOfManifestationsTemplate.values()) {
			group.generate(wrap);
		}
		generatePopulationAndAttributes();
		generateInterventions();
		wrap.printIndividuals(true);
		wrap.save();
	}

	private void generateDisease() {
		final String diseaseIRI = ModifiableOSDiWrapper.InstanceIRI.DISEASE.getIRI(STR_DISEASE_NAME);
		wrap.createDisease(diseaseIRI, "Type I Diabetes Mellitus", "do:9744", "icd:E10", "omim:222100", "snomed:46635009");
		OSDiObjectProperties.HAS_EPIDEMIOLOGICAL_PARAMETER.add(diseaseIRI, STR_CONST_PREV_1);
		OSDiObjectProperties.IS_PARAMETER_OF_DISEASE.add(STR_CONST_PREV_1, diseaseIRI);
		final String diseaseCostIRI = ModifiableOSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(STR_DISEASE_NAME);
		wrap.addCost(diseaseIRI, OSDiObjectProperties.HAS_FOLLOW_UP_COST, diseaseCostIRI, 
				"Value computed by substracting the burden of complications from the global burden of DM1 in Spain; finally divided by the prevalent DM1 population", 
				"Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007", 2012, false, OSDiDataItemTypes.CURRENCY_EURO, 1116.733023);
	}

	private void generateInterventions() {
		final String interventionIRI = ModifiableOSDiWrapper.InstanceIRI.INTERVENTION.getIRI(STR_INTERVENTION_NAME);
		wrap.createIntervention(interventionIRI, ModifiableOSDiWrapper.InterventionType.THERAPEUTIC, "DCCT intensive arm");
		
		// Create a modification of the HbA1c level
		final String attributeIRI = ModifiableOSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c",false);
		final String attributeValueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "HbA1c");
		final String modificationIRI = wrap.getAttributeValueInstanceModificationName(STR_INTERVENTION_NAME, "HbA1c"); 

		// Create uncertainty for the modification
		wrap.createProbabilityDistributionExpression(ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(modificationIRI, false), OSDiProbabilityDistributionExpressions.NORMAL, new double[] {1.5, 1.1});
		wrap.addAttributeValueModification(modificationIRI, interventionIRI, attributeValueIRI, OSDiClasses.PARAMETER, attributeIRI, "DCCT", 2013, OSDiDataItemTypes.DI_MEAN_DIFFERENCE, ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(modificationIRI, false));
		
		OSDiObjectProperties.HAS_INTERVENTION.add(ModifiableOSDiWrapper.InstanceIRI.DISEASE.getIRI(STR_DISEASE_NAME), interventionIRI);		
	}
	
	private void generatePopulationAndAttributes() {
		final String populationIRI = ModifiableOSDiWrapper.InstanceIRI.POPULATION.getIRI(STR_POPULATION_NAME);
		wrap.createPopulation(populationIRI, "First cohort of DCCT Population", 13, 40, 100, 2010);
		OSDiObjectProperties.HAS_EPIDEMIOLOGICAL_PARAMETER.add(populationIRI, STR_CONST_PREV_1);
		OSDiObjectProperties.IS_PARAMETER_OF_POPULATION.add(STR_CONST_PREV_1, populationIRI);

		final String diseaseUtilityIRI = ModifiableOSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(STR_POPULATION_NAME + "_ComplicationsFree");
		wrap.addUtility(populationIRI, OSDiObjectProperties.HAS_UTILITY, diseaseUtilityIRI, "Utility of DM1 without complications", "TODO: Buscar", 
				2015, false, false, new double[] {0.785, 0.889, 0.681});
		
		// Define population age (fixed)
		String valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "Age");
		wrap.addAttributeValue(populationIRI, OSDiObjectProperties.HAS_AGE, valueIRI, OSDiClasses.PARAMETER, ModifiableOSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("Age", false), "DCCT", 2013, OSDiDataItemTypes.DI_OTHER, 26.47933884);
		
		// Define sex proportion within the population. The default value is 0 (male) because there are more men than women.
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "Sex");
		wrap.createProbabilityDistributionExpression(ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(valueIRI, false), OSDiProbabilityDistributionExpressions.BERNOULLI, new double[] {0.483966942});
		wrap.addAttributeValue(populationIRI, OSDiObjectProperties.HAS_SEX, valueIRI, OSDiClasses.PARAMETER, ModifiableOSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("Sex", false), "Proportion of female population in DCCT", 2013, OSDiDataItemTypes.DI_PROPORTION, ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(valueIRI, false));
		// Define stochastic uncertainty for sex based on the proportion of female population
		
		// Define duration of diabetes for the population
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "DurationOfDiabetes");
		// Define stochastic uncertainty for Duration of diabetes
		wrap.createProbabilityDistributionExpression(ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(valueIRI, false), OSDiProbabilityDistributionExpressions.NORMAL, new double[] {2.6, 1.4});
		wrap.addAttributeValue(populationIRI, OSDiObjectProperties.HAS_ATTRIBUTE_VALUE, valueIRI, OSDiClasses.PARAMETER, ModifiableOSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("DurationOfDiabetes", false), "DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401", 2013, OSDiDataItemTypes.DI_TIME_TO_EVENT, ModifiableOSDiWrapper.InstanceIRI.UNCERTAINTY_STOCHASTIC.getIRI(valueIRI, false));
		
		// Define HbAc level for the population (fixed)
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "HbA1c");
		wrap.addAttributeValue(populationIRI, OSDiObjectProperties.HAS_ATTRIBUTE_VALUE, valueIRI, OSDiClasses.PARAMETER, ModifiableOSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c", false), 
				"Own calculation from: Design of DCCT (http://diabetes.diabetesjournals.org/content/35/5/530) and DCCT (https://www.nejm.org/doi/10.1056/NEJM199309303291401)", 2013, OSDiDataItemTypes.DI_OTHER, 8.8);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new T1DMInstancesGenerator("resources/OSDi_test.owl");
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			e.printStackTrace();
		}

	}

}
