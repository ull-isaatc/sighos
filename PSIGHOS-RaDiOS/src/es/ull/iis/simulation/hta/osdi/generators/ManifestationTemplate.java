package es.ull.iis.simulation.hta.osdi.generators;

import java.util.Set;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.ManifestationType;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.TemporalBehavior;

public enum ManifestationTemplate {
	SHE("Severe Hypoglycemic Episode", ManifestationType.ACUTE),
	ANGINA("Angina", ManifestationType.CHRONIC) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			String costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ANNUAL_COST_SUFFIX);
			wrap.createCost(costIRI, "Year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 532.01, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ONETIME_COST_SUFFIX);
			wrap.createCost(costIRI, "Episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 1985.96, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			String utilityIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_UTILITY_SUFFIX);
			T1DMInstancesGenerator.generateUtilityFromAvgCI(wrap, utilityIRI, "Annual disutility of Angina", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.09, 0.054, 0.126}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			OSDiWrapper.ObjectProperty.HAS_UTILITY.add(getInstanceName(), utilityIRI);
			
			String imrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCREASED_MORTALITY_RATE_SUFFIX);
			wrap.createParameter(imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for Angina", "https://doi.org/10.2337/diacare.28.3.617", 2005, 1.96, DataItemType.DI_RELATIVE_RISK);
			T1DMInstancesGenerator.generateCIParameters(wrap, imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for Angina", "https://doi.org/10.2337/diacare.28.3.617", new double[] {1.33, 2.89}, 2005);
			OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE.add(getInstanceName(), imrIRI);
		}
	},
	HF("Heart Failure", ManifestationType.CHRONIC) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			String costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ANNUAL_COST_SUFFIX);
			wrap.createCost(costIRI, "Year 2+ of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 1054.42, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ONETIME_COST_SUFFIX); 
			wrap.createCost(costIRI, "Episode of heart failure", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 4503.24, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			String utilityIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_UTILITY_SUFFIX);
			T1DMInstancesGenerator.generateUtilityFromAvgCI(wrap, utilityIRI, "Annual disutility of heart failure", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.108, 0.048, 0.169}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			OSDiWrapper.ObjectProperty.HAS_UTILITY.add(getInstanceName(), utilityIRI);
			
			String imrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCREASED_MORTALITY_RATE_SUFFIX);
			wrap.createParameter(imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for heart failure", "https://doi.org/10.2337/diacare.28.3.617", 2005, 1.96, DataItemType.DI_RELATIVE_RISK);
			T1DMInstancesGenerator.generateCIParameters(wrap, imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for heart failure", "https://doi.org/10.2337/diacare.28.3.617", new double[] {1.33, 2.89}, 2005);
			OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE.add(getInstanceName(), imrIRI);
		}
	},
	STROKE("Stroke", ManifestationType.CHRONIC) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			String costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ANNUAL_COST_SUFFIX);
			wrap.createCost(costIRI, "Year 2+ of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 2485.66, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ONETIME_COST_SUFFIX);
			wrap.createCost(costIRI, "Episode of stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 3634.66, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			String utilityIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_UTILITY_SUFFIX);
			T1DMInstancesGenerator.generateUtilityFromAvgCI(wrap, utilityIRI, "Annual disutility of myocardial infarction", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.055, 0.042, 0.067}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			OSDiWrapper.ObjectProperty.HAS_UTILITY.add(getInstanceName(), utilityIRI);
			
			String imrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCREASED_MORTALITY_RATE_SUFFIX);
			wrap.createParameter(imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for stroke", "https://doi.org/10.2337/diacare.28.3.617", 2005, 1.96, DataItemType.DI_RELATIVE_RISK);
			T1DMInstancesGenerator.generateCIParameters(wrap, imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for stroke", "https://doi.org/10.2337/diacare.28.3.617", new double[] {1.33, 2.89}, 2005);
			OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE.add(getInstanceName(), imrIRI);
			
			String pDeathIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_DEATH_PROBABILITY_SUFFIX);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.createParameter(pDeathIRI, OSDiWrapper.Clazz.PARAMETER, "Probability of sudden death after Stroke (average men-women)", "As in CORE Model", 2005, 0.124, DataItemType.DI_PROBABILITY);
			OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH.add(getInstanceName(), pDeathIRI);
		}
	},
	MI("Myocardial Infarction", ManifestationType.CHRONIC) {
		@Override
		protected void createParameters(OSDiWrapper wrap) {
			String costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ANNUAL_COST_SUFFIX);
			wrap.createCost(costIRI, "Year 2+ of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ANNUAL, 2016, 948, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			costIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_ONETIME_COST_SUFFIX);
			wrap.createCost(costIRI, "Episode of myocardial infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", TemporalBehavior.ONETIME, 2016, 22588, DataItemType.CURRENCY_EURO);
			OSDiWrapper.ObjectProperty.HAS_COST.add(getInstanceName(), costIRI);
			String utilityIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_UTILITY_SUFFIX);
			T1DMInstancesGenerator.generateUtilityFromAvgCI(wrap, utilityIRI, "Annual disutility of stroke", "Bagust and Beale (10.1002/hec.910)", 
					new double[] {0.164, 0.105, 0.222}, TemporalBehavior.ANNUAL, 2005, OSDiWrapper.UtilityType.DISUTILITY);
			OSDiWrapper.ObjectProperty.HAS_UTILITY.add(getInstanceName(), utilityIRI);
			
			String imrIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_INCREASED_MORTALITY_RATE_SUFFIX);
			wrap.createParameter(imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for myocardial infarction", "https://doi.org/10.2337/diacare.28.3.617", 2005, 1.96, DataItemType.DI_RELATIVE_RISK);
			T1DMInstancesGenerator.generateCIParameters(wrap, imrIRI, OSDiWrapper.Clazz.PARAMETER, "Increased mortality rate for myocardial infarction", "https://doi.org/10.2337/diacare.28.3.617", new double[] {1.33, 2.89}, 2005);
			OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE.add(getInstanceName(), imrIRI);
			
			String pDeathIRI = wrap.getParameterInstanceName(OSDiWrapper.STR_MANIF_PREFIX + this + OSDiWrapper.STR_DEATH_PROBABILITY_SUFFIX);
			// TODO: create an expression to use sex to distinguish death probability
			wrap.createParameter(pDeathIRI, OSDiWrapper.Clazz.PARAMETER, "Probability of sudden death after MI (average men-women)", "As in CORE Model", 2005, 0.3785, DataItemType.DI_PROBABILITY);
			OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH.add(getInstanceName(), pDeathIRI);
		}
	},
	BGRET("Background Retinopathy", ManifestationType.CHRONIC),
	PRET("Proliferative Retinopathy", ManifestationType.CHRONIC),
	ME("Macular Edema", ManifestationType.CHRONIC),
	BLI("Blindness", ManifestationType.CHRONIC),
	NEU("Neuropathy", ManifestationType.CHRONIC),
	LEA("Lower Extremity Amputation", ManifestationType.CHRONIC),
	ALB1("Microalbuminuria", ManifestationType.CHRONIC),
	ALB2("Macroalbuminuria", ManifestationType.CHRONIC),
	ESRD("End-Stage Renal Disease", ManifestationType.CHRONIC);
	
	static {
		// Define all the exclusions among manifestations
		ManifestationTemplate.ANGINA.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.MI));
		ManifestationTemplate.HF.addExclusions(Set.of(ManifestationTemplate.ANGINA, ManifestationTemplate.STROKE, ManifestationTemplate.MI));
		ManifestationTemplate.STROKE.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.ANGINA, ManifestationTemplate.MI));
		ManifestationTemplate.MI.addExclusions(Set.of(ManifestationTemplate.HF, ManifestationTemplate.STROKE, ManifestationTemplate.ANGINA));
		ManifestationTemplate.PRET.addExclusions(Set.of(ManifestationTemplate.BGRET));
		ManifestationTemplate.BLI.addExclusions(Set.of(ManifestationTemplate.BGRET, ManifestationTemplate.PRET, ManifestationTemplate.ME));
		ManifestationTemplate.LEA.addExclusions(Set.of(ManifestationTemplate.NEU));
		ManifestationTemplate.ALB2.addExclusions(Set.of(ManifestationTemplate.ALB1));
		ManifestationTemplate.ESRD.addExclusions(Set.of(ManifestationTemplate.ALB1, ManifestationTemplate.ALB2));
	}
	
	private final String description;
	private final OSDiWrapper.ManifestationType type;
	private final Set<ManifestationTemplate> exclusions;
	private String instanceName = null;
	/**
	 * @param description
	 * @param type
	 */
	private ManifestationTemplate(String description, ManifestationType type) {
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
	public OSDiWrapper.ManifestationType getType() {
		return type;
	}
	
	/**
	 * @return the exclusions
	 */
	public Set<ManifestationTemplate> getExclusions() {
		return exclusions;
	}		
	
	public void addExclusions(Set<ManifestationTemplate> newExclusions) {
		exclusions.addAll(newExclusions);
	}
	
	public void generate(OSDiWrapper wrap) {
		final Set<String> strExclusions = new TreeSet<>();
		for (ManifestationTemplate exclManif : exclusions)
			strExclusions.add(exclManif.name());
		instanceName = wrap.getManifestationInstanceName(name());
		wrap.createManifestation(instanceName, type, description, strExclusions, wrap.getDiseaseInstanceName(T1DMInstancesGenerator.STR_DISEASE_NAME));
		createParameters(wrap);
	}
	
	protected void createParameters(OSDiWrapper wrap) {		
	}
	
	public String getInstanceName() {
		return instanceName;
	}
}