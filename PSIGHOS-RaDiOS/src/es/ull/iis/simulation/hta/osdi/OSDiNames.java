/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public interface OSDiNames {
	String STR_SOURCE_UNKNOWN = "Not specified";
	
	/**
	 * Names of the classes defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum Class implements Describable {
		COST("#Cost"),
		DEVELOPMENT("#Development"),
		DIAGNOSIS_STRATEGY("#DiagnosisStrategy"),	
		DIAGNOSIS_TEST("#DiagnosisTest"),	
		DISEASE("#Disease"),
		DRUG("#Drug"),
		EPIDEMIOLOGICAL_PARAMETER("#EpidemiologicalParameter"),
		FOLLOW_UP_STRATEGY("#FollowUpStrategy"),
		FOLLOW_UP_TEST("#FollowUpTest"),
		GUIDELINE("#Guideline"),
		GUIDELINE_RANGE("#GuidelineRange"),
		HEALTH_TECHNOLOGY("#HealthTechnology"),
		INDIVIDUAL_PARAMETER("#IndividualParameter"),	
		INTERVENTION("#Intervention"),
		LINE_OF_THERAPY("#LineOfTherapy"),
		MANIFESTATION("#Manifestation"),
		MANIFESTATION_PATHWAY("#ManifestationPathway"),
		MODIFICATION("#Modification"),
		PARAMETER("#Parameter"),
		POPULATION("#Population"),
		SCREENING_STRATEGY("#ScreeningStrategy"),
		SCREENING_TEST("#ScreeningTest"),
		STRATEGY("#Strategy"),
		TREATMENT("#Treatment"),
		UTILITY("#Utility");

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
		public List<String> getDescendantsOf(OwlHelper helper, String instanceName) {
			return helper.getChildsByClassName(instanceName, description);
		}
	}

	public static final Map<String, OSDiNames.DataProperty> DATA_PROPERTY_MAP = new HashMap<String, OSDiNames.DataProperty>();  
	/**
	 * Names of the data properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataProperty implements Describable {
		FAILS_IF("#failsIf"),
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
		HAS_INTERVENTION_KIND("#hasInterventionKind", EnumSet.of(DataPropertyRange.INTERVENTION_KIND_DIAGNOSIS, DataPropertyRange.INTERVENTION_KIND_GENERAL, DataPropertyRange.INTERVENTION_KIND_SCREENING)),
		HAS_LIFE_EXPECTANCY("#hasLifeExpectancy"),
		HAS_MANIFESTATION_KIND("#hasManifestationKind", EnumSet.of(DataPropertyRange.MANIFESTATION_KIND_ACUTE, DataPropertyRange.MANIFESTATION_KIND_CHRONIC)),
		HAS_MIN_AGE("#hasMinAge"),
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
		HAS_PROPORTION("#hasProportion"),
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
		
		public String getValue(OwlHelper helper, String instanceName) {
			return helper.getDataPropertyValue(instanceName, description);
		}
		
		public String getValue(OwlHelper helper, String instanceName, String defaultValue) {
			return helper.getDataPropertyValue(instanceName, description, defaultValue);
		}
		
		public List<String> getValues(OwlHelper helper, String instanceName) {
			return helper.getDataPropertyValues(instanceName, description);
		}
	}
	

	/**
	 * Names of the object properties defined in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum ObjectProperty implements Describable {
		DEFINES_INDIVIDUAL_PARAMETER_VALUE("#definesIndividualParameterValue"),
		EXCLUDES_MANIFESTATION("#excludesManifestation"),
		HAS_DEVELOPMENT("#hasDevelopment"),
		HAS_DIAGNOSIS_COST("#hasDiagnosisCost"),
		HAS_DIAGNOSIS_STRATEGY("#hasDiagnosisStrategy"),
		HAS_COST("#hasCost"),
		HAS_FOLLOWUP_COST("#hasFollowUpCost"),
		HAS_FOLLOWUP_STRATEGY("#hasFollowUpStrategy"),
		HAS_GUIDELINE("#hasGuideline"),
		HAS_GUIDELINE_RANGE("#hasGuidelineRange"),
		HAS_INTERVENTION("#hasIntervention"),
		HAS_LINE_OF_THERAPY("#hasLineOfTherapy"),
		HAS_MANIFESTATION("#hasManifestation"),
		HAS_NATURAL_DEVELOPMENT("#hasNaturalDevelopment"),
		HAS_PARAMETER("#hasParameter"),
		HAS_PATHWAY("#hasPathway"),
		HAS_SCREENING_COST("#hasScreeningCost"),
		HAS_SCREENING_STRATEGY("#hasScreeningStrategy"),
		HAS_STRATEGY("#hasStrategy"),
		HAS_SUBPOPULATION("#hasSubpopulation"),
		HAS_TREATMENT_COST("#hasTreatmentCost"),
		HAS_TREATMENT_STRATEGY("#hasTreatmentStrategy"),
		HAS_UTILITY("#hasUtility"),
		INVOLVES_MODIFICATION("#involvesModification"),
		IS_MODIFIED_BY("#isModifiedBy"),
		IS_PARAMETER_OF("#isParameterOf"),
		IS_PARAMETER_OF_DISEASE("#isParameterOfDisease"),
		IS_PARAMETER_OF_MANIFESTATION("#isParameterOfManifestation"),
		IS_PARAMETER_OF_POPULATION("#isParameterOfPopulation"),
		IS_PATHWAY_TO("#isPathwayTo"),
		IS_SUBPOPULATION_OF("#isSubpopulationOf"),
		IS_VALUE_OF_INDIVIDUAL_PARAMETER("#isValueOfIndividualParameter"),
		MODIFIES("#modifies"),
		MODIFIES_DEVELOPMENT("#modifiesDevelopment"),
		MODIFIES_INDIVIDUAL_PARAMETER("#modifiesIndividualParameter"),
		MODIFIES_MANIFESTATION("#modifiesManifestation"),
		MODIFIES_MANIFESTATION_PATHWAY("#modifiesManifestationPathway"),
		REQUIRES_DEVELOPMENT("#requiresDevelopment"),
		REQUIRES_PREVIOUS_MANIFESTATION("#requiresPreviousManifestation"),
		USES_DIAGNOSIS_TEST("#usesDiagnosisTest"),
		USES_DRUG("#usesDrug"),
		USES_FOLLOW_UP_TEST("#usesFollowUpTest"),
		USES_HEALTH_TECHNOLOGY("#usesHealthTechnology"),
		USES_SCREENING_TEST("#usesScreeningTest"),
		USES_TREATMENT("#hasTreatment");
		
		private final String description;
		private ObjectProperty(String description) {
			this.description = description;
		}
		@Override
		public String getDescription() {
			return description;
		}
		
		/**
		 * Returns the object associated to another object by means of the specified object property. If there are more than 
		 * one, returns the first one.  
		 * @param instanceName Name of the original instance
		 * @return the object associated to another object by means of the specified object property
		 */
		public String getValue(OwlHelper helper, String instanceName) {
			return helper.getObjectPropertyByName(instanceName, description);
		}
		
		/**
		 * Returns the list of objects associated to another object by means of the specified object property  
		 * @param instanceName Name of the original instance
		 * @return the list of objects associated to another object by means of the specified object property
		 */
		public List<String> getValues(OwlHelper helper, String instanceName) {
			return helper.getObjectPropertiesByName(instanceName, description);
		}
	}

	/**
	 * Names of the ranges defined for data properties in the ontology
	 * @author Iván Castilla
	 *
	 */
	public static enum DataPropertyRange implements Describable {
		INTERVENTION_KIND_DIAGNOSIS("DIAGNOSIS"),
		INTERVENTION_KIND_GENERAL("GENERAL"),
		INTERVENTION_KIND_SCREENING("SCREENING"),
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

	public static String getSource(OwlHelper helper, String instanceName) {
		return DataProperty.HAS_SOURCE.getValue(helper, instanceName, "Unknown");
	}
}
