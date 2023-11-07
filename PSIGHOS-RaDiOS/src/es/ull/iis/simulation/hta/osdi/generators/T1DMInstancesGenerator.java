/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.generators;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.Clazz;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ModelType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ObjectProperty;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ProbabilityDistributionExpression;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.TemporalBehavior;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMInstancesGenerator {
	private final OSDiWrapper wrap;
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
		wrap = new OSDiWrapper(path, STR_MODEL_NAME, INSTANCE_PREFIX);
		wrap.createWorkingModel(ModelType.DES, "ICR", "First example of T1DM model", "Spain", 2022, "", DisutilityCombinationMethod.ADD);
		
		generateDisease();
		for (ManifestationTemplate manif : ManifestationTemplate.values()) {
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
		final String diseaseIRI = wrap.getDiseaseInstanceName(STR_DISEASE_NAME);
		wrap.createDisease(diseaseIRI, "Type I Diabetes Mellitus", "do:9744", "icd:E10", "omim:222100", "snomed:46635009");
		OSDiWrapper.ObjectProperty.HAS_EPIDEMIOLOGICAL_PARAMETER.add(diseaseIRI, STR_CONST_PREV_1);
		OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_DISEASE.add(STR_CONST_PREV_1, diseaseIRI);
		final String diseaseCostIRI = wrap.getParameterInstanceName(STR_DISEASE_NAME + OSDiWrapper.STR_ANNUAL_COST_SUFFIX);
		wrap.createCost(diseaseCostIRI, 
				"Value computed by substracting the burden of complications from the global burden of DM1 in Spain; finally divided by the prevalent DM1 population", 
				"Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007", TemporalBehavior.ANNUAL, 2012, 1116.733023, DataItemType.CURRENCY_EURO);
		OSDiWrapper.ObjectProperty.HAS_FOLLOW_UP_COST.add(diseaseIRI, diseaseCostIRI);
	}
	
	private void generateInterventions() {
		final String interventionIRI = wrap.getInterventionInstanceName(STR_INTERVENTION_NAME);
		wrap.createIntervention(interventionIRI, OSDiWrapper.InterventionType.THERAPEUTIC, "DCCT intensive arm");
		
		// Create a modification of the HbA1c level
		final String attributeIRI = wrap.getAttributeInstanceName("HbA1c");
		final String attributeValueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "HbA1c");
		final String modificationIRI = wrap.getAttributeValueInstanceModificationName(STR_INTERVENTION_NAME, "HbA1c"); 
		wrap.createAttributeValueModification(modificationIRI, interventionIRI, attributeValueIRI, attributeIRI, "DCCT", 1.5, DataItemType.DI_MEAN_DIFFERENCE);
		// Create uncertainty for the modification
		wrap.createProbabilityDistributionExpression(modificationIRI + OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX, ProbabilityDistributionExpression.NORMAL, new double[] {1.5, 1.1});
		OSDiWrapper.ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY.add(modificationIRI, modificationIRI + OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX);
		
		OSDiWrapper.ObjectProperty.HAS_INTERVENTION.add(wrap.getDiseaseInstanceName(STR_DISEASE_NAME), interventionIRI);		
	}
	
	private void generatePopulationAndAttributes() {
		final String populationIRI = wrap.getPopulationInstanceName(STR_POPULATION_NAME);
		wrap.createPopulation(populationIRI, "First cohort of DCCT Population", 13, 40, 100, 2010);
		OSDiWrapper.ObjectProperty.HAS_EPIDEMIOLOGICAL_PARAMETER.add(populationIRI, STR_CONST_PREV_1);
		OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_POPULATION.add(STR_CONST_PREV_1, populationIRI);

		final String diseaseUtilityIRI = wrap.getParameterInstanceName(STR_POPULATION_NAME + "_ComplicationsFree" + OSDiWrapper.STR_UTILITY_SUFFIX);
		generateUtilityFromAvgCI(wrap, diseaseUtilityIRI, "Utility of DM1 without complications", "TODO: Buscar", 
				new double[] {0.785, 0.889, 0.681}, TemporalBehavior.ANNUAL, 2015, OSDiWrapper.UtilityType.UTILITY);
		OSDiWrapper.ObjectProperty.HAS_UTILITY.add(populationIRI, diseaseUtilityIRI);		
		
		// Define population age (fixed)
		String valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "Age");
		wrap.createAttributeValue(valueIRI, wrap.getAttributeInstanceName("Age"), "DCCT", 26.47933884, DataItemType.DI_OTHER);
		ObjectProperty.HAS_AGE.add(populationIRI, valueIRI);
		
		// Define sex proportion within the population. The default value is 0 (male) because there are more men than women.
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "Sex");
		wrap.createAttributeValue(valueIRI, wrap.getAttributeInstanceName("Sex"), "Proportion of female population in DCCT", 0, DataItemType.DI_PROPORTION);
		ObjectProperty.HAS_SEX.add(populationIRI, valueIRI);
		// Define stochastic uncertainty for sex based on the proportion of female population
		ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY.add(valueIRI, valueIRI + OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX);
		valueIRI += OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX;
		wrap.createProbabilityDistributionExpression(valueIRI, ProbabilityDistributionExpression.BERNOULLI, new double[] {0.483966942});
		
		// Define duration of diabetes for the population
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "DurationOfDiabetes");
		wrap.createAttributeValue(valueIRI, wrap.getAttributeInstanceName("DurationOfDiabetes"), "DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401", 2.6, DataItemType.DI_TIME_TO_EVENT);
		ObjectProperty.HAS_ATTRIBUTE_VALUE.add(populationIRI, valueIRI);
		// Define stochastic uncertainty for Duration of diabetes
		ObjectProperty.HAS_STOCHASTIC_UNCERTAINTY.add(valueIRI, valueIRI + OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX);
		valueIRI += OSDiWrapper.STR_STOCHASTIC_UNCERTAINTY_SUFFIX;
		wrap.createProbabilityDistributionExpression(valueIRI, ProbabilityDistributionExpression.NORMAL, new double[] {2.6, 1.4});
		
		// Define HbAc level for the population (fixed)
		valueIRI = wrap.getPopulationAttributeValueInstanceName(STR_POPULATION_NAME, "HbA1c");
		wrap.createAttributeValue(valueIRI, wrap.getAttributeInstanceName("HbA1c"), 
				"Own calculation from: Design of DCCT (http://diabetes.diabetesjournals.org/content/35/5/530) and DCCT (https://www.nejm.org/doi/10.1056/NEJM199309303291401)", 8.8, DataItemType.DI_OTHER);
		ObjectProperty.HAS_ATTRIBUTE_VALUE.add(populationIRI, valueIRI);
	}
	
	public static void generateUtilityFromAvgCI(OSDiWrapper wrap, String utilityIRI, String description, String source, double[] values, TemporalBehavior tmpBehavior, int year, OSDiWrapper.UtilityType utilityType) {
		wrap.createUtility(utilityIRI, description, source, tmpBehavior, year, values[0], utilityType);
		final String[] params = generateCIParameters(wrap, utilityIRI, Clazz.UTILITY, description, source, new double[] {values[1], values[2]}, year);
		OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.add(params[0], tmpBehavior.getShortName());
		OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.add(params[1], tmpBehavior.getShortName());
	}
	
	public static String[] generateCIParameters(OSDiWrapper wrap, String mainParamIRI, Clazz clazz, String description, String source, double[] values, int year) {
		String []params = new String[2];
		String paramUncertaintyIRI = mainParamIRI + OSDiWrapper.STR_L95CI_SUFFIX;
		wrap.createParameter(paramUncertaintyIRI, clazz, "Lower 95% confidence interval for " + description, 
				source, year, values[0], OSDiWrapper.DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT);
		OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY.add(mainParamIRI, paramUncertaintyIRI);
		params[0] = paramUncertaintyIRI;
		paramUncertaintyIRI = mainParamIRI + OSDiWrapper.STR_U95CI_SUFFIX;
		wrap.createParameter(paramUncertaintyIRI, clazz, "Upper 95% confidence interval for " + description, 
				source, year, values[1], OSDiWrapper.DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT);
		OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY.add(mainParamIRI, paramUncertaintyIRI);		
		params[1] = paramUncertaintyIRI;
		return params;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final T1DMInstancesGenerator gen = new T1DMInstancesGenerator("resources/OSDi_test.owl");
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			e.printStackTrace();
		}

	}

}
