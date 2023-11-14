package es.ull.iis.simulation.hta.osdi.generators;

import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DiseaseProgressionType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ProbabilityDistributionExpression;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.TemporalBehavior;

public enum DiseaseProgressionTemplate {
	SHE("Severe Hypoglycemic Episode", DiseaseProgressionType.ACUTE_MANIFESTATION),
	ANGINA("Angina", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 532.01, DataItemType.CURRENCY_EURO);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 1985.96, DataItemType.CURRENCY_EURO);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtilityFromAvgCI(instanceIRI, OSDiWrapper.ObjectProperty.HAS_UTILITY, utilityIRI, "Annual disutility of Angina", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.09, 0.054, 0.126}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiWrapper.Clazz.MANIFESTATION_PATHWAY.add(pathwayIRI);
			OSDiWrapper.ObjectProperty.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addParameter(pathwayIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, propIRI, OSDiWrapper.Clazz.PROPORTION_WITHIN_GROUP, "Proportion of angina within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, 0.28, DataItemType.DI_PROPORTION);
			OSDiWrapper.ObjectProperty.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
			wrap.addProbabilityDistributionExpression(propIRI, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY,
					OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), ProbabilityDistributionExpression.GAMMA, new double[] {1.0, 0.28});
			
		}
	},
	HF("Heart Failure", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Year 2+ of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 1054.42, DataItemType.CURRENCY_EURO);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null); 
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Episode of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 4503.24, DataItemType.CURRENCY_EURO);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtilityFromAvgCI(instanceIRI, OSDiWrapper.ObjectProperty.HAS_UTILITY, utilityIRI, "Annual disutility of heart failure", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.108, 0.048, 0.169}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiWrapper.Clazz.MANIFESTATION_PATHWAY.add(pathwayIRI);
			OSDiWrapper.ObjectProperty.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addParameter(pathwayIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, propIRI, OSDiWrapper.Clazz.PROPORTION_WITHIN_GROUP, "Proportion of heart failure within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, 0.12, DataItemType.DI_PROPORTION);
			OSDiWrapper.ObjectProperty.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
			wrap.addProbabilityDistributionExpression(propIRI, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY,
					OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), ProbabilityDistributionExpression.GAMMA, new double[] {1.0, 0.12});
		}
	},
	STROKE("Stroke", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Year 2+ of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 2485.66, DataItemType.CURRENCY_EURO);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Episode of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 3634.66, DataItemType.CURRENCY_EURO);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtilityFromAvgCI(instanceIRI, OSDiWrapper.ObjectProperty.HAS_UTILITY, utilityIRI, "Annual disutility of myocardial infarction", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.055, 0.042, 0.067}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiWrapper.Clazz.PARAMETER, "Probability of sudden death after Stroke (average men-women)", "As in CORE Model", 2005, 0.124, DataItemType.DI_PROBABILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiWrapper.Clazz.MANIFESTATION_PATHWAY.add(pathwayIRI);
			OSDiWrapper.ObjectProperty.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addParameter(pathwayIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, propIRI, OSDiWrapper.Clazz.PROPORTION_WITHIN_GROUP, "Proportion of stroke within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, 0.07, DataItemType.DI_PROPORTION);
			OSDiWrapper.ObjectProperty.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
			wrap.addProbabilityDistributionExpression(propIRI, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY,
					OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), ProbabilityDistributionExpression.GAMMA, new double[] {1.0, 0.07});
		}
	},
	MI("Myocardial Infarction", DiseaseProgressionType.CHRONIC_MANIFESTATION) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();

			String costIRI = OSDiWrapper.InstanceIRI.PARAM_ANNUAL_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Year 2+ of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 948, DataItemType.CURRENCY_EURO);
			costIRI = OSDiWrapper.InstanceIRI.PARAM_ONE_TIME_COST.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addCost(instanceIRI, OSDiWrapper.ObjectProperty.HAS_COST, costIRI, "Episode of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 22588, DataItemType.CURRENCY_EURO);
			String utilityIRI = OSDiWrapper.InstanceIRI.PARAM_UTILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addUtilityFromAvgCI(instanceIRI, OSDiWrapper.ObjectProperty.HAS_UTILITY, utilityIRI, "Annual disutility of stroke", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.164, 0.105, 0.222}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			
			String pDeathIRI = OSDiWrapper.InstanceIRI.PARAM_DEATH_PROBABILITY.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.addParameter(instanceIRI, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH, pDeathIRI, OSDiWrapper.Clazz.PARAMETER, "Probability of sudden death after MI (average men-women)", "As in CORE Model", 2005, 0.3785, DataItemType.DI_PROBABILITY);

			String pathwayIRI = OSDiWrapper.InstanceIRI.MANIFESTATION_PATHWAY.getIRI(this.name());
			OSDiWrapper.Clazz.MANIFESTATION_PATHWAY.add(pathwayIRI);
			OSDiWrapper.ObjectProperty.REQUIRES.add(pathwayIRI, OSDiWrapper.InstanceIRI.STAGE.getIRI(CHD.name()));
			OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.add(instanceIRI, pathwayIRI);
			
			String propIRI = OSDiWrapper.InstanceIRI.PARAM_PROPORTION.getIRI(this.name(), OSDiWrapper.InstanceIRI.MANIFESTATION, null);
			wrap.addParameter(pathwayIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, propIRI, OSDiWrapper.Clazz.PROPORTION_WITHIN_GROUP, "Proportion of myocardial infarction within CHD complications", 
					"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 2005, 0.53, DataItemType.DI_PROPORTION);
			OSDiWrapper.ObjectProperty.BELONGS_TO_GROUP.add(propIRI, OSDiWrapper.InstanceIRI.MANIFESTATION_GROUP.getIRI(GroupOfManifestationsTemplate.CHD.name()));
			wrap.addProbabilityDistributionExpression(propIRI, OSDiWrapper.ObjectProperty.HAS_PARAMETER_UNCERTAINTY,
					OSDiWrapper.InstanceIRI.UNCERTAINTY_PARAM.getIRI(propIRI, false), ProbabilityDistributionExpression.GAMMA, new double[] {1.0, 0.53});
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
		protected void createParameters(OSDiWrapper wrap) {
			final String instanceIRI = getInstanceIRI();
			
			String imrIRI = OSDiWrapper.InstanceIRI.PARAM_INCREASED_MORTALITY_RATE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null); 
			wrap.addParameter(instanceIRI, OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE, imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for CHD", 
					"https://doi.org/10.2337/diacare.28.3.617", 2005, 1.96, DataItemType.DI_RELATIVE_RISK);
			wrap.addCIParameters(imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for CHD", "https://doi.org/10.2337/diacare.28.3.617", new double[] {1.33, 2.89}, 2005);

			String incidenceIRI = OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null);
			wrap.addParameter(instanceIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", 
					"Hoerger 2004", 2004, 0.0045, DataItemType.DI_PROBABILITY);
			wrap.addCIParameters(incidenceIRI, OSDiWrapper.Clazz.INCIDENCE, "Base incidence of any manifestation related to CHD when HbA1c is 9.1", "Hoerger 2004", new double[] {0.001, 0.0084}, 2004);
			
			String baseRrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base");  
			wrap.createParameter(baseRrIRI, OSDiWrapper.Clazz.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, 1.15, DataItemType.DI_RELATIVE_RISK);
			wrap.addCIParameters(baseRrIRI, OSDiWrapper.Clazz.PARAMETER, "Base RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", new double[] {0.92, 1.43}, 2004);
			
			final TreeSet<String> dependentAttributes = new TreeSet<>();
			dependentAttributes.add("HbA1c");
			final TreeSet<String> dependentParameters = new TreeSet<>();
			dependentParameters.add(OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false) + "Base", false));
			String rrIRI = OSDiWrapper.InstanceIRI.PARAM_RELATIVE_RISK.getIRI(OSDiWrapper.InstanceIRI.PARAM_INCIDENCE.getIRI(this.name(), OSDiWrapper.InstanceIRI.STAGE, null, false)); 
			wrap.addParameter(instanceIRI, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, rrIRI, OSDiWrapper.Clazz.PARAMETER, "RR for CHD-related complication, associated to a 1 PP increment of HbA1c with respect to 9.1", 
					"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 2004, baseRrIRI + "^("+ OSDiWrapper.InstanceIRI.ATTRIBUTE.getIRI("HbA1c", false) + " - 9.1)", dependentAttributes, dependentParameters, DataItemType.DI_RELATIVE_RISK);
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
	public void generate(OSDiWrapper wrap) {
		final Set<String> strExclusions = new TreeSet<>();
		for (DiseaseProgressionTemplate exclManif : exclusions)
			strExclusions.add(exclManif.name());
			wrap.createManifestation(getInstanceIRI(), type, description, strExclusions, OSDiWrapper.InstanceIRI.DISEASE.getIRI(T1DMInstancesGenerator.STR_DISEASE_NAME));			
		createParameters(wrap);
	}
	
	protected void createParameters(OSDiWrapper wrap) {		
	}
	
}