/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public interface OSDiNames {

	/**
	 * Names of the classes defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum Class implements Describable {
		CLINICAL_DIAGNOSIS("#ClinicalDiagnosis"),	
		DEVELOPMENT("#Development"),
		DISEASE("#Disease"),
		DRUG("#Drug"),
		FOLLOWUP("#FollowUp"),
		GUIDELINE("#Guideline"),
		INTERVENTION("#Intervention"),
		MANIFESTATION("#Manifestation"),
		MANIFESTATION_PATHWAY("#ManifestationPathway"),
		MODIFICATION("#Modification"),
		MANIFESTATION_MODIFICATION("#ManifestationModification"),
		DEVELOPMENT_MODIFICACION("#DevelopmentModificacion"),
		PARAMETER("#Parameter"),
		EPIDEMIOLOGICAL_PARAMETER("#EpidemiologicalParameter"),
		COST("#Cost"),
		UTILITY("#Utility"),
		POPULATION("#Population"),
		SCREENING("#Screening"),
		STRATEGY("#Strategy"),
		STRATEGY_STEP("#StrategyStep"),
		TREATMENT("#Treatment");

		private final String description;
		private Class(String description) {
			this.description = description;
		}
		/**
		 * @return the description
		 */
		@Override
		public String getDescription() {
			return description;
		}
	}

	/**
	 * Names of the data properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataProperty implements Describable {
		HAS_AGE("#hasAge"),
		HAS_AGE_OF_FIRST_PRESENTATION("#hasAgeOfFirstPresentation"),
		HAS_CALCULATION_METHOD("#hasCalculationMethod"),
		HAS_CONDITION("#hasCondition"),
		HAS_COUNTRY("#hasCountry"),
		HAS_DESCRIPTION("#hasDescription"),
		HAS_DEVELOPMENT_KIND("#hasDevelopmentKind"),
		HAS_DOSE("#hasDose"),
		HAS_DURATION("#hasDuration"),
		HAS_END_AGE("#hasEndAge"),
		HAS_EPIDEMIOLOGICAL_PARAMETER_KIND("#hasEpidemiologicalParameterKind"),
		HAS_FEMALE_PROPORTION("#hasFemaleProportion"),
		HAS_FREQUENCY("#hasFrequency"),
		HAS_FREQUENCY_MODIFICATION("#hasFrequencyModification"),
		HAS_GEOGRAPHICAL_CONTEXT("#hasGeographicalContext"),
		HAS_HOURS_INTERVAL("#hasHoursInterval"),
		HAS_IDENTIFIER("#hasIdentifier"),
		HAS_INTERVENTION_KIND("#hasInterventionKind"),
		HAS_LIFE_EXPECTANCY("#hasLifeExpectancy"),
		HAS_LIFE_EXPECTANCY_MODIFICATION("#hasLifeExpectancyModification"),
		HAS_MANIFESTATION_KIND("#hasManifestationKind"),
		HAS_MODIFICATION_FOR_ALL_PARAMS("#hasModificationForAllParams"),
		HAS_MORTALITY_FACTOR("#hasMortalityFactor"),
		HAS_MORTALITY_FACTOR_MODIFICATION("#hasMortalityFactorModification"),
		HAS_NAME("#hasName"),
		HAS_NATURE_OF_EPIDEMIOLOGICAL_PARAMETER_ESTIMATE("#hasNatureOfEpidemiologicalParameterEstimate"),
		HAS_ONSET_AGE("#hasOnsetAge"),
		HAS_PERCENTAGE_NEXT("#hasPercentageNext"),
		HAS_PERCENTAGE_TREATED("#hasPercentageTreated"),
		HAS_PROBABILITY("#hasProbability"),
		HAS_PROBABILITY_MODIFICATION("#hasProbabilityModification"),
		HAS_PROBABILITY_OF_DIAGNOSIS("#hasProbabilityOfDiagnosis"),
		HAS_PROBABILITY_OF_DIAGNOSIS_MODIFICATION("#hasProbabilityOfDiagnosisModification"),
		HAS_RANGE("#hasRange"),
		HAS_REF_TO_DO("#hasRefToDO"),
		HAS_REF_TO_GARD("#hasRefToGARD"),
		HAS_REF_TO_ICD("#hasRefToICD"),
		HAS_REF_TO_OMIM("#hasRefToOMIM"),
		HAS_REF_TO_ORDO("#hasRefToORDO"),
		HAS_REF_TO_SNOMED("#hasRefToSNOMED"),
		HAS_RELATIVE_RISK("#hasRelativeRisk"),
		HAS_RELATIVE_RISK_MODIFICATION("#hasRelativeRiskModification"),
		HAS_SENSITIVITY("#hasSensitivity"),
		HAS_SIZE("#hasSize"),
		HAS_SOURCE("#hasSource"),
		HAS_SPECIFICITY("#hasSpecificity"),
		HAS_STEPORDER("#hasStepOrder"),
		HAS_TEMPORAL_BEHAVIOR("#hasTemporalBehavior"),
		HAS_TEMPORARY_THRESHOLD("#hasTemporaryThreshold"),
		HAS_TIME_TO("#hasTimeTo"),
		HAS_UTILITY_KIND("#hasUtilityKind"),
		HAS_VALUE("#hasValue"),
		HAS_YEAR("#hasYear");
		
		private final String description;
		private DataProperty(String description) {
			this.description = description;
		}
		/**
		 * @return the description
		 */
		@Override
		public String getDescription() {
			return description;
		}
	}
	

	/**
	 * Names of the object properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum ObjectProperty implements Describable {
		EXCLUDES("#excludes"),
		HAS_CLINICAL_DIAGNOSIS("#hasClinicalDiagnosis"),
		HAS_CLINICAL_DIAGNOSIS_STRATEGY("#hasClinicalDiagnosisStrategy"),
		HAS_COST("#hasCost"),
		HAS_DEVELOPMENT("#hasDevelopment"),
		HAS_DEVELOPMENT_MODIFICATION("#hasDevelopmentModification"),
		HAS_DRUG("#hasDrug"),
		HAS_FOLLOWUP("#hasFollowUp"),
		HAS_FOLLOWUP_STRATEGY("#hasFollowUpStrategy"),
		HAS_GUIDELINE("#hasGuideline"),
		HAS_INTERVENTION("#hasIntervention"),
		HAS_MANIFESTATION("#hasManifestation"),
		HAS_MANIFESTATION_MODIFICATION("#hasManifestationModification"),
		HAS_NATURAL_DEVELOPMENT("#hasNaturalDevelopment"),
		HAS_PARAMETER("#hasParameter"),
		HAS_PATHWAY("#hasPathway"),
		HAS_SCREENING("#hasScreening"),
		HAS_SCREENING_STRATEGY("#hasScreeningStrategy"),
		HAS_STEP("#hasStep"),
		HAS_STRATEGY("#hasStrategy"),
		HAS_SUBPOPULATION("#hasSubpopulation"),
		HAS_TREATMENT("#hasTreatment"),
		HAS_TREATMENT_STRATEGY("#hasTreatmentStrategy"),
		HAS_UTILITY("#hasUtility"),
		INVOLVES_MODIFICATION("#involvesModification"),
		IS_MODIFIED_BY("#isModifiedBy"),
		IS_PARAMETER_OF("#isParameterOf"),
		IS_PARAMETER_OF_DISEASE("#isParameterOfDisease"),
		IS_PARAMETER_OF_MANIFESTATION("#isParameterOfManifestation"),
		IS_PARAMETER_OF_POPULATION("#isParameterOfPopulation"),
		IS_STEP_OF("#isStepOf"),
		IS_STRATEGY_OF("#isStrategyOf"),
		IS_SUBPOPULATION_OF("#isSubpopulationOf"),
		MODIFIES("#modifies"),
		REQUIRES_DEVELOPMENT("#requiresDevelopment"),
		REQUIRES_PREVIOUS_MANIFESTATION("#requiresPreviousManifestation");
		
		private final String description;
		private ObjectProperty(String description) {
			this.description = description;
		}
		@Override
		public String getDescription() {
			return description;
		}
	}

	/**
	 * Names of the ranges defined for data properties in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataPropertyRange implements Describable {
		KIND_INTERVENTION_SCREENING_VALUE("SCREENING"),
		KIND_INTERVENTION_NOSCREENING_VALUE("NO_SCREENING"),
		TEMPORAL_BEHAVIOR_ANNUAL_VALUE("ANNUAL"),
		TEMPORAL_BEHAVIOR_ONETIME_VALUE("ONETIME"),
		KIND_UTILITY_UTILITY("UTILITY"),
		KIND_UTILITY_DISUTILITY("DISUTILITY"),
		KIND_MANIFESTATION_CHRONIC("CHRONIC"),
		KIND_MANIFESTATION_ACUTE("ACUTE");
		
		private final String description;
		private DataPropertyRange(String description) {
			this.description = description;
		}
		@Override
		public String getDescription() {
			return description;
		}
	}

}
