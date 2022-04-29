package es.ull.iis.simulation.hta.osdi;

public interface Constants {
	public static String CONSTANT_DO_NOTHING = "DO_NOTHING";
	public static String CONSTANT_EMPTY_STRING = "";
	public static String CONSTANT_EMPTY_JSON = "{}";
	public static String CONSTANT_DEFAULT_SPACES_SHOW_NODE = "      ";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_3DEC = "%.3f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_6DEC = "%.6f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_9DEC = "%.9f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_12DEC = "%.12f";
	public static String CONSTANT_DOUBLE_TYPE = "#double";
	public static String CONSTANT_STRING_TYPE = "#string";
	public static String CONSTANT_HASHTAG = "#";
	public static String CONSTANT_NEGATION = "!";
	public static String CONSTANT_UNDEFINED_TYPE = "#string";
	public static String CONSTANT_SPLIT_TYPE = "::";
	public static String CONSTANT_DECISION_NODE = "(*)";
	public static String CONSTANT_SENSITIVITY_100 = "1.0";
	public static String CONSTANT_SPECIFICITY_100 = "1.0";
	public static String CONSTANT_VALUE_ZERO = "0.0";
	public static String CONSTANT_VALUE_ONE = "1.0";
	public static String CONSTANT_DISTRUBUTION_SUFFIX = "Distribution";
	public static int CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_GLOBAL = 0;
	public static int CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_SPECIFIC = 1;
	
	public static String REGEX_ANYEXPRESION_TYPE = ".*::[#a-zA-Z0-9(),]+";
	public static String REGEX_NUMERICVALUE_DISTRO = "[0-9.,E-]+#[a-zA-Z0-9(),.#]+";
	public static String REGEX_NUMERICVALUE_DISTRO_EXTENDED = "^([0-9\\.,E-]+)?(#?([A-Z]+)\\(([+-]?[0-9]+\\.?[0-9]*)(,([+-]?[0-9]+\\.?[0-9]*))?\\))?$";	
	public static String REGEX_OPERATION_NUMERICVALUE = "[*/+-][0-9.,E-]+";
   public static String REGEX_NUMERICVALUE = "[0-9.,E-]+";	

	// Disease Datasheet properties
   public static String DATASHEET_NATURAL_DEVELOPMENT_LIFE_EXPECTANCY = "naturalDevelopmentLifeExpectancy"; 
   public static String DATASHEET_FOLLOWUP_TREATMENT_STRATEGIES_COSTS = "followUpTreatmentStrategiesCosts"; 
   public static String DATASHEET_PREVALENCE_AT_BITH = "prevalenceAtBith"; 
   public static String DATASHEET_DISCOUNT_RATE_COSTS = "discountRateCosts"; 
   public static String DATASHEET_DISCOUNT_RATE_EFFECTS = "discountRateEffects"; 
   public static String DATASHEET_UTILITY_GENERAL_POPULATION = "utilityGeneralPopulation"; 
   public static String DATASHEET_INTERVENTION_SUMMARY_QALYS = "summaryQalys"; 
   public static String DATASHEET_INTERVENTION_SUMMARY_COSTS = "summaryCosts"; 

   // Classes
	public static String CLASS_DISEASE = "#Disease";
	public static String CLASS_DEVELOPMENT = "#Development";
	public static String CLASS_TREATMENT = "#Treatment";
	public static String CLASS_FOLLOWUP = "#FollowUp";
	public static String CLASS_SCREENING = "#Screening";
	public static String CLASS_CLINICALDIAGNOSIS = "#ClinicalDiagnosis";	
	public static String CLASS_MANIFESTATION = "#Manifestation";
	public static String CLASS_INTERVENTION = "#Intervention";
	public static String CLASS_COST = "#Cost";
	public static String CLASS_UTILITY = "#Utility";
	public static String CLASS_SCREENINGSTRATEGY = "#ScreeningStrategy";
	public static String CLASS_CLINICALDIAGNOSISSTRATEGY = "#ClinicalDiagnosisStrategy";
	public static String CLASS_TREATMENTSTRATEGY = "#TreatmentStrategy";
	public static String CLASS_FOLLOWUPSTRATEGY = "#FollowUpStrategy";	
	public static String CLASS_SCREENINGSTEPSTRATEGY = "#ScreeningStepStrategy";
	public static String CLASS_CLINICALDIAGNOSISSTEPSTRATEGY = "#ClinicalDiagnosisStepStrategy";
	public static String CLASS_TREATMENTSTEPSTRATEGY = "#TreatmentStepStrategy";
	public static String CLASS_FOLLOWUPSTEPSTRATEGY = "#FollowUpStepStrategy";	
	public static String CLASS_GUIDELINES = "#Guideline";
	public static String CLASS_DRUG = "#Drug";
	public static String CLASS_DEVELOPMENTMODIFICACION = "#DevelopmentModificacion";
	public static String CLASS_MANIFESTATIONMODIFICATION = "#ManifestationModification";

