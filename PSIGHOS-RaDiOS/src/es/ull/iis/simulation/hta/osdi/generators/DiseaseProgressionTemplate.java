package es.ull.iis.simulation.hta.osdi.generators;

import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.ModifiableOSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper.DiseaseProgressionType;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper.ExpressionLanguage;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiProbabilityDistributionExpressions;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;

public enum DiseaseProgressionTemplate {
	// SHE("Severe Hypoglycemic Episode", DiseaseProgressionType.ACUTE_MANIFESTATION),
	ANGINA("Angina", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 532.01);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 1985.96);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of Angina", "Bagust and Beale (10.1002/hec.910)", 2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.09, 0.054, 0.126}, OSDiClasses.UTILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			wrap.createDiseaseProgressionPathway(pathwayIRI, "Path to " + getDescription(), instanceIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.28});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of angina within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION);
			wrap.addSecondOrderNature(propIRI, 0.28, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));	
		}
	},
	HF("Heart Failure", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 1054.42);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 4503.24);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of heart failure", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.108, 0.048, 0.169}, OSDiClasses.UTILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			wrap.createDiseaseProgressionPathway(pathwayIRI, "Path to " + getDescription(), instanceIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.12});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of heart failure within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION);
			wrap.addSecondOrderNature(propIRI, 0.12, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	STROKE("Stroke", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 2485.66);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 3634.66);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of myocardial infarction", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.055, 0.042, 0.067}, OSDiClasses.UTILITY);
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiClasses.PARAMETER, "Probability of sudden death after Stroke (average men-women)", "As in CORE Model", 2005, OSDiDataItemTypes.DI_PROBABILITY);
			wrap.addDeterministicNature(pDeathIRI, 0.124);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			wrap.createDiseaseProgressionPathway(pathwayIRI, "Path to " + getDescription(), instanceIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.07});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of stroke within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION);
			wrap.addSecondOrderNature(propIRI, 0.07, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	MI("Myocardial Infarction", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 948);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 22588);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of stroke", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.164, 0.105, 0.222}, OSDiClasses.UTILITY);
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiClasses.PARAMETER, "Probability of sudden death after MI (average men-women)", "As in CORE Model", 2005, OSDiDataItemTypes.DI_PROBABILITY);
			wrap.addDeterministicNature(pDeathIRI, 0.3785);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			wrap.createDiseaseProgressionPathway(pathwayIRI, "Path to " + getDescription(), instanceIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.53});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of myocardial infarction within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION);
			wrap.addSecondOrderNature(propIRI, 0.53, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	// BGRET("Background Retinopathy", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	// PRET("Proliferative Retinopathy", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	// ME("Macular Edema", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	// BLI("Blindness", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	NEU("Neuropathy", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Annual cost of neuropathy", "Ray (2015)", 2015, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 3108.86);

			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of neuropathy", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.084, 0.057, 0.111}, OSDiClasses.UTILITY);

			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate due to peripheral neuropathy (vibratory sense diminished)", 
					"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(imrIRI, new double[] {1.51, 1.00, 2.28}, OSDiClasses.PARAMETER);

			addSheffieldParameters(wrap, 5.3, new double[] {0.0354, 0.020, 0.055});
		}
	},
	LEA("Lower Extremity Amputation", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Annual cost after low extremity amputation", "del Pino et al", 2017, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 918.01);

			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Cost of low extremity amputation", "Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", 2017, true, OSDiDataItemTypes.CURRENCY_EURO);
			String costSDIRI = OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(costIRI, false);
			wrap.createParameter(costSDIRI, OSDiClasses.COST, "Standard deviation for the cost of low extremity amputation", "Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", 2017, OSDiDataItemTypes.DI_STANDARD_DEVIATION);
			wrap.addDeterministicNature(costSDIRI, 1674.37);
			wrap.addSecondOrderNature(costIRI, 11333.04, costSDIRI);

			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility after low extremity amputation", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.28, 0.17, 0.389}, OSDiClasses.UTILITY);

			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate due to peripheral neuropathy (amputation)", 
					"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(imrIRI, new double[] {3.98, 1.84, 8.59}, OSDiClasses.PARAMETER);

			String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addParameter(getInstanceIRI(), OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Incidence of " + getDescription(), 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 1995, OSDiDataItemTypes.DI_PROBABILITY);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(incidenceIRI, false), OSDiProbabilityDistributionExpressions.UNIFORM, new double[] {0.0, 0.0006});
			wrap.addSecondOrderNature(incidenceIRI, 0.0003, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(incidenceIRI, false));

			addPathway(wrap, NEU, "Klein et al. 2004 (also Sheffield)", 1995, new double[]{0.0154, 0.01232, 0.01848}, false);
		}
	},
	ALB1("Microalbuminuria", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Annual cost of microalbuminuria", "Assumption", 2024, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 0.0);

			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of microalbuminuria", "Assumption", 
					2024, false, true);
			wrap.addDeterministicNature(utilityIRI, 0.0);

			addSheffieldParameters(wrap, 3.25, new double[] {0.0436, 0.0136, 0.0736});
		}
	},
	ALB2("Macroalbuminuria", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Annual cost of macroalbuminuria", "Assumption", 2024, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 0.0);

			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of macroalbuminuria", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.048, 0.005, 0.091}, OSDiClasses.UTILITY);

			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate due to severe proteinuria", 
			"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(imrIRI, new double[] {2.23, 1.11, 4.49}, OSDiClasses.PARAMETER);

			// CIs assumed to be +- 0.001
			addSheffieldParameters(wrap, 7.95, new double[] {0.0037, 0.0027, 0.0047});
			// CIs assumed to be +- 0.001
			addPathway(wrap, ALB1, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 1995, new double[]{0.1565, 0.1465, 0.1665}, true);
		}
	},
	ESRD("End-Stage Renal Disease", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Annual cost of end-stage renal disease", "Ray (2005)", 2015, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 34259.48);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Cost of onset of end-stage renal disease", "Ray (2005)", 2015, false, OSDiDataItemTypes.CURRENCY_EURO);
			wrap.addDeterministicNature(costIRI, 3250.73);

			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of end-stage renal disease", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true);
			wrap.addSecondOrderNature(utilityIRI, new double[] {0.204, 0.066, 0.342}, OSDiClasses.UTILITY);

			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate due to end-stage renal disease", 
			"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(imrIRI, new double[] {4.53, 2.64, 7.77}, OSDiClasses.PARAMETER);

			String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null);
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Incidence of end-stage renal disease", 
			"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 1995, OSDiDataItemTypes.DI_PROBABILITY);
			// CIs assumed to be +- 0,0001
			wrap.addSecondOrderNature(incidenceIRI, new double[] {0.0002, 0.0001, 0.0003}, OSDiClasses.PARAMETER);			

			addPathway(wrap, ALB1, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 1995, new double[]{0.0133, 0.01064, 0.01596}, false);

			// CIs assumed to be +- 0,01
			addPathway(wrap, ALB2, "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 1995, new double[]{0.1579, 0.1479, 0.1679}, false);
		}
	},
	CHD("Coronary Heart Disease", DiseaseProgressionType.STAGE) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();
			
			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate for CHD", 
					"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(imrIRI, new double[] {1.96, 1.33, 2.89}, OSDiClasses.PARAMETER);

			String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null);
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", 
					"Hoerger 2004", 2004, OSDiDataItemTypes.DI_PROBABILITY);
			wrap.addSecondOrderNature(incidenceIRI, new double[] {0.0045, 0.001, 0.0084}, OSDiClasses.PARAMETER);
			
			String baseRrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base");  
			wrap.createParameter(baseRrIRI, OSDiClasses.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addSecondOrderNature(baseRrIRI, new double[] {1.15, 0.92, 1.43}, OSDiClasses.PARAMETER);
			
			final TreeSet<String> dependentAttributes = new TreeSet<>();
			dependentAttributes.add("HbA1c");
			final TreeSet<String> dependentParameters = new TreeSet<>();
			dependentParameters.add(OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base", false));
			final String rrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false)); 
			final String rrExpression = baseRrIRI + "^("+ OSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c", false) + " - 9.1)";
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, rrIRI, OSDiClasses.PARAMETER, "RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, OSDiDataItemTypes.DI_RELATIVE_RISK);
			wrap.addCalculatedNature(rrIRI, rrExpression, ExpressionLanguage.JEXL, dependentAttributes, dependentParameters);
			// TODO: Characterize pathway from NPH to CHD
