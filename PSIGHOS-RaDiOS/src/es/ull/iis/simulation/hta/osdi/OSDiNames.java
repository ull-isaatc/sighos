/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
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
		MANIFESTATION_PATHWAY_MODIFICATION("#ManifestationPathwayModification"),
		DEVELOPMENT_MODIFICATION("#DevelopmentModification"),
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

	public static final Map<String, OSDiNames.DataProperty> DATA_PROPERTY_MAP = new HashMap<String, OSDiNames.DataProperty>();  
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
		HAS_DATA_PROPERTY_MODIFIED("#hasDataPropertyModified"),
		HAS_DESCRIPTION("#hasDescription"),
		HAS_DEVELOPMENT_KIND("#hasDevelopmentKind"),
		HAS_DOSE("#hasDose"),
		HAS_DURATION("#hasDuration"),
		HAS_END_AGE("#hasEndAge"),
		HAS_EPIDEMIOLOGICAL_PARAMETER_KIND("#hasEpidemiologicalParameterKind", EnumSet.of(DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_BIRTH_PREVALENCE, DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_INCIDENCE, DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_PREVALENCE)),
		HAS_FEMALE_PROPORTION("#hasFemaleProportion"),
		HAS_FREQUENCY("#hasFrequency"),
		HAS_GEOGRAPHICAL_CONTEXT("#hasGeographicalContext"),
		HAS_HOURS_INTERVAL("#hasHoursInterval"),
		HAS_IDENTIFIER("#hasIdentifier"),
		HAS_INTERVENTION_KIND("#hasInterventionKind", EnumSet.of(DataPropertyRange.INTERVENTION_KIND_NOSCREENING, DataPropertyRange.INTERVENTION_KIND_SCREENING)),
		HAS_LIFE_EXPECTANCY("#hasLifeExpectancy"),
		HAS_MANIFESTATION_KIND("#hasManifestationKind", EnumSet.of(DataPropertyRange.MANIFESTATION_KIND_ACUTE, DataPropertyRange.MANIFESTATION_KIND_CHRONIC)),
		HAS_MODIFICATION_FOR_ALL_PARAMS("#hasModificationForAllParams"),
		HAS_MODIFICATION_KIND("#hasModificationKind", EnumSet.of(DataPropertyRange.MODIFICATION_KIND_DIFF, DataPropertyRange.MODIFICATION_KIND_RR, DataPropertyRange.MODIFICATION_KIND_SET)),
		HAS_MORTALITY_FACTOR("#hasMortalityFactor"),
		HAS_NAME("#hasName"),
		HAS_NATURE_OF_EPIDEMIOLOGICAL_PARAMETER_ESTIMATE("#hasNatureOfEpidemiologicalParameterEstimate", EnumSet.of(DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_NATURE_APPARENT, DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_NATURE_TRUE)),
		HAS_ONSET_AGE("#hasOnsetAge"),
		HAS_PERCENTAGE_NEXT("#hasPercentageNext"),
		HAS_PERCENTAGE_TREATED("#hasPercentageTreated"),
		HAS_PROBABILITY("#hasProbability"),
		HAS_PROBABILITY_OF_DIAGNOSIS("#hasProbabilityOfDiagnosis"),
		HAS_RANGE("#hasRange"),
		HAS_REF_TO_DO("#hasRefToDO"),
		HAS_REF_TO_GARD("#hasRefToGARD"),
		HAS_REF_TO_ICD("#hasRefToICD"),
		HAS_REF_TO_OMIM("#hasRefToOMIM"),
		HAS_REF_TO_ORDO("#hasRefToORDO"),
		HAS_REF_TO_SNOMED("#hasRefToSNOMED"),
		HAS_RELATIVE_RISK("#hasRelativeRisk"),
		HAS_SENSITIVITY("#hasSensitivity"),
		HAS_SIZE("#hasSize"),
		HAS_SOURCE("#hasSource"),
		HAS_SPECIFICITY("#hasSpecificity"),
		HAS_STEPORDER("#hasStepOrder"),
		HAS_TEMPORAL_BEHAVIOR("#hasTemporalBehavior", EnumSet.of(DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL, DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME)),
		HAS_TEMPORARY_THRESHOLD("#hasTemporaryThreshold"),
		HAS_TIME_TO("#hasTimeTo"),
		HAS_UTILITY_KIND("#hasUtilityKind", EnumSet.of(DataPropertyRange.UTILITY_KIND_DISUTILITY, DataPropertyRange.UTILITY_KIND_UTILITY)),
		HAS_VALUE("#hasValue"),
		HAS_YEAR("#hasYear");
		
		private final String description;
		private final EnumSet<DataPropertyRange> range;
		private DataProperty(String description) {
			this(description, EnumSet.noneOf(DataPropertyRange.class));
		}
		private DataProperty(String description, EnumSet<DataPropertyRange> range) {
			this.description = description;
			this.range = range;
			DATA_PROPERTY_MAP.put(description, this);
		}
		/**
		 * @return the description
		 */
		@Override
		public String getDescription() {
			return description;
		}
		
		public EnumSet<DataPropertyRange> getRange() {
			return range;
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
		HAS_DRUG("#hasDrug"),
		HAS_FOLLOWUP("#hasFollowUp"),
		HAS_FOLLOWUP_STRATEGY("#hasFollowUpStrategy"),
		HAS_GUIDELINE("#hasGuideline"),
		HAS_INTERVENTION("#hasIntervention"),
		HAS_MANIFESTATION("#hasManifestation"),
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
		IS_PATHWAY_TO("#isPathwayTo"),
		IS_PARAMETER_OF("#isParameterOf"),
		IS_PARAMETER_OF_DISEASE("#isParameterOfDisease"),
		IS_PARAMETER_OF_MANIFESTATION("#isParameterOfManifestation"),
		IS_PARAMETER_OF_POPULATION("#isParameterOfPopulation"),
		IS_STEP_OF("#isStepOf"),
		IS_STRATEGY_OF("#isStrategyOf"),
		IS_SUBPOPULATION_OF("#isSubpopulationOf"),
		MODIFIES("#modifies"),
		MODIFIES_DEVELOPMENT("#modifiesDevelopment"),
		MODIFIES_MANIFESTATION("#modifiesManifestation"),
		MODIFIES_MANIFESTATION_PATHWAY("#modifiesManifestationPathway"),
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
		INTERVENTION_KIND_SCREENING("SCREENING"),
		INTERVENTION_KIND_NOSCREENING("NO_SCREENING"),
		TEMPORAL_BEHAVIOR_ANNUAL("ANNUAL"),
		TEMPORAL_BEHAVIOR_ONETIME("ONETIME"),
		UTILITY_KIND_UTILITY("UTILITY"),
		UTILITY_KIND_DISUTILITY("DISUTILITY"),
		MANIFESTATION_KIND_CHRONIC("CHRONIC"),
		MANIFESTATION_KIND_ACUTE("ACUTE"),
		EPIDEMIOLOGICAL_PARAMETER_KIND_BIRTH_PREVALENCE("BIRTH PREVALENCE"),
		EPIDEMIOLOGICAL_PARAMETER_KIND_INCIDENCE("INCIDENCE"),
		EPIDEMIOLOGICAL_PARAMETER_KIND_PREVALENCE("PREVALENCE"),
		EPIDEMIOLOGICAL_PARAMETER_NATURE_APPARENT("APPARENT"),
		EPIDEMIOLOGICAL_PARAMETER_NATURE_TRUE("TRUE"),
		MODIFICATION_KIND_DIFF("DIFF"),
		MODIFICATION_KIND_RR("RR"),
		MODIFICATION_KIND_SET("SET");
		
		private final String description;
		private DataPropertyRange(String description) {
			this.description = description;
		}
		@Override
		public String getDescription() {
			return description;
		}
	}

	public static String getSource(String instanceName) {
		return OwlHelper.getDataPropertyValue(instanceName, DataProperty.HAS_SOURCE.getDescription(), "Unknown");
	}
}
