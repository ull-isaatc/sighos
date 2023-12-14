/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import es.ull.iis.ontology.OWLOntologyWrapper;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OSDiWrapper extends OWLOntologyWrapper {
	public static final boolean ENABLE_WARNINGS = true;  
	
	private final static String PREFIX = "http://www.ull.es/iis/simulation/ontologies/disease-simulation#";
	private String workingModelInstance;
	private final Set<String> modelItems;
	private final String instancePrefix;
	private final Map<String, ParameterWrapper> parameterWrappers;
	
	private final static String STR_SEP = "_";
	public enum InstanceIRI {
		/** In general, attributes do not use the prefix of the working model, since they should be defined for every disease and model */
		ATTRIBUTE("Attribute_", ""),
		DISEASE("", ""),
		EXPRESSION("", "_Expression"),
		INTERVENTION("Intervention_", ""),
		MANIFESTATION("Manif_", ""),
		MANIFESTATION_GROUP("Group_Manif_", ""),
		MANIFESTATION_PATHWAY("Manif_Pathway_", ""),
		PARAMETER("", ""),
		POPULATION("Population_", ""),
		STAGE("Stage_", ""),
		STAGE_PATHWAY("Stage_Pathway_", ""),
		PARAM_ANNUAL_COST("", "_AC"),
		PARAM_DEATH_PROBABILITY("", "_ProbDeath"),
		PARAM_INCREASED_MORTALITY_RATE("", "_IMR"),
		PARAM_INCIDENCE("", "_Incidence"),
		PARAM_ONE_TIME_COST("", "_TC"),
		PARAM_PREVALENCE("", "_Prevalence"),
		PARAM_PROPORTION("", "_Proportion"),
		PARAM_RELATIVE_RISK("", "_RR"),
		PARAM_UTILITY("", "_U"),
		UNCERTAINTY_HETEREOGENEITY("", "_Heterogeneity"),
		UNCERTAINTY_L95CI("", "_L95CI"),
		UNCERTAINTY_PARAM("", "_ParamUncertainty"),
		UNCERTAINTY_STOCHASTIC("", "_StochasticUncertainty"),
		UNCERTAINTY_U95CI("", "_U95CI");
		private final String prefix;
		private final String suffix;
		private InstanceIRI(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}
		public String getIRI(String instanceName) {
			return this.getIRI(instanceName, true);
		}
		public String getIRI(String instanceName, InstanceIRI extraPrefix, InstanceIRI extraSuffix) {
			return this.getIRI(instanceName, extraPrefix, extraSuffix, true);
		}
		public String getIRI(String instanceName, boolean useInstancePrefix) {
			return (useInstancePrefix ? currentWrapper.getInstancePrefix() : "") + prefix + instanceName + suffix;
		}
		public String getIRI(String instanceName, InstanceIRI extraPrefix, InstanceIRI extraSuffix, boolean useInstancePrefix) {
			return (useInstancePrefix ? currentWrapper.getInstancePrefix() : "") + prefix + (extraPrefix != null ? extraPrefix.prefix : "") + instanceName + (extraSuffix != null ? extraSuffix.suffix : "") + suffix;
		}
	}
	
	/**
	 * A list of the different types of models that can be defined in OSDi
	 * @author Iván Castilla
	 *
	 */
	public enum ModelType {
		DES(Clazz.DISCRETE_EVENT_SIMULATION_MODEL),
		AGENT(Clazz.AGENT_BASED_MODEL),
		MARKOV(Clazz.MARKOV_MODEL),
		DECISION_TREE(Clazz.DECISION_TREE_MODEL);
		private final Clazz clazz;
		private ModelType(Clazz clazz) {
			this.clazz = clazz;
		}
		/**
		 * Returns the associated class in the ontology
		 * @return The associated class in the ontology
		 */
		public Clazz getClazz() {
			return clazz;
		}
	}

	public enum InterventionType {
		DIAGNOSIS(Clazz.DIAGNOSIS_INTERVENTION),
		SCREENING(Clazz.SCREENING_INTERVENTION),
		THERAPEUTIC(Clazz.THERAPEUTIC_INTERVENTION);
		private final Clazz clazz;
		private InterventionType(Clazz clazz) {
			this.clazz = clazz;
		}
		/**
		 * Returns the associated class in the ontology
		 * @return The associated class in the ontology
		 */
		public Clazz getClazz() {
			return clazz;
		}
	}
	
	public enum DiseaseProgressionType {
		ACUTE_MANIFESTATION(Clazz.ACUTE_MANIFESTATION),
		CHRONIC_MANIFESTATION(Clazz.CHRONIC_MANIFESTATION),
		STAGE(Clazz.STAGE);
		private final Clazz clazz;
		private DiseaseProgressionType(Clazz clazz) {
			this.clazz = clazz;
		}
		/**
		 * @return the clazz
		 */
		public Clazz getClazz() {
			return clazz;
		}
	}
	
	/**
	 * The type of utilities that can be handled
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public enum UtilityType {
		UTILITY(DataItemType.DI_UTILITY),
		DISUTILITY(DataItemType.DI_DISUTILITY);
		private final DataItemType type;
		
		private UtilityType(DataItemType type) {
			this.type = type;
		}

		/**
		 * @return the type
		 */
		public DataItemType getType() {
			return type;
		}
	}

	/**
	 * Temporal behavior for costs and utilities in the ontology
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public enum TemporalBehavior {
		ANNUAL("ANNUAL"),
		ONETIME("ONETIME"),
		NOT_SPECIFIED("NOT SPECIFIED");
		
		private final String shortName;
		private TemporalBehavior(String shortName) {
			this.shortName = shortName;
		}
		/**
		 * @return the shortName
		 */
		public String getShortName() {
			return shortName;
		}
	}
	
	/**
	 * @author Iván Castilla
	 * TODO: Process parameters when expressed in different ways. E.g. gamma parameters may be average and standard deviation
	 */
	public enum ProbabilityDistributionExpression {
		NORMAL(Clazz.NORMAL_DISTRIBUTION_EXPRESSION, "NormalVariate", 2),
		UNIFORM(Clazz.UNIFORM_DISTRIBUTION_EXPRESSION, "UniformVariate", 2) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LOWER_LIMIT_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_UPPER_LIMIT_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		BETA(Clazz.BETA_DISTRIBUTION_EXPRESSION, "BetaVariate", 2) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_BETA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		GAMMA(Clazz.GAMMA_DISTRIBUTION_EXPRESSION, "GammaVariate", 2) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		EXPONENTIAL(Clazz.EXPONENTIAL_DISTRIBUTION_EXPRESSION, "ExponentialVariate", 1) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		POISSON(Clazz.POISSON_DISTRIBUTION_EXPRESSION, "PoissonVariate", 1) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		BERNOULLI(Clazz.BERNOULLI_DISTRIBUTION_EXPRESSION, "BernoulliVariate", 1) {
			public String[] getParameters(String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_PROBABILITY_PARAMETER.getValue(instanceId, "0")};				
			}			
		};
		private final Clazz clazz;
		private final String variateName;
		private final int nParameters;
		
		private ProbabilityDistributionExpression(Clazz clazz, String variateName, int nParameters) {
			this.clazz = clazz;
			this.variateName = variateName;
			this.nParameters = nParameters;
		}
		
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiWrapper.DataProperty.HAS_AVERAGE_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_STANDARD_DEVIATION_PARAMETER.getValue(instanceId, "0")};
		}
		
		
		/**
		 * @return the variateName
		 */
		public String getVariateName() {
			return variateName;
		}

		/**
		 * @return the clazz
		 */
		public Clazz getClazz() {
			return clazz;
		}

		/**
		 * @return the nParameters
		 */
		public int getnParameters() {
			return nParameters;
		}
		

	}
	
	public enum Clazz {
		ACUTE_MANIFESTATION("AcuteManifestation"),
		AD_HOC_EXPRESSION("AdHocExpression"),
		AGENT_BASED_MODEL("AgentBasedModel"),
		ATTRIBUTE("Attribute"),
		ATTRIBUTE_VALUE("AttributeValue"),
		BERNOULLI_DISTRIBUTION_EXPRESSION("BernoulliDistributionExpression"),
		BETA_DISTRIBUTION_EXPRESSION("BetaDistributionExpression"),
		BIRTH_PREVALENCE("BirthPrevalence"),
		CHRONIC_MANIFESTATION("ChronicManifestation"),
		COST("Cost"),
		CURRENCY("Currency"),
		DATA_ITEM_TYPE("DataItemType"),
		DECISION_TREE_MODEL("DecisionTreeModel"),
		DETECTION_INTERVENTION("DetectionIntervention"),
		DETECTION_STRATEGY("DetectionStrategy"),
		DEVELOPMENT("Development"),
		DEVELOPMENT_PATHWAY("DevelopmentPathway"),
		DIAGNOSIS_INTERVENTION("DiagnosisIntervention"),
		DIAGNOSIS_STRATEGY("DiagnosisStrategy"),
		DISCRETE_EVENT_SIMULATION_MODEL("DiscreteEventSimulationModel"),
		DISEASE("Disease"),
		DISEASE_PATHWAY("DiseasePathway"),
		DISEASE_PROGRESSION("DiseaseProgression"),
		DRUG("Drug"),
		EPIDEMIOLOGICAL_PARAMETER("EpidemiologicalParameter"),
		EXPONENTIAL_DISTRIBUTION_EXPRESSION("ExponentialDistributionExpression"),
		EXPRESSION("Expression"),
		FOLLOW_UP_STRATEGY("FollowUpStrategy"),
		FOLLOW_UP_TEST("FollowUpTest"),
		GAMMA_DISTRIBUTION_EXPRESSION("GammaDistributionExpression"),
		GROUP("Group"),
		GROUPABLE_MODEL_ITEM("GroupableModelItem"),
		GUIDELINE("Guideline"),
		HEALTH_TECHNOLOGY("HealthTechnology"),
		INCIDENCE("Incidence"),
		INTERVENTION("Intervention"),
		LINE_OF_THERAPY("LineOfTherapy"),
		MANIFESTATION("Manifestation"),
		MANIFESTATION_PATHWAY("ManifestationPathway"),
		MARKOV_MODEL("MarkovModel"),
		MEASURED_DATA_ITEM_TYPE("MeasuredDataItemType"),
		MODEL("Model"),
		MODEL_ITEM("ModelItem"),
		NORMAL_DISTRIBUTION_EXPRESSION("NormalDistributionExpression"),
		PARAMETER("Parameter"),
		PATHWAY("Pathway"),
		POISSON_DISTRIBUTION_EXPRESSION("PoissonDistributionExpression"),
		POPULATION("Population"),
		PREVALENCE("Prevalence"),
		PROBABILITY_DISTRIBUTION_EXPRESSION("ProbabilityDistributionExpression"),
		PROPORTION_WITHIN_GROUP("ProportionWithinGroup"),
		RARE_DISEASE("RareDisease"),
		SCREENING_INTERVENTION("ScreeningIntervention"),
		SCREENING_STRATEGY("ScreeningStrategy"),
		STAGE("Stage"),
		STAGE_PATHWAY("StagePathway"),
		STRATEGY("Strategy"),
		THERAPEUTIC_INTERVENTION("TherapeuticIntervention"),
		TREATMENT("Treatment"),
		UNIFORM_DISTRIBUTION_EXPRESSION("UniformDistributionExpression"),
		UTILITY("Utility"),
		VALUABLE("Valuable");
		/** The short name that is used as IRI of this class in the ontology */
		private final String shortName;
		private Clazz(String shortName) {
			this.shortName = shortName;
		}
		
		/**
		 * Returns a short name that is used as IRI of this class in the ontology.
		 * @return the short name that is used as IRI of this class in the ontology.
		 */
		public String getShortName() {
			return shortName;
		}

		/**
		 * Creates a new individual that belongs to this class.
		 * @param individualIRI Unique identifier for the individual.
		 */
		public void add(String individualIRI) {
			currentWrapper.addIndividual(shortName, individualIRI);
		}		
		
		/**
		 * Returns the individuals defined for this class. 
		 * @return the individuals defined for this class.
		 */
		public Set<String> getIndividuals() {
			return getIndividuals(false);
		}

		/**
		 * Returns the individuals defined for this class. If restrictToWorkingModel is enabled, only returns those individuals that belong to the working model
		 * @param restrictToWorkingModel Restricts the results to those individuals that belong to the working model 
		 * @return the individuals defined for this class.
		 */
		public Set<String> getIndividuals(boolean restrictToWorkingModel) {
			final Set<String> results = currentWrapper.getIndividuals(shortName);
			if (restrictToWorkingModel)
				results.retainAll(currentWrapper.modelItems);
			return results;
		}
		
		/**
		 * Returns true if the specified individual is an instance of this class (or any of its subclasses)
		 * @param individualIRI The IRI of an individual in the ontology
		 * @return true if the specified individual is an instance of this class (or any of its subclasses)
		 */
		public boolean containsIntance(String individualIRI) {
			return currentWrapper.isInstanceOf(individualIRI, shortName);
		}
	}

	public enum DataProperty {
		FAILS_IF("failsIf"),
		HAS_ALFA_PARAMETER("hasAlfaParameter"),
		HAS_AUTHOR("hasAuthor"),
		HAS_AVERAGE_PARAMETER("hasAverageParameter"),
		HAS_BETA_PARAMETER("hasBetaParameter"),
		HAS_CALCULATION_METHOD("hasCalculationMethod"),
		HAS_CONDITION("hasCondition"),
		HAS_DESCRIPTION("hasDescription"),
		HAS_DISUTILITY_COMBINATION_METHOD("hasDisutilityCombinationMethod"),
		HAS_DOSE("hasDose"),
		HAS_EXPECTED_VALUE("hasExpectedValue"),
		HAS_EXPRESSION_VALUE("hasExpressionValue"),
		HAS_FREQUENCY("hasFrequency"),
		HAS_GEOGRAPHICAL_CONTEXT("hasGeographicalContext"),
		HAS_HOURS_INTERVAL("hasHoursInterval"),
		HAS_LAMBDA_PARAMETER("hasLambdaParameter"),
		HAS_LOWER_LIMIT_PARAMETER("hasLowerLimitParameter"),
		HAS_MAX_AGE("hasMaxAge"),
		HAS_MIN_AGE("hasMinAge"),
		HAS_NAME("hasName"),
		HAS_OFFSET_PARAMETER("hasOffsetParameter"),
		HAS_PROBABILITY_DISTRIBUTION_PARAMETER("hasProbabilityDistributionParameter"),
		HAS_PROBABILITY_PARAMETER("hasProbabilityParameter"),
		HAS_RANGE("hasRange"),
		HAS_REF_TO("hasRefTo"),
		HAS_REF_TO_DO("hasRefToDO"),
		HAS_REF_TO_GARD("hasRefToGARD"),
		HAS_REF_TO_ICD("hasRefToICD"),
		HAS_REF_TO_OMIM("hasRefToOMIM"),
		HAS_REF_TO_ORDO("hasRefToORDO"),
		HAS_REF_TO_SNOMED("hasRefToSNOMED"),
		HAS_REF_TO_STATO("hasRefToSTATO"),
		HAS_REF_TO_WIKIDATA("hasRefToWikidata"),
		HAS_SCALE_PARAMETER("hasScaleParameter"),
		HAS_SIZE("hasSize"),
		HAS_SOURCE("hasSource"),
		HAS_STANDARD_DEVIATION_PARAMETER("hasStandardDeviationParameter"),
		HAS_TEMPORAL_BEHAVIOR("hasTemporalBehavior"),
		HAS_UNIT("hasUnit"),
		HAS_UPPER_LIMIT_PARAMETER("hasUpperLimitParameter"),
		HAS_YEAR("hasYear"),
		IS_TRUE_EPIDEMIOLOGICAL_PARAMETER_ESTIMATE("isTrueEpidemiologicalParameterEstimate"),
		TOP_DATA_PROPERTY("topDataProperty");
		
		private final String shortName;
		private DataProperty(String shortName) {
			this.shortName = shortName;
		}
		/**
		 * @return the shortName
		 */
		public String getShortName() {
			return shortName;
		}
		
		public void add(String individualIRI, String value) {
			if(!("".equals(value))) {
				currentWrapper.addDataPropertyValue(individualIRI, shortName, value);
			}
		}
		
		public void add(String individualIRI, String value, OWL2Datatype dataType) {
			if(!("".equals(value))) {
				currentWrapper.addDataPropertyValue(individualIRI, shortName, value, dataType);
			}
		}

		public String getValue(String individualIRI) {
			return getValue(individualIRI, null);
		}

		public String getValue(String individualIRI, String defaultValue) {
			ArrayList<String> values = getValues(individualIRI);
			if (values.size() > 1)
				currentWrapper.printWarning(individualIRI, this, "Found more than one value for the data property. Using only " + values.toArray()[0]);
			if (values.size() == 0)
				return defaultValue;
			return values.get(0);
		}

		public ArrayList<String> getValues(String individualIRI) {
			return currentWrapper.getDataPropertyValue(individualIRI, shortName);
		}
	}

	public enum ObjectProperty {
		BELONGS_TO_GROUP("belongsToGroup"),
		DEPENDS_ON_ATTRIBUTE("dependsOnAttribute"),
		DEPENDS_ON_PARAMETER("dependsOnParameter"),
		EXCLUDES_MANIFESTATION("excludesManifestation"),
		FOLLOWED_BY_STRATEGY("followedByStrategy"),
		HAS_AGE("hasAge"),
		HAS_ATTRIBUTE_VALUE("hasAttributeValue"),
		HAS_COMPONENT("hasComponent"),
		HAS_COST("hasCost"),
		HAS_DATA_ITEM_TYPE("hasDataItemType"),
		HAS_DEVELOPMENT("hasDevelopment"),
		HAS_DIAGNOSIS_COST("hasDiagnosisCost"),
		HAS_DIAGNOSIS_STRATEGY("hasDiagnosisStrategy"),
		HAS_DURATION("hasDuration"),
		HAS_END_AGE("hasEndAge"),
		HAS_EPIDEMIOLOGICAL_PARAMETER("hasEpidemiologicalParameter"),
		HAS_EXPRESSION("hasExpression"),
		HAS_FOLLOW_UP_COST("hasFollowUpCost"),
		HAS_FOLLOW_UP_STRATEGY("hasFollowUpStrategy"),
		HAS_GUIDELINE("hasGuideline"),
		HAS_HETEROGENEITY("hasHeterogeneity"),
		HAS_INCREASED_MORTALITY_RATE("hasIncreasedMortalityRate"),
		HAS_INITIAL_PROPORTION("hasInitialProportion"),
		HAS_INTERVENTION("hasIntervention"),
		HAS_LIFE_EXPECTANCY("hasLifeExpectancy"),
		HAS_LIFE_EXPECTANCY_REDUCTION("hasLifeExpectancyReduction"),
		HAS_LINE_OF_THERAPY("hasLineOfTherapy"),
		HAS_MANIFESTATION("hasManifestation"),
		HAS_NATURAL_DEVELOPMENT("hasNaturalDevelopment"),
		HAS_ONSET_AGE("hasOnsetAge"),
		HAS_PARAMETER("hasParameter"),
		HAS_PARAMETER_UNCERTAINTY("hasParameterUncertainty"),
		HAS_PROBABILITY_OF_DEATH("hasProbabilityOfDeath"),
		HAS_PROBABILITY_OF_DIAGNOSIS("hasProbabilityOfDiagnosis"),
		HAS_PROPORTION_WITHIN_GROUP("hasProportionWithinGroup"),
		HAS_RISK_CHARACTERIZATION("hasRiskCharacterization"),
		HAS_SCREENING_COST("hasScreeningCost"),
		HAS_SCREENING_STRATEGY("hasScreeningStrategy"),
		HAS_SENSITIVITY("hasSensitivity"),
		HAS_SEX("hasSex"),
		HAS_SPECIFICITY("hasSpecificity"),
		HAS_STAGE("hasStage"),
		HAS_STOCHASTIC_UNCERTAINTY("hasStochasticUncertainty"),
		HAS_STRATEGY("hasStrategy"),
		HAS_SUBPOPULATION("hasSubpopulation"),
		HAS_TEMPORARY_THRESHOLD("hasTemporaryThreshold"),
		HAS_TREATMENT_COST("hasTreatmentCost"),
		HAS_UTILITY("hasUtility"),
		HAS_VALUE("hasValue"),
		INCLUDED_BY_MODEL("includedByModel"),
		INCLUDES_MODEL_ITEM("includesModelItem"),
		INVOLVES_MODIFICATION("involvesModification"),
		IS_MODIFIED_BY("isModifiedBy"),
		IS_PARAMETER_OF("isParameterOf"),
		IS_PARAMETER_OF_DISEASE("isParameterOfDisease"),
		IS_PARAMETER_OF_MANIFESTATION("isParameterOfManifestation"),
		IS_PARAMETER_OF_POPULATION("isParameterOfPopulation"),
		IS_SUBPOPULATION_OF("isSubpopulationOf"),
		IS_VALUE_OF_ATTRIBUTE("isValueOfAttribute"),
		MODIFIES("modifies"),
		REQUIRES("requires"),
		USES_DRUG("usesDrug"),
		USES_FOLLOW_UP_TEST("usesFollowUpTest"),
		USES_HEALTH_TECHNOLOGY("usesHealthTechnology"),
		USES_SAME_MODEL_ITEMS_AS("usesSameModelItemsAs"),
		USES_TREATMENT("usesTreatment");
		
		private final String shortName;
		private ObjectProperty(String shortName) {
			this.shortName = shortName;
		}
		/**
		 * @return the shortName
		 */
		public String getShortName() {
			return shortName;
		}
	
		public void add(String srcIndividualIRI, String destIndividualIRI) {
			currentWrapper.addObjectPropertyValue(srcIndividualIRI, shortName, destIndividualIRI);
		}

		/**
		 * Returns only the first value for the object property of the specified individual. If more than one are defined, prints a warning.
		 * @param individualIRI A specific individual in the ontology
		 * @return only the first value for the object property of the specified individual; null if non defined.
		 */
		public String getValue(String individualIRI) {
			return getValue(individualIRI, false);
		}
		
		/**
		 * Returns only the first value for the object property of the specified individual. If more than one are defined, prints a warning
		 * @param individualIRI A specific individual in the ontology
		 * @param restrictToWorkingModel Restrict results to those individuals belonging to the working model
		 * @return only the first value for the object property of the specified individual; null if non defined.
		 */
		public String getValue(String individualIRI, boolean restrictToWorkingModel) {
			Set<String> values = getValues(individualIRI, restrictToWorkingModel);
			if (values.size() > 1)
				currentWrapper.printWarning(individualIRI, this, "Found more than one value for the object property. Using only " + values.toArray()[0]);
			if (values.size() == 0)
				return null;
			return (String)values.toArray()[0];
		}
		
		public Set<String> getValues(String individualIRI) {
			return getValues(individualIRI, false);
		}
		
		public Set<String> getValues(String individualIRI, boolean restrictToWorkingModel) {
			final Set<String> results = currentWrapper.getObjectPropertyValue(individualIRI, shortName);
			if (restrictToWorkingModel)
				results.retainAll(currentWrapper.modelItems);
			return results;
		}
	}

	/**
	 * Individuals defined in the ontology as DataItemTypes. The enum name must be the upper case version of the individual IRI.
	 * The DI_UNDEFINED value does not exist in the ontology and is used only in this code.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public enum DataItemType {
		CURRENCY_DOLLAR("Currency_Dollar", 0.0),
		CURRENCY_EURO("Currency_Euro", 0.0),
		CURRENCY_POUND("Currency_Pound", 0.0),
		DI_CONTINUOUS_VARIABLE("DI_Continuous_Variable", 0.0),
		DI_COUNT("DI_Count", 0.0),
		DI_DISUTILITY("DI_Disutility", 0.0),
		DI_FACTOR("DI_Factor", 1.0),
		DI_LOWER_95_CONFIDENCE_LIMIT("DI_Lower95ConfidenceLimit", 0.0),
		DI_MEAN_DIFFERENCE("DI_MeanDifference", 0.0),
		DI_OTHER("DI_Other", 0.0),
		DI_PROBABILITY("DI_Probability", 0.0),
		DI_PROPORTION("DI_Proportion", 0.0),
		DI_RATIO("DI_Ratio", 1.0),
		DI_RELATIVE_RISK("DI_RelativeRisk", 1.0),
		DI_SENSITIVITY("DI_Sensitivity", 1.0),
		DI_SPECIFICITY("DI_Specificity", 1.0),
		DI_STANDARD_DEVIATION("DI_StandardDeviation", 0.0),
		DI_TIME_TO_EVENT("DI_TimeToEvent", 0.0),
		DI_UPPER_95_CONFIDENCE_LIMIT("DI_Upper95ConfidenceLimit", 0.0),
		DI_UTILITY("DI_Utility", 1.0),
		DI_UNDEFINED("Undefined", Double.NaN);

		private final String instanceName;
		private final double defaultValue;
		private DataItemType(String instanceName, double defaultValue) {
			this.instanceName = instanceName;
			this.defaultValue = defaultValue;
		}
		/**
		 * @return the shortName
		 */
		public String getInstanceName() {
			return instanceName;
		}
		/**
		 * @return the defaultValue
		 */
		public double getDefaultValue() {
			return defaultValue;
		}		
	}
	
	private final static String STR_MODIFICATION_SUFFIX = STR_SEP + "Modification";
	private final static TreeMap<Clazz, ModelType> reverseModelType = new TreeMap<>(); 
	private final static TreeMap<Clazz, InterventionType> reverseInterventionType = new TreeMap<>(); 
	private final static TreeMap<String, DataItemType> reverseDataItemType = new TreeMap<>(); 
	private static OSDiWrapper currentWrapper = null;

	static {
		reverseModelType.put(Clazz.DISCRETE_EVENT_SIMULATION_MODEL, ModelType.DES);
		reverseModelType.put(Clazz.AGENT_BASED_MODEL, ModelType.AGENT);
		reverseModelType.put(Clazz.MARKOV_MODEL, ModelType.MARKOV);
		reverseModelType.put(Clazz.DECISION_TREE_MODEL, ModelType.DECISION_TREE);
		reverseInterventionType.put(Clazz.DIAGNOSIS_INTERVENTION, InterventionType.DIAGNOSIS);
		reverseInterventionType.put(Clazz.SCREENING_INTERVENTION, InterventionType.SCREENING);
		reverseInterventionType.put(Clazz.THERAPEUTIC_INTERVENTION, InterventionType.THERAPEUTIC);
		for (DataItemType type : DataItemType.values()) {
			reverseDataItemType.put(type.getInstanceName(), type);
		}
	}

	/**
	 * Creating an OSDi wrapper, by default initializes the static property currentWrapper to this instance.
	 * @param file
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(File file, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
		super(file, PREFIX);
		this.instancePrefix = instancePrefix;
		this.modelItems = new TreeSet<>();
		this.parameterWrappers = new TreeMap<>();
		OSDiWrapper.setCurrentWrapper(this, workingModelName);
	}

	/**
	 * Creating an OSDi wrapper, by default initializes the static property currentWrapper to this instance.
	 * @param path
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(String path, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
		super(path, PREFIX);
		this.instancePrefix = instancePrefix;
		this.modelItems = new TreeSet<>();
		this.parameterWrappers = new TreeMap<>();
		OSDiWrapper.setCurrentWrapper(this, workingModelName);
	}

	/**
	 * @return the currentWrapper
	 */
	public static OSDiWrapper getCurrentWrapper() {
		return currentWrapper;
	}

	/**
	 * @param currentWrapper the currentWrapper to set
	 */
	public static void setCurrentWrapper(OSDiWrapper currentWrapper, String workingModelName) {
		OSDiWrapper.currentWrapper = currentWrapper;
		currentWrapper.setWorkingModelInstance(workingModelName);
	}

	/**
	 * @param paramName
	 * @return
	 */
	public ParameterWrapper getParameterWrapper(String paramName) {
		return parameterWrappers.get(paramName);
	}

	/**
	 * @param paramName
	 * @param value
	 * @return
	 */
	public boolean addParameterWrapper(String paramName, ParameterWrapper wrapper) {
		if (parameterWrappers.containsKey(paramName))
			return false;
		parameterWrappers.put(paramName, wrapper);
		return true;
	}

	/**
	 * @return the workingModelId
	 */
	public String getWorkingModelInstance() {
		return workingModelInstance;
	}
	
	public void setWorkingModelInstance(String workingModelName) {
		this.workingModelInstance = instancePrefix + workingModelName;
		modelItems.clear();
		modelItems.addAll(ObjectProperty.INCLUDES_MODEL_ITEM.getValues(this.workingModelInstance));
	}
	
	public static DataItemType getDataItemType(String individualIRI) {
		try {
			return reverseDataItemType.get(individualIRI);
		} catch(IllegalArgumentException ex) {
			return DataItemType.DI_UNDEFINED;
		}
	}
	
	@Override
	public String simplifyIRI(String IRI) {
		return IRI.split("#")[1];
	}
	
	/**
	 * @return The prefix that is applied to every instance
	 */
	public String getInstancePrefix() {
		return instancePrefix;
	}

	public String getPopulationAttributeValueInstanceName(String populationName, String attributeName) {
		return OSDiWrapper.InstanceIRI.POPULATION.getIRI(populationName) + STR_SEP + attributeName; 
	}
	
	public String getAttributeValueInstanceModificationName(String interventionName, String attributeName) {
		return  OSDiWrapper.InstanceIRI.INTERVENTION.getIRI(interventionName) + STR_SEP + attributeName + STR_MODIFICATION_SUFFIX; 
	}
	
	public String getParameterInstanceModificationName(String interventionName, String paramName) {
		return OSDiWrapper.InstanceIRI.INTERVENTION.getIRI(interventionName) + STR_SEP + paramName + STR_MODIFICATION_SUFFIX;
	}

	/**
	 * Common part of creating a valuable, independently of whether it is created by defining an expected value or an expression
	 * @param valuableIRI Name of the valuable in the ontology
	 * @param clazz Specific class of the valuable in the ontology: Parameter, AttributeValue...
	 * @param source Source of the value or expression
	 * @param dataType Data type of the valuable
	 */
	private void createCommonPartOfValuable(String valuableIRI, Clazz clazz, String source, DataItemType dataType) {
		clazz.add(valuableIRI);
		ObjectProperty.HAS_DATA_ITEM_TYPE.add(valuableIRI, dataType.getInstanceName());
		DataProperty.HAS_SOURCE.add(valuableIRI, source);
		includeInModel(valuableIRI);
	}
	
	public void createValuable(String valuableIRI, Clazz clazz, String source, double value, DataItemType dataType) {
		createCommonPartOfValuable(valuableIRI, clazz, source, dataType);
		DataProperty.HAS_EXPECTED_VALUE.add(valuableIRI, Double.toString(value));
	}
	
	public void createValuable(String valuableIRI, Clazz clazz, String source, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		createCommonPartOfValuable(valuableIRI, clazz, source, dataType);
		final String expInstanceName = InstanceIRI.EXPRESSION.getIRI(valuableIRI, false); 
		Clazz.AD_HOC_EXPRESSION.add(expInstanceName);
		DataProperty.HAS_EXPRESSION_VALUE.add(expInstanceName, expression);
		for (String attributeName : dependentAttributes) {
			ObjectProperty.DEPENDS_ON_ATTRIBUTE.add(expInstanceName, InstanceIRI.ATTRIBUTE.getIRI(attributeName, false));
		}
		for (String parameterName : dependentParameters) {
			ObjectProperty.DEPENDS_ON_PARAMETER.add(expInstanceName, InstanceIRI.PARAMETER.getIRI(parameterName));
		}
		ObjectProperty.HAS_EXPRESSION.add(valuableIRI, expInstanceName);
		includeInModel(expInstanceName);			
	}

	public void addValuable(String instanceIRI, ObjectProperty property, String valuableIRI, Clazz clazz, String source, double value, DataItemType dataType) {
		createValuable(valuableIRI, clazz, source, value, dataType);
		property.add(instanceIRI, valuableIRI);
	}

	public void addValuable(String instanceIRI, ObjectProperty property, String valuableIRI, Clazz clazz, String source, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		createValuable(valuableIRI, clazz, source, expression, dependentAttributes, dependentParameters, dataType);
		property.add(instanceIRI, valuableIRI);
	}

	/**
	 * Common part of creating a parameter, independently of whether it is created by defining an expected value or an expression
	 * @param paramIRI Name of the parameter in the ontology
	 * @param description Description of the parameter
	 * @param year Year of the parameter
	 */
	private void createCommonPartOfParameter(String paramIRI, String description, int year) {
		DataProperty.HAS_DESCRIPTION.add(paramIRI, description);
		DataProperty.HAS_YEAR.add(paramIRI, "" + year);
	}
	
	public void createParameter(String paramIRI, Clazz clazz, String description, String source, int year, double value, DataItemType dataType) {
		createValuable(paramIRI, clazz, source, value, dataType);
		createCommonPartOfParameter(paramIRI, description, year);
	}
	
	public void createParameter(String paramIRI, Clazz clazz, String description, String source, int year, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		createValuable(paramIRI, clazz, source, expression, dependentAttributes, dependentParameters, dataType);
		createCommonPartOfParameter(paramIRI, description, year);
	}

	public void addParameter(String instanceIRI, ObjectProperty property, String paramIRI, Clazz clazz, String description, String source, int year, double value, DataItemType dataType) {
		createParameter(paramIRI, clazz, description, source, year, value, dataType);
		property.add(instanceIRI, paramIRI);
	}
	
	public void addParameter(String instanceIRI, ObjectProperty property, String paramIRI, Clazz clazz, String description, String source, int year, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		createParameter(paramIRI, clazz, description, source, year, expression, dependentAttributes, dependentParameters, dataType);
		property.add(instanceIRI, paramIRI);
	}

	public void addAttributeValue(String instanceIRI, ObjectProperty property, String attributeValueIRI, String attributeIRI, String source, double value, DataItemType dataType) {
		addValuable(instanceIRI, property, attributeValueIRI, Clazz.ATTRIBUTE_VALUE, source, value, dataType);
		ObjectProperty.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
	}
	
	public void addAttributeValue(String instanceIRI, ObjectProperty property, String attributeValueIRI, String attributeIRI, String source, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		addValuable(instanceIRI, property, attributeValueIRI, Clazz.ATTRIBUTE_VALUE, source, expression, dependentAttributes, dependentParameters, dataType);
		ObjectProperty.IS_VALUE_OF_ATTRIBUTE.add(attributeValueIRI, attributeIRI);
	}
	
	private void createCommonPartOfModification(String modificationIRI, String interventionIRI, String modifiedInstanceIRI) {
		ObjectProperty.MODIFIES.add(modificationIRI, modifiedInstanceIRI);
		ObjectProperty.IS_MODIFIED_BY.add(modifiedInstanceIRI, modificationIRI);
	}
	

	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, String attributeIRI, String source, double value, DataItemType dataType) {
		addAttributeValue(interventionIRI, ObjectProperty.INVOLVES_MODIFICATION, modificationIRI, attributeIRI, source, value, dataType);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedAttributeValueIRI);
	}
	
	public void addAttributeValueModification(String modificationIRI, String interventionIRI, String modifiedAttributeValueIRI, String attributeIRI, String source, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		addAttributeValue(interventionIRI, ObjectProperty.INVOLVES_MODIFICATION, modificationIRI, attributeIRI, source, expression, dependentAttributes, dependentParameters, dataType);		
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedAttributeValueIRI);
	}
	
	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, Clazz clazz, String description, String source, int year, double value, DataItemType dataType) {
		addParameter(interventionIRI, ObjectProperty.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, value, dataType);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedParameterIRI);
	}
	
	public void addParameterModification(String modificationIRI, String interventionIRI, String modifiedParameterIRI, Clazz clazz, String description, String source, int year, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType dataType) {
		addParameter(interventionIRI, ObjectProperty.INVOLVES_MODIFICATION, modificationIRI, clazz, description, source, year, expression, dependentAttributes, dependentParameters, dataType);
		createCommonPartOfModification(modificationIRI, interventionIRI, modifiedParameterIRI);
	}
	
	public void addCost(String instanceIRI, ObjectProperty property, String paramIRI, String description, String source, TemporalBehavior tmpBehavior, int year, double value, DataItemType currency) {
		addParameter(instanceIRI, property, paramIRI, Clazz.COST, description, source, year, value, currency);
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(paramIRI, tmpBehavior.getShortName());
	}
	
	public void addCost(String instanceIRI, ObjectProperty property, String paramIRI, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, DataItemType currency) {
		addParameter(instanceIRI, property, paramIRI, Clazz.COST, description, source, year, expression, dependentAttributes, dependentParameters, currency);
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(paramIRI, tmpBehavior.getShortName());
	}

	public void addUtility(String instanceIRI, ObjectProperty property, String paramIRI, String description, String source, TemporalBehavior tmpBehavior, int year, double value, UtilityType utilityType) {
		addParameter(instanceIRI, property, paramIRI, Clazz.UTILITY, description, source, year, value, utilityType.getType());
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(paramIRI, tmpBehavior.getShortName());
	}

	public void addUtility(String instanceIRI, ObjectProperty property, String paramIRI, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, Set<String> dependentAttributes, Set<String> dependentParameters, UtilityType utilityType) {
		addParameter(instanceIRI, property, paramIRI, Clazz.UTILITY, description, source, year, expression, dependentAttributes, dependentParameters, utilityType.getType());
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(paramIRI, tmpBehavior.getShortName());
	}

	public void addProbabilityDistributionExpression(String instanceIRI, ObjectProperty property, String expressionIRI, ProbabilityDistributionExpression type, double[] parameters) {
		if (parameters.length != type.getnParameters())
			throw new IllegalArgumentException("Creating a " + type.name() + " probability distribution requires " + type.getnParameters() + " parameters. Passed " + parameters.length);
		type.getClazz().add(expressionIRI);
		switch(type) {
		case BERNOULLI:
			OSDiWrapper.DataProperty.HAS_PROBABILITY_PARAMETER.add(expressionIRI, "" + parameters[0]);		
			break;			
		case BETA:
			OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiWrapper.DataProperty.HAS_BETA_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case POISSON:
		case EXPONENTIAL:
			OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.add(expressionIRI, "" + parameters[0]);		
			break;
		case GAMMA:
			OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case UNIFORM:
			OSDiWrapper.DataProperty.HAS_LOWER_LIMIT_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiWrapper.DataProperty.HAS_UPPER_LIMIT_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;
		case NORMAL:
		default:
			OSDiWrapper.DataProperty.HAS_AVERAGE_PARAMETER.add(expressionIRI, "" + parameters[0]);
			OSDiWrapper.DataProperty.HAS_STANDARD_DEVIATION_PARAMETER.add(expressionIRI, "" + parameters[1]);		
			break;		
		}
		property.add(instanceIRI, expressionIRI);
	}

	/**
	 * Creates a model by using the working model instance specified 
	 * @param modelName
	 * @param type
	 * @param author
	 * @param description
	 * @param geoContext
	 * @param year
	 * @param reference
	 */
	public void createWorkingModel(ModelType type, String author, String description, String geoContext, int year, String reference, DisutilityCombinationMethod combinationMethod) {
		type.getClazz().add(workingModelInstance);
		DataProperty.HAS_AUTHOR.add(workingModelInstance, author);
		DataProperty.HAS_DESCRIPTION.add(workingModelInstance, description);
		DataProperty.HAS_GEOGRAPHICAL_CONTEXT.add(workingModelInstance, geoContext);
		DataProperty.HAS_REF_TO.add(workingModelInstance, reference);
		DataProperty.HAS_YEAR.add(workingModelInstance, "" + year);		
		DataProperty.HAS_DISUTILITY_COMBINATION_METHOD.add(workingModelInstance, combinationMethod.name());
	}

	public void createDisease(String instanceName, String description, String refToDO, String refToICD, String refToOMIM, String refToSNOMED) {
		Clazz.DISEASE.add(instanceName);		
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		DataProperty.HAS_REF_TO_DO.add(instanceName,  refToDO);
		DataProperty.HAS_REF_TO_ICD.add(instanceName,  refToICD);
		DataProperty.HAS_REF_TO_OMIM.add(instanceName,  refToOMIM);
		DataProperty.HAS_REF_TO_SNOMED.add(instanceName,  refToSNOMED);
		includeInModel(instanceName);
	}
	
	public void createManifestation(String instanceName, DiseaseProgressionType type, String description, Set<String> exclusions, String diseaseInstanceName) {
		type.getClazz().add(instanceName);		
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		ObjectProperty.HAS_MANIFESTATION.add(diseaseInstanceName, instanceName);
		includeInModel(instanceName);
		for (String excludedManif : exclusions) {
			ObjectProperty.EXCLUDES_MANIFESTATION.add(instanceName, InstanceIRI.MANIFESTATION.getIRI(excludedManif));
		}
	}
	
	public void createGroupOfManifestations(String instanceName, Set<String> manifestationNames) {
		Clazz.GROUP.add(instanceName);
		includeInModel(instanceName);
		for (String manifestation : manifestationNames) {
			ObjectProperty.HAS_COMPONENT.add(instanceName, InstanceIRI.MANIFESTATION.getIRI(manifestation));			
			ObjectProperty.BELONGS_TO_GROUP.add(InstanceIRI.MANIFESTATION.getIRI(manifestation), instanceName);			
		}
	}
	
	public void createPopulation(String instanceName, String description, int minAge, int maxAge, int size, int year) {
		Clazz.POPULATION.add(instanceName);
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		DataProperty.HAS_MIN_AGE.add(instanceName, "" + minAge);
		DataProperty.HAS_MAX_AGE.add(instanceName, "" + maxAge);
		DataProperty.HAS_SIZE.add(instanceName, "" + size);
		DataProperty.HAS_YEAR.add(instanceName, "" + year);
		
		includeInModel(instanceName);
	}
	
	public void createIntervention(String instanceName, InterventionType type, String description) {
		type.getClazz().add(instanceName);
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		
		includeInModel(instanceName);
	}

	public void addUtilityFromAvgCI(String instanceIRI, ObjectProperty property, String utilityIRI, String description, String source, double[] values, TemporalBehavior tmpBehavior, int year, UtilityType utilityType) {
		addUtility(instanceIRI, property, utilityIRI, description, source, tmpBehavior, year, values[0], utilityType);
		final String[] params = addCIParameters(utilityIRI, Clazz.UTILITY, description, source, new double[] {values[1], values[2]}, year);
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(params[0], tmpBehavior.getShortName());
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(params[1], tmpBehavior.getShortName());
	}
	
	public String[] addCIParameters(String mainParamIRI, Clazz clazz, String description, String source, double[] values, int year) {
		String []params = new String[2];
		String paramUncertaintyIRI = InstanceIRI.UNCERTAINTY_L95CI.getIRI(mainParamIRI, false);
		addParameter(mainParamIRI, ObjectProperty.HAS_PARAMETER_UNCERTAINTY, paramUncertaintyIRI, clazz, "Lower 95% confidence interval for " + description, 
				source, year, values[0], DataItemType.DI_LOWER_95_CONFIDENCE_LIMIT);
		params[0] = paramUncertaintyIRI;
		paramUncertaintyIRI = InstanceIRI.UNCERTAINTY_U95CI.getIRI(mainParamIRI, false);
		addParameter(mainParamIRI, ObjectProperty.HAS_PARAMETER_UNCERTAINTY, paramUncertaintyIRI, clazz, "Upper 95% confidence interval for " + description, 
				source, year, values[1], DataItemType.DI_UPPER_95_CONFIDENCE_LIMIT);
		params[1] = paramUncertaintyIRI;
		return params;
	}
	
	
	private void includeInModel(String instanceName) {
		ObjectProperty.INCLUDED_BY_MODEL.add(instanceName, workingModelInstance);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(workingModelInstance, instanceName);
	}
	
	public ArrayList<String> getEnglishCommentForClass(IRI iri) {
		final ArrayList<String> list = new ArrayList<>();
//		final OWLClass owlClass = getClass(clazz.getShortName());
		for(OWLAnnotationAssertionAxiom a : ontology.getAnnotationAssertionAxioms(iri)) {
		    if(a.getProperty().isComment()) {
		        if(a.getValue() instanceof OWLLiteral) {
		            OWLLiteral val = (OWLLiteral) a.getValue();
		            if (val.hasLang("en"))
		            	list.add(val.getLiteral());
		        }
		    }
		}
		return list;
	}
	
	public Set<String> getIndividuals(String classIRI, boolean restrictToWorkingModel) {
		if (restrictToWorkingModel)
			return getIndividualsSubclassOf(modelItems, classIRI);
		return super.getIndividuals(classIRI);
	}
	
	public static void printEverythingAsEnum(String path) {
		try {
			final OSDiWrapper wrap = new OSDiWrapper(path, "", "");

			System.out.println("---------------- CLASSES ----------------");
			wrap.printClassesAsEnum();
			System.out.println("---------------- DATA PROPS ----------------");
			wrap.printDataPropertiesAsEnum();
			System.out.println("---------------- OBJECT PROPS ----------------");
			wrap.printObjectPropertiesAsEnum();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Processes the hasYear data property of an individual and returns an integer representation of its value. If the property is not defined
	 * or its value has a wrong format, returns the current year  
	 * @param individualIRI The IRI of a valid individual in the ontology
	 * @return an integer representation of the hasYear data property for an individual
	 */
	public int parseHasYearProperty(String individualIRI) {
		int currentYear = Year.now().getValue();
		final String strYear = OSDiWrapper.DataProperty.HAS_YEAR.getValue(individualIRI, "" + currentYear);
		try {
			currentYear = Integer.parseInt(strYear);
		} catch(NumberFormatException ex) {
			printWarning(individualIRI, OSDiWrapper.DataProperty.HAS_YEAR, "Wrong year format. Found " + strYear + ". Using " + currentYear + " instead");
		}
		return currentYear;
		
	}
	
	public void printWarning(String individualIRI, OSDiWrapper.DataProperty prop, String msg) {
		printWarning(individualIRI + "\t" + prop.getShortName() + "\tWARNING\t\"" + msg + "\"");
	}
	
	public void printWarning(String individualIRI, OSDiWrapper.ObjectProperty prop, String msg) {
		printWarning(individualIRI + "\t" + prop.getShortName() + "\tWARNING\t\"" + msg + "\"");
	}
	
	public void printWarning(String msg) {
		if (ENABLE_WARNINGS)
			System.err.println("WARNING: " + msg);
	}
	
	public void printWarning(boolean condition, String individualIRI, OSDiWrapper.DataProperty prop, String msg) {
		if (condition)
			printWarning(individualIRI, prop, msg);
	}
	
	public void printWarning(boolean condition, String individualIRI, OSDiWrapper.ObjectProperty prop, String msg) {
		if (condition)
			printWarning(individualIRI, prop, msg);
	}
	
	public void printWarning(boolean condition, String msg) {
		if (condition)
			printWarning(msg);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		printEverythingAsEnum("resources/OSDi.owl");
//		try {
//			final OSDiWrapper wrap = new OSDiWrapper("resources/OSDi.owl", "T1DM_StdModelDES", "T1DM_");
//			for (String str : wrap.getIndividuals("DataItemType"))
//				System.out.println(str);
//			for (String str : wrap.getClassesForIndividual("T1DM_StdModelDES"))
//				System.out.println(str);
//			System.out.println("TESTING ACCESSING TO DATA PROPERTIES");
//			System.out.println(DataProperty.HAS_REF_TO.getValue("T1DM_StdModelDES", "NOPE!"));
//		} catch (OWLOntologyCreationException e) {
//			e.printStackTrace();
//		}
	}
	
}
