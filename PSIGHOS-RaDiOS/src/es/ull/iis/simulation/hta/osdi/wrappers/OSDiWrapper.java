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
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import es.ull.iis.ontology.OWLOntologyWrapper;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;

/**
 * @author Iv�n Castilla Rodr�guez
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
	public final static String STR_MANIF_PREFIX = "Manif" + STR_SEP;
	public final static String STR_MANIF_GROUP_PREFIX = "Group_Manif" + STR_SEP;
	public final static String STR_POPULATION_PREFIX = "Population" + STR_SEP;
	public final static String STR_ATTRIBUTE_PREFIX = "Attribute" + STR_SEP;
	public final static String STR_UNCERTAINTY_SUFFIX = STR_SEP + "ParamUncertainty";
	public final static String STR_L95CI_SUFFIX = STR_SEP + "L95CI";
	public final static String STR_U95CI_SUFFIX = STR_SEP + "U95CI";
	public final static String STR_ANNUAL_COST_SUFFIX = STR_SEP + "AC";
	public final static String STR_ONETIME_COST_SUFFIX = STR_SEP + "TC";
	public final static String STR_UTILITY_SUFFIX = STR_SEP + "U";
	private final static TreeMap<Clazz, ModelType> reverseModelType = new TreeMap<>(); 
	private final static TreeMap<Clazz, InterventionType> reverseInterventionType = new TreeMap<>(); 
	private static OSDiWrapper currentWrapper = null;

	static {
		reverseModelType.put(Clazz.DISCRETE_EVENT_SIMULATION_MODEL, ModelType.DES);
		reverseModelType.put(Clazz.AGENT_BASED_MODEL, ModelType.AGENT);
		reverseModelType.put(Clazz.MARKOV_MODEL, ModelType.MARKOV);
		reverseModelType.put(Clazz.DECISION_TREE_MODEL, ModelType.DECISION_TREE);
		reverseInterventionType.put(Clazz.DIAGNOSIS_INTERVENTION, InterventionType.DIAGNOSIS);
		reverseInterventionType.put(Clazz.SCREENING_INTERVENTION, InterventionType.SCREENING);
		reverseInterventionType.put(Clazz.THERAPEUTIC_INTERVENTION, InterventionType.THERAPEUTIC);
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
	
	public enum ManifestationType {
		ACUTE(Clazz.ACUTE_MANIFESTATION),
		CHRONIC(Clazz.CHRONIC_MANIFESTATION);
		private final Clazz clazz;
		private ManifestationType(Clazz clazz) {
			this.clazz = clazz;
		}
		/**
		 * @return the clazz
		 */
		public Clazz getClazz() {
			return clazz;
		}
	}
	
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
	 * @author Iván Castilla
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
	
	public enum Clazz {
		ACUTE_MANIFESTATION("AcuteManifestation"),
		AGENT_BASED_MODEL("AgentBasedModel"),
		ATTRIBUTE("Attribute"),
		ATTRIBUTE_VALUE("AttributeValue"),
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
		DRUG("Drug"),
		EPIDEMIOLOGICAL_PARAMETER("EpidemiologicalParameter"),
		FOLLOW_UP_STRATEGY("FollowUpStrategy"),
		FOLLOW_UP_TEST("FollowUpTest"),
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
		PARAMETER("Parameter"),
		PATHWAY("Pathway"),
		POPULATION("Population"),
		PREVALENCE("Prevalence"),
		PROPORTION_WITHIN_GROUP("ProportionWithinGroup"),
		RARE_DISEASE("RareDisease"),
		SCREENING_INTERVENTION("ScreeningIntervention"),
		SCREENING_STRATEGY("ScreeningStrategy"),
		STAGE("Stage"),
		STAGE_PATHWAY("StagePathway"),
		STRATEGY("Strategy"),
		THERAPEUTIC_INTERVENTION("TherapeuticIntervention"),
		TREATMENT("Treatment"),
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
		HAS_AUTHOR("hasAuthor"),
		HAS_CALCULATION_METHOD("hasCalculationMethod"),
		HAS_CONDITION("hasCondition"),
		HAS_DESCRIPTION("hasDescription"),
		HAS_DISUTILITY_COMBINATION_METHOD("hasDisutilityCombinationMethod"),
		HAS_DOSE("hasDose"),
		HAS_EXPRESSION("hasExpression"),
		HAS_FREQUENCY("hasFrequency"),
		HAS_GEOGRAPHICAL_CONTEXT("hasGeographicalContext"),
		HAS_HOURS_INTERVAL("hasHoursInterval"),
		HAS_MAX_AGE("hasMaxAge"),
		HAS_MIN_AGE("hasMinAge"),
		HAS_NAME("hasName"),
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
		HAS_REFTO_WIKIDATA("hasReftoWikidata"),
		HAS_SIZE("hasSize"),
		HAS_SOURCE("hasSource"),
		HAS_TEMPORAL_BEHAVIOR("hasTemporalBehavior"),
		HAS_UNIT("hasUnit"),
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

		public String getValue(String individualIRI, String defaultValue) {
			ArrayList<String> values = getValues(individualIRI);
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
		HAS_FOLLOW_UP_COST("hasFollowUpCost"),
		HAS_FOLLOW_UP_STRATEGY("hasFollowUpStrategy"),
		HAS_GUIDELINE("hasGuideline"),
		HAS_HETEROGENEITY("hasHeterogeneity"),
		HAS_INCREASED_MORTALITY_RATE("hasIncreasedMortalityRate"),
		HAS_INITIAL_PROPORTION("hasInitialProportion"),
		HAS_INTERVENTION("hasIntervention"),
		HAS_LIFE_EXPECTANCY("hasLifeExpectancy"),
		HAS_LIFE_EXPECTANCY_REDUCTION("hasLifeExctancyReduction"),
		HAS_LINE_OF_THERAPY("hasLineOfTherapy"),
		HAS_MANIFESTATION("hasManifestation"),
		HAS_NATURAL_DEVELOPMENT("hasNaturalDevelopment"),
		HAS_ONSET_AGE("hasOnsetAge"),
		HAS_PARAMETER("hasParameter"),
		HAS_PARAMETER_UNCERTAINTY("hasParameterUncertainty"),
		HAS_PATHWAY("hasPathway"),
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
		IS_PATHWAY_TO("isPathwayTo"),
		IS_SUBPOPULATION_OF("isSubpopulationOf"),
		IS_VALUE_OF_ATTRIBUTE("isValueOfAttribute"),
		MODIFIES("modifies"),
		REQUIRES("requires"),
		USES_DRUG("usesDrug"),
		USES_FOLLOW_UP_TEST("usesFollowUpTest"),
		USES_HEALTH_TECHNOLOGY("usesHealthTechnology"),
		USES_SAME_MODEL_ITEMS_AS("usesSameModelItemsAs"),
		USES_TREATMENT("usesTreatment"),
		USES_VALUE_FROM("usesValueFrom");
		
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
		 * Returns only the first value for the object property of the specified individual. If more than one are defined, prints a warning
		 * @param individualIRI A specific individual in the ontology
		 * @return only the first value for the object property of the specified individual; null if non defined.
		 */
		public String getValue(String individualIRI) {
			return getValue(individualIRI, false);
		}
		
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
		DI_LOWER95CONFIDENCELIMIT("DI_Lower95ConfidenceLimit", 0.0),
		DI_MEANDIFFERENCE("DI_MeanDifference", 0.0),
		DI_OTHER("DI_Other", 0.0),
		DI_PROBABILITY("DI_Probability", 0.0),
		DI_PROPORTION("DI_Proportion", 0.0),
		DI_RATIO("DI_Ratio", 1.0),
		DI_RELATIVERISK("DI_RelativeRisk", 1.0),
		DI_SENSITIVITY("DI_Sensitivity", 1.0),
		DI_SPECIFICITY("DI_Specificity", 1.0),
		DI_TIMETOEVENT("DI_TimeToEvent", 0.0),
		DI_UPPER95CONFIDENCELIMIT("DI_Upper95ConfidenceLimit", 0.0),
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
			return DataItemType.valueOf(individualIRI.toUpperCase());
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
	
	public String getDiseaseInstanceName(String diseaseName) {
		return instancePrefix + diseaseName;
	}
	
	public String getManifestationInstanceName(String manifName) {
		return instancePrefix + STR_MANIF_PREFIX + manifName;
	}

	public String getManifestationGroupInstanceName(String groupName) {
		return instancePrefix + STR_MANIF_GROUP_PREFIX + groupName;
	}

	public String getPopulationInstanceName(String populationName) {
		return instancePrefix + STR_POPULATION_PREFIX + populationName;
	}
	
	/**
	 * Returns the expected name for an attribute instance within the ontology. In general, attributes do not use the prefix of the working model, since they should be defined for every disease and model
	 * @param attributeName The "raw" name of the attribute 
	 * @return the expected name for an attribute instance within the ontology.
	 */
	public String getAttributeInstanceName(String attributeName) {
		return STR_ATTRIBUTE_PREFIX + attributeName; 
	}
	
	public String getPopulationAttributeValueInstanceName(String populationName, String attributeName) {
		return getPopulationInstanceName(populationName) + STR_SEP + attributeName; 
	}
	
	public String getParameterInstanceName(String paramName) {
		return instancePrefix + paramName;
	}
	
	public void createValuable(String instanceName, Clazz clazz, String source, String expression, DataItemType dataType) {
		clazz.add(instanceName);
		ObjectProperty.HAS_DATA_ITEM_TYPE.add(instanceName, dataType.getInstanceName());
		DataProperty.HAS_SOURCE.add(instanceName, source);
		DataProperty.HAS_EXPRESSION.add(instanceName, expression);		
		includeInModel(instanceName);
	}
	
	public void createParameter(String instanceName, Clazz clazz, String description, String source, int year, String expression, DataItemType dataType) {
		createValuable(instanceName, clazz, source, expression, dataType);
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		DataProperty.HAS_YEAR.add(instanceName, "" + year);
	}

	public void createAttributeValue(String instanceName, String attributeInstanceName, String source, String expression, DataItemType dataType) {
		createValuable(instanceName, Clazz.ATTRIBUTE_VALUE, source, expression, dataType);
		ObjectProperty.IS_VALUE_OF_ATTRIBUTE.add(instanceName, attributeInstanceName);
	}
	
	public void createCost(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, DataItemType currency) {
		createParameter(instanceName, Clazz.COST, description, source, year, expression, currency);
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(instanceName, tmpBehavior.getShortName());
	}
	
	public void createCost(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String deterministic, String uncertainty, DataItemType currency) {
		createCost(instanceName, description, source, tmpBehavior, year, deterministic, currency);
		if (uncertainty != null && !("".equals(uncertainty))) {
			createParameter(instanceName + STR_UNCERTAINTY_SUFFIX, Clazz.COST, description, source, year, uncertainty, currency);
			DataProperty.HAS_TEMPORAL_BEHAVIOR.add(instanceName, tmpBehavior.getShortName());
		}
	}

	public void createUtility(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, UtilityType utilityType) {
		createParameter(instanceName, Clazz.UTILITY, description, source, year, expression, utilityType.getType());
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(instanceName, tmpBehavior.getShortName());
	}
	
	public void createUtility(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String deterministic, String uncertainty, UtilityType utilityType) {
		createUtility(instanceName, description, source, tmpBehavior, year, deterministic, utilityType);
		if (uncertainty != null && !("".equals(uncertainty))) {
			createParameter(instanceName + STR_UNCERTAINTY_SUFFIX, Clazz.UTILITY, description, source, year, uncertainty, utilityType.getType());
			DataProperty.HAS_TEMPORAL_BEHAVIOR.add(instanceName, tmpBehavior.getShortName());
		}
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
	
	public void createManifestation(String instanceName, ManifestationType type, String description, Set<String> exclusions, String diseaseInstanceName) {
		type.getClazz().add(instanceName);		
		DataProperty.HAS_DESCRIPTION.add(instanceName, description);
		ObjectProperty.HAS_MANIFESTATION.add(diseaseInstanceName, instanceName);
		includeInModel(instanceName);
		for (String excludedManif : exclusions) {
			ObjectProperty.EXCLUDES_MANIFESTATION.add(instanceName, getManifestationInstanceName(excludedManif));
		}
	}
	
	public void createGroupOfManifestations(String instanceName, Set<String> manifestationNames) {
		Clazz.GROUP.add(instanceName);
		includeInModel(instanceName);
		for (String manifestation : manifestationNames) {
			ObjectProperty.HAS_COMPONENT.add(instanceName, getManifestationInstanceName(manifestation));			
			ObjectProperty.BELONGS_TO_GROUP.add(getManifestationInstanceName(manifestation), instanceName);			
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
	
	private void includeInModel(String instanceName) {
		ObjectProperty.INCLUDED_BY_MODEL.add(instanceName, workingModelInstance);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(workingModelInstance, instanceName);
	}
	
	public Set<String> getClassesForIndividual(String individualIRI) {
		Set<String> result = new TreeSet<>();
		final Set<OWLClass> types = reasoner.types(factory.getOWLNamedIndividual(individualIRI, pm)).collect(Collectors.toSet());
		for (OWLClass clazz : types)
			result.add(simplifyIRI(clazz.getIRI().getIRIString()));
		return result;
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		printEverythingAsEnum("resources/OSDi.owl");
		try {
			final OSDiWrapper wrap = new OSDiWrapper("resources/OSDi.owl", "T1DM_StdModelDES", "T1DM_");
			for (String str : wrap.getIndividuals("DataItemType"))
				System.out.println(str);
			for (String str : wrap.getClassesForIndividual("T1DM_StdModelDES"))
				System.out.println(str);
			System.out.println("TESTING ACCESSING TO DATA PROPERTIES");
			System.out.println(DataProperty.HAS_REF_TO.getValue("T1DM_StdModelDES", "NOPE!"));
//			for (String str : DataProperty.HAS_REF_TO_SNOMED.getValues(wrap, "T1DM_Disease")) {
//				System.out.println(str);
//			}
//			for (String str : ObjectProperty.HAS_MANIFESTATION.getValues(wrap, "T1DM_Disease")) {
//				System.out.println(str);
//			}

//			for (String  str : wrap.getEnglishCommentForClass(wrap.getObjectProperty(ObjectProperty.BELONGS_TO_AREA.getShortName()).getIRI()))
//				System.out.println(str);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
	
}