	// Object Properties
	public static String OBJECTPROPERTY_DISEASE_DEVELOPMENTS = "#hasDevelopment";	
	public static String OBJECTPROPERTY_DISEASE_SCREENINGSTRATEGY = "#hasScreeningStrategy";	
	public static String OBJECTPROPERTY_DISEASE_CLINICALDIAGNOSISSTRATEGY = "#hasClinicalDiagnosisStrategy";	
	public static String OBJECTPROPERTY_DISEASE_TREATMENTSTRATEGY = "#hasTreatmentStrategy";	
	public static String OBJECTPROPERTY_DISEASE_FOLLOWUPSTRATEGY = "#hasFollowUpStrategy";	
	public static String OBJECTPROPERTY_DISEASE_INTERVENTIONS = "#hasIntervention";	

	public static String OBJECTPROPERTY_DEVELOPMENT_MODIFICATION = "#hasDevelopmentModification";	
	public static String OBJECTPROPERTY_DEVELOPMENT_MANIFESTATIONS = "#hasManifestation";	
	public static String OBJECTPROPERTY_DEVELOPMENT = "#hasDevelopment";	
	
	public static String OBJECTPROPERTY_MANIFESTATION_MODIFICATION = "#hasManifestationModification";	
	public static String OBJECTPROPERTY_MANIFESTATION = "#hasManifestation";	
	public static String OBJECTPROPERTY_MANIFESTATION_COST = "#hasCost";
	public static String OBJECTPROPERTY_MANIFESTATION_PRECEDINGMANIFESTATIONS = "#hasPrecedingManifestation";	

	public static String OBJECTPROPERTY_PRECEDINGMANIFESTATIONS_PRECEDINGMANIFESTATION = "#hasManifestation";	
	
	public static String OBJECTPROPERTY_UTILITY = "#hasUtility";	
	
	public static String OBJECTPROPERTY_CLINICAL_DIAGNOSIS_COST = "#hasCost";	
	
	public static String OBJECTPROPERTY_INTERVENTION_FOLLOWUP_STRATEGY = "#hasFollowUpStrategy";	
	public static String OBJECTPROPERTY_INTERVENTION_TREATMENT_STRATEGY = "#hasTreatmentStrategy";	
	public static String OBJECTPROPERTY_INTERVENTION_SCREENING_STRATEGY = "#hasScreeningStrategy";	
	public static String OBJECTPROPERTY_INTERVENTION_CLINICAL_DIAGNOSIS_STRATEGY = "#hasClinicalDiagnosisStrategy";	

	// Data Properties
	public static String DATAPROPERTY_BIRTH_PREVALENCE = "#hasBirthPrevalence";
	public static String DATAPROPERTY_KIND_INTERVENTION = "#hasInterventionKind";
	public static String DATAPROPERTY_KIND_DEVELOPMENT = "#hasDevelopmentKind";
	public static String DATAPROPERTY_KIND_MANIFESTATION = "#hasManifestationKind";
	public static String DATAPROPERTY_STAGE = "#hasStage";
	public static String DATAPROPERTY_SPECIFICITY = "#hasSpecificity";
	public static String DATAPROPERTY_SENSITIVITY = "#hasSensitivity";
	public static String DATAPROPERTY_LIFE_EXPECTANCY = "#hasLifeExpectancy";	
	public static String DATAPROPERTY_COST = "#hasCost";	
	public static String DATAPROPERTY_FREQUENCY = "#hasFrequency";	
	public static String DATAPROPERTY_PROBABILITY = "#hasProbability";	
	public static String DATAPROPERTY_PROBABILITY_DISTRIBUTION = "#hasProbabilityDistribution";
	public static String DATAPROPERTY_PROBABILITYOFLEADINGTODIAGNOSIS = "#hasProbabilityOfDiagnosis";
	public static String DATAPROPERTY_MORTALITY_FACTOR = "#hasMortalityFactor";	
	public static String DATAPROPERTY_MORTALITY_FACTOR_DISTRIBUTION = "#hasMortalityFactorDistribution";	
	public static String DATAPROPERTY_RELATIVE_RISK = "#hasRelativeRisk";	
	public static String DATAPROPERTY_FREQUENCY_MODIFICATION = "#hasFrequencyModification";	
	public static String DATAPROPERTY_PROBABILITY_MODIFICATION = "#hasProbabilityModification";	
	public static String DATAPROPERTY_MORTALITY_FACTOR_MODIFICATION = "#hasMortalityFactorModification";	
	public static String DATAPROPERTY_RELATIVE_RISK_MODIFICATION = "#hasRelativeRiskModification";
	public static String DATAPROPERTY_PROBABILITYOFLEADINGTODIAGNOSISMODIFICATION = "#hasProbabilityOfDiagnosisModification";	
	public static String DATAPROPERTY_VALUE = "#hasValue";
	public static String DATAPROPERTY_VALUE_DISTRIBUTION = "#hasValueDistribution";
	public static String DATAPROPERTY_KIND_UTILITY = "#hasUtilityKind";
	public static String DATAPROPERTY_ONSET_AGE = "#hasOnsetAge";
	public static String DATAPROPERTY_END_AGE = "#hasEndAge";
	public static String DATAPROPERTY_TEMPORAL_BEHAVIOR = "#hasTemporalBehavior";
	public static String DATAPROPERTY_DURATION = "#hasDuration";
	public static String DATAPROPERTY_AMOUNT = "#hasAmount";
	public static String DATAPROPERTY_CALCULATEMETHOD = "#hasCalculationMethod";	
	public static String DATAPROPERTY_DOSE = "#hasDose";	
	public static String DATAPROPERTY_HOURSINTERVAL = "#hasHoursInterval";	
	public static String DATAPROPERTY_CONDITIONS = "#hasCondition";	
	public static String DATAPROPERTY_RANGE = "#hasRange";	
	public static String DATAPROPERTY_PERCENTAGETREATED = "#hasPercentageTreated";	
	public static String DATAPROPERTY_PERCENTFORTHENEXT = "#hasPercentageNext";	
	public static String DATAPROPERTY_TEMPORARYTHRESHOLD = "#hasTemporaryThreshold";	
	public static String DATAPROPERTY_MODIFICATIONFORALLPARAMS = "#hasModificationForAllParams";	
	public static String DATAPROPERTY_YEAR = "#hasYear";
	public static String DATAPROPERTY_REPLACEPREVIOUS = "#isReplacingPrevious";