//			incidenceIRI = OSDiWrapper.NAME.PARAM_INCIDENCE.getInstanceIRI(getInstanceName())
//			incidenceIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCIDENCE_SUFFIX);
//			wrap.createParameter(incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Incidence of any manifestation related to CHD", "Klein 2014", 2014, 0.0224, DataItemType.DI_PROBABILITY);
//			T1DMInstancesGenerator.generateCIParameters(wrap, incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Incidence of any manifestation related to CHD", "Klein 2014", new double[] {0.013, 0.034}, 2014);
		}
	};
	
	static {
		// Define all the exclusions among manifestations
		DiseaseProgressionTemplate.ANGINA.addExclusions(Set.of(DiseaseProgressionTemplate.HF, DiseaseProgressionTemplate.STROKE, DiseaseProgressionTemplate.MI));
		DiseaseProgressionTemplate.HF.addExclusions(Set.of(DiseaseProgressionTemplate.ANGINA, DiseaseProgressionTemplate.STROKE, DiseaseProgressionTemplate.MI));
		DiseaseProgressionTemplate.STROKE.addExclusions(Set.of(DiseaseProgressionTemplate.HF, DiseaseProgressionTemplate.ANGINA, DiseaseProgressionTemplate.MI));
		DiseaseProgressionTemplate.MI.addExclusions(Set.of(DiseaseProgressionTemplate.HF, DiseaseProgressionTemplate.STROKE, DiseaseProgressionTemplate.ANGINA));
		// DiseaseProgressionTemplate.PRET.addExclusions(Set.of(DiseaseProgressionTemplate.BGRET));
		// DiseaseProgressionTemplate.BLI.addExclusions(Set.of(DiseaseProgressionTemplate.BGRET, DiseaseProgressionTemplate.PRET, DiseaseProgressionTemplate.ME));
		DiseaseProgressionTemplate.LEA.addExclusions(Set.of(DiseaseProgressionTemplate.NEU));
		DiseaseProgressionTemplate.ALB2.addExclusions(Set.of(DiseaseProgressionTemplate.ALB1));
		DiseaseProgressionTemplate.ESRD.addExclusions(Set.of(DiseaseProgressionTemplate.ALB1, DiseaseProgressionTemplate.ALB2));
	}
	
	private final String description;
	private final OSDiWrapper.DiseaseProgressionType type;
	private final Set<DiseaseProgressionTemplate> exclusions;
	/**
	 * @param description
	 * @param type
	 */
	private DiseaseProgressionTemplate(String description, DiseaseProgressionType type) {
		this.description = description;
		this.type = type;
		this.exclusions = new TreeSet<>();
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the type
	 */
	public OSDiWrapper.DiseaseProgressionType getType() {
		return type;
	}
	
	/**
	 * @return the exclusions
	 */
	public Set<DiseaseProgressionTemplate> getExclusions() {
		return exclusions;
	}		
	
	public void addExclusions(Set<DiseaseProgressionTemplate> newExclusions) {
		exclusions.addAll(newExclusions);
	}
	
	public String getInstanceIRI() {
		return OSDiWrapper.DiseaseProgressionType.STAGE.equals(getType()) ? OSDiWrapper.InstanceIRI.STAGE.getIRI(name()) : OSDiWrapper.InstanceIRI.MANIFESTATION.getIRI(name());

	}
	public void generate(ModifiableOSDiWrapper wrap) {
		final Set<String> strExclusions = new TreeSet<>();
		for (DiseaseProgressionTemplate exclManif : exclusions)
			strExclusions.add(exclManif.name());
			wrap.createDiseaseProgression(getInstanceIRI(), type, description, strExclusions, OSDiWrapper.InstanceIRI.DISEASE.getIRI(T1DMInstancesGenerator.STR_DISEASE_NAME));			
		createParameters(wrap);
	}
	
	protected void createParameters(ModifiableOSDiWrapper wrap) {		
	}

	protected void addSheffieldParameters(ModifiableOSDiWrapper wrap, double beta, double[] incidence) {
		addSheffieldBeta(wrap, beta);
		addSheffieldIncidence(wrap, incidence);
	}

	protected void addSheffieldBeta(ModifiableOSDiWrapper wrap, double beta) {
		String betaIRI = wrap.getInstancePrefix() + "BETA_" + OSDiWrapper.InstanceIRI.MANIFESTATION.getIRI(this.name(), false);
		wrap.createParameter(betaIRI, OSDiClasses.PARAMETER, "Beta parameter for " + getDescription(), "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 1996, OSDiDataItemTypes.DI_RELATIVE_RISK);
		wrap.addDeterministicNature(betaIRI, beta);
		useSheffieldBeta(wrap, getInstanceIRI());
	}

	protected void useSheffieldBeta(ModifiableOSDiWrapper wrap, String instanceIRI) {
		String betaIRI = wrap.getInstancePrefix() + "BETA_" + OSDiWrapper.InstanceIRI.MANIFESTATION.getIRI(this.name(), false);
		String rrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
		final TreeSet<String> dependentAttributes = new TreeSet<>();
		dependentAttributes.add("HbA1c");
		final TreeSet<String> dependentParameters = new TreeSet<>();
		dependentParameters.add("BETA_" + OSDiWrapper.InstanceIRI.MANIFESTATION.getIRI(this.name(), false));
		wrap.addCalculatedNature(rrIRI, "("+ OSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c", false) + " / 10) ^" + betaIRI, ExpressionLanguage.JEXL, dependentAttributes, dependentParameters);
		wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, rrIRI, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Relative risk expression for " + getDescription(), 
			"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 1996, OSDiDataItemTypes.DI_RELATIVE_RISK);
	}

	protected void addSheffieldIncidence(ModifiableOSDiWrapper wrap, double[] incidence) {
		String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
		wrap.addParameter(getInstanceIRI(), OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Base incidence of " + getDescription() + " when HbA1c is 10", 
			"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 1995, OSDiDataItemTypes.DI_PROBABILITY);
		wrap.addSecondOrderNature(incidenceIRI, incidence, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, "Base incidence of " + getDescription() + " when HbA1c is 10", 
			"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 1995);
	}
	
	protected void addPathway(ModifiableOSDiWrapper wrap, DiseaseProgressionTemplate pathwayFrom, String source, int year, double[] incidence, boolean isSheffield) {
		final String incidenceIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(pathwayFrom.name() + "_" + this.name());
		wrap.createDiseaseProgressionPathway(incidenceIRI, "Path from " + pathwayFrom.getDescription() + " to " + getDescription(), getInstanceIRI());
		final String description = isSheffield ? ("Base incidence of the pathway from " + pathwayFrom.getDescription() + " to " + getDescription() + " when HbA1c is 10") : 
			("Incidence of the pathway from " + pathwayFrom.getDescription() + " to " + getDescription());
		wrap.addParameter(incidenceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(incidenceIRI, false), OSDiClasses.EPIDEMIOLOGICAL_PARAMETER, 
			description, 
			source, year, OSDiDataItemTypes.DI_PROBABILITY);
		wrap.addSecondOrderNature(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(incidenceIRI, false), incidence, OSDiClasses.EPIDEMIOLOGICAL_PARAMETER);
		OSDiObjectProperties.REQUIRES.add(incidenceIRI, pathwayFrom.getInstanceIRI());
		if (isSheffield) {
			useSheffieldBeta(wrap, incidenceIRI);
		}
	}

}