package es.ull.iis.simulation.hta.osdi.generators;

import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.ModifiableOSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper.DiseaseProgressionType;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiProbabilityDistributionExpressions;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;

public enum DiseaseProgressionTemplate {
	SHE("Severe Hypoglycemic Episode", DiseaseProgressionType.ACUTE_MANIFESTATION),
	ANGINA("Angina", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO, 532.01);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO, 1985.96);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of Angina", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true, new double[] {0.09, 0.054, 0.126});

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiClasses.DISEASE_PROGRESSION_PATHWAY.add(pathwayIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.28});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of angina within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION, 0.28, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));	
		}
	},
	HF("Heart Failure", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO, 1054.42);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO, 4503.24);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of heart failure", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true, new double[] {0.108, 0.048, 0.169});

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiClasses.DISEASE_PROGRESSION_PATHWAY.add(pathwayIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.12});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of heart failure within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION, 0.12, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	STROKE("Stroke", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO, 2485.66);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO, 3634.66);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of myocardial infarction", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true, new double[] {0.055, 0.042, 0.067});
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiClasses.PARAMETER, "Probability of sudden death after Stroke (average men-women)", "As in CORE Model", 2005, OSDiDataItemTypes.DI_PROBABILITY, 0.124);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiClasses.DISEASE_PROGRESSION_PATHWAY.add(pathwayIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.07});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of stroke within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION, 0.07, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	MI("Myocardial Infarction", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Year 2+ of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, false, OSDiDataItemTypes.CURRENCY_EURO, 948);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiObjectProperties.HAS_COST, costIRI, "Episode of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, true, OSDiDataItemTypes.CURRENCY_EURO, 22588);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtility(instanceIRI, OSDiObjectProperties.HAS_UTILITY, utilityIRI, "Annual disutility of stroke", "Bagust and Beale (10.1002/hec.910)", 
					2005, false, true, new double[] {0.164, 0.105, 0.222});
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiClasses.PARAMETER, "Probability of sudden death after MI (average men-women)", "As in CORE Model", 2005, OSDiDataItemTypes.DI_PROBABILITY, 0.3785);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiClasses.DISEASE_PROGRESSION_PATHWAY.add(pathwayIRI);
			OSDiObjectProperties.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.createProbabilityDistributionExpression(OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), OSDiProbabilityDistributionExpressions.GAMMA, new double[] {1.0, 0.53});
			wrap.addParameter(pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, propIRI, OSDiClasses.PROPORTION_WITHIN_GROUP, "Proportion of myocardial infarction within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, OSDiDataItemTypes.DI_PROPORTION, 0.53, OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false));
			OSDiObjectProperties.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
		}
	},
	BGRET("Background Retinopathy", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	PRET("Proliferative Retinopathy", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	ME("Macular Edema", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	BLI("Blindness", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	NEU("Neuropathy", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	LEA("Lower Extremity Amputation", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	ALB1("Microalbuminuria", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	ALB2("Macroalbuminuria", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	ESRD("End-Stage Renal Disease", DiseaseProgressionType.CHRONIC_MANIFESTATION),
	CHD("Coronary Heart Disease", DiseaseProgressionType.STAGE) {
		@Override
		protected void createParameters(ModifiableOSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();
			
			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null); 
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiClasses.PARAMETER, "Increased mortality rate for CHD", 
					"https://doi.org/10.2337/diacare.28.3.617", 2005, OSDiDataItemTypes.DI_RELATIVE_RISK, new double[] {1.96, 1.33, 2.89});

			String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null);
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiClasses.INCIDENCE, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", 
					"Hoerger 2004", 2004, OSDiDataItemTypes.DI_PROBABILITY, new double[] {0.0045, 0.001, 0.0084});
			
			String baseRrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base");  
			wrap.createParameter(baseRrIRI, OSDiClasses.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, OSDiDataItemTypes.DI_RELATIVE_RISK, new double[] {1.15, 0.92, 1.43});
			
			final TreeSet<String> dependentAttributes = new TreeSet<>();
			dependentAttributes.add("HbA1c");
			final TreeSet<String> dependentParameters = new TreeSet<>();
			dependentParameters.add(OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base", false));
			final String rrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false)); 
			final String rrExpressionIRI = OSDiWrapper.InstanceIRI.EXPRESSION.getIRI(rrIRI, false);
			wrap.createAdHocExpression(rrExpressionIRI, baseRrIRI + "^("+ OSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c", false) + " - 9.1)", dependentAttributes, dependentParameters);
			wrap.addParameter(instanceIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, rrIRI, OSDiClasses.PARAMETER, "RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, OSDiDataItemTypes.DI_RELATIVE_RISK, rrExpressionIRI);
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
		DiseaseProgressionTemplate.PRET.addExclusions(Set.of(DiseaseProgressionTemplate.BGRET));
		DiseaseProgressionTemplate.BLI.addExclusions(Set.of(DiseaseProgressionTemplate.BGRET, DiseaseProgressionTemplate.PRET, DiseaseProgressionTemplate.ME));
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
			wrap.createManifestation(getInstanceIRI(), type, description, strExclusions, OSDiWrapper.InstanceIRI.DISEASE.getIRI(T1DMInstancesGenerator.STR_DISEASE_NAME));			
		createParameters(wrap);
	}
	
	protected void createParameters(ModifiableOSDiWrapper wrap) {		
	}
	
}