	// Data properties that do not belong to the ontology, but are generated on the fly
	public static String CUSTOM_PROPERTY_CUMULATIVE_COST = "#hasCumulativeCost";
	public static String CUSTOM_PROPERTY_COST_TYPE = "#hasCostType";
	public static String CUSTOM_PROPERTY_CUMULATIVE_PROBABILITY = "#hasCumulativeProbability";
	public static String CUSTOM_PROPERTY_PROBABILITY_DISTRIBUTION = "#hasProbability" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_PROBABILITY_MODIFICATION_DISTRIBUTION = "#hasProbabilityModification" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_FREQUENCY_MODIFICATION_DISTRIBUTION = "#hasFrequencyModification" + CONSTANT_DISTRUBUTION_SUFFIX;	
	public static String CUSTOM_PROPERTY_MORTALITY_FACTOR_MODIFICATION_DISTRIBUTION = "#hasMortalityFactorModification" + CONSTANT_DISTRUBUTION_SUFFIX;	
	public static String CUSTOM_PROPERTY_RELATIVE_RISK_MODIFICATION_DISTRIBUTION = "#hasRelativeRiskModification" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_AMOUNT = "#hasAmount";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE = "#hasUtilityValue";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM = "#hasUtilityValueMinimum";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM_WITH_DISCOUNT = "#hasUtilityValueMinimumWithDiscount";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MULTIPLICATIVE = "#hasUtilityValueMultiplicative";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MULTIPLICATIVE_WITH_DISCOUNT = "#hasUtilityValueMultiplicativeWithDiscount";
	public static String CUSTOM_PROPERTY_UTILITY_DISTRIBUTION = "#hasUtilityValue" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_UTILITY_KIND = "#hasUtilityKind";
	public static String CUSTOM_PROPERTY_ANNUAL_COST = "#hasAnnualCost";
	public static String CUSTOM_PROPERTY_ONETIME_COST = "#hasOnetimeCost";
	public static String CUSTOM_PROPERTY_LIFETIME_COST = "#hasLifetimeCost";
	
	// Data Properties Ranges
	public static String DATAPROPERTYVALUE_KIND_DEVELOPMENT_NATURAL_VALUE = "NATURAL";
	public static String DATAPROPERTYVALUE_KIND_INTERVENTION_SCREENING_VALUE = "SCREENING";
	public static String DATAPROPERTYVALUE_KIND_INTERVENTION_NOSCREENING_VALUE = "NO_SCREENING";
	public static String DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE = "ANNUAL";
	public static String DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE = "ONETIME";
	public static String DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_LIFETIME_VALUE = "LIFETIME";
	public static String DATAPROPERTYVALUE_KIND_UTILITY_UTILITY = "UTILITY";
	public static String DATAPROPERTYVALUE_KIND_UTILITY_DISUTILITY = "DISUTILITY";
	public static String DATAPROPERTYVALUE_KIND_MANIFESTATION_CHRONIC = "CHRONIC";
	public static String DATAPROPERTYVALUE_KIND_MANIFESTATION_ACUTE = "ACUTE";
	
	public static String DATAPROPERTYVALUE_CALCULATED_METHOD_DEFAULT = "MIN";
}
