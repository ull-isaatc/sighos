/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import es.ull.iis.simulation.hta.Named;

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
	public static enum Class implements Named {
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

		private final String name;
		private Class(String name) {
			this.name = name;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * Names of the data properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataProperty implements Named {
		HAS_AGE("#hasAge"),
		HAS_AGE_OF_FIRST_PRESENTATION("#hasAgeOfFirstPresentation"),
		HAS_CALCULATION_METHOD("#hasCalculationMethod"),
		HAS_CONDITION("#hasCondition"),
		HAS_COUNTRY("#hasCountry"),
		HAS_DESCRIPTION("#hasDescription"),
		HAS_DEVELOPMENT_KIND("#hasDevelopmentKind"),
		HAS_DOSE("#hasDose"),
		HAS_DURATION("#hasDuration"),
		HAS_ENDAGE("#hasEndAge"),
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
		HAS_REFTOOMIM("#hasRefToOMIM"),
		HAS_REFTOORDO("#hasRefToORDO"),
		HAS_REFTOSNOMED("#hasRefToSNOMED"),
		HAS_RELATIVERISK("#hasRelativeRisk"),
		HAS_RELATIVERISKMODIFICATION("#hasRelativeRiskModification"),
		HAS_SENSITIVITY("#hasSensitivity"),
		HAS_SIZE("#hasSize"),
		HAS_SOURCE("#hasSource"),
		HAS_SPECIFICITY("#hasSpecificity"),
		HAS_STAGE("#hasStage"),
		HAS_STEPORDER("#hasStepOrder"),
		HAS_TEMPORALBEHAVIOR("#hasTemporalBehavior"),
		HAS_TEMPORARYTHRESHOLD("#hasTemporaryThreshold"),
		HAS_TIMETO("#hasTimeTo"),
		HAS_UTILITYKIND("#hasUtilityKind"),
		HAS_VALUE("#hasValue"),
		HAS_YEAR("#hasYear");
		
		private final String name;
		private DataProperty(String name) {
			this.name = name;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
	

	/**
	 * Names of the object properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum ObjectProperty implements Named {
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
		REQUIRES_PREVIOUS_MANIFESTATION("#requiresPreviousManifestation");
		
		private final String name;
		private ObjectProperty(String name) {
			this.name = name;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
}
