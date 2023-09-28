/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
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
		private final String shortName;
		private Clazz(String shortName) {
			this.shortName = shortName;
		}
		/**
		 * @return the shortName
		 */
		public String getShortName() {
			return shortName;
		}
		
		public void add(OSDiWrapper wrap, String individualIRI) {
			wrap.addIndividual(shortName, individualIRI);
		}		
		
		public Set<String> getIndividuals(OSDiWrapper wrap) {
			return getIndividuals(wrap, false);
		}

		public Set<String> getIndividuals(OSDiWrapper wrap, boolean restrictToWorkingModel) {
			final Set<String> results = wrap.getIndividuals(shortName);
			if (restrictToWorkingModel)
				results.retainAll(wrap.modelItems);
			return results;
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
		
		public void add(OSDiWrapper wrap, String individualIRI, String value) {
			if(!("".equals(value))) {
				wrap.addDataPropertyValue(individualIRI, shortName, value);
			}
		}
		
		public void add(OSDiWrapper wrap, String individualIRI, String value, OWL2Datatype dataType) {
			if(!("".equals(value))) {
				wrap.addDataPropertyValue(individualIRI, shortName, value, dataType);
			}
		}

		public String getValue(OSDiWrapper wrap, String individualIRI, String defaultValue) {
			ArrayList<String> values = getValues(wrap, individualIRI);
			if (values.size() == 0)
				return defaultValue;
			return values.get(0);
		}

		public ArrayList<String> getValues(OSDiWrapper wrap, String individualIRI) {
			return wrap.getDataPropertyValue(individualIRI, shortName);
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
	
		public void add(OSDiWrapper wrap, String srcIndividualIRI, String destIndividualIRI) {
			wrap.addObjectPropertyValue(srcIndividualIRI, shortName, destIndividualIRI);
		}

		/**
		 * Returns only the first value for the object property of the specified individual. If more than one are defined, prints a warning
		 * @param wrap A wrapper for the ontology
		 * @param individualIRI A specific individual in the ontology
		 * @return only the first value for the object property of the specified individual; null if non defined.
		 */
		public String getValue(OSDiWrapper wrap, String individualIRI) {
			return getValue(wrap, individualIRI, false);
		}
		
		public String getValue(OSDiWrapper wrap, String individualIRI, boolean restrictToWorkingModel) {
			Set<String> values = getValues(wrap, individualIRI, restrictToWorkingModel);
			if (values.size() > 1)
				wrap.printWarning(individualIRI, this, "Found more than one value for the object property. Using only " + values.toArray()[0]);
			if (values.size() == 0)
				return null;
			return (String)values.toArray()[0];
		}
		
		public Set<String> getValues(OSDiWrapper wrap, String individualIRI) {
			return getValues(wrap, individualIRI, false);
		}
		
		public Set<String> getValues(OSDiWrapper wrap, String individualIRI, boolean restrictToWorkingModel) {
			final Set<String> results = wrap.getObjectPropertyValue(individualIRI, shortName);
			if (restrictToWorkingModel)
				results.retainAll(wrap.modelItems);
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
		CURRENCY_DOLLAR("Currency_Dollar"),
		CURRENCY_EURO("Currency_Euro"),
		CURRENCY_POUND("Currency_Pound"),
		DI_CONTINUOUS_VARIABLE("DI_Continuous_Variable"),
		DI_COUNT("DI_Count"),
		DI_DISUTILITY("DI_Disutility"),
		DI_FACTOR("DI_Factor"),
		DI_LOWER95CONFIDENCELIMIT("DI_Lower95ConfidenceLimit"),
		DI_MEANDIFFERENCE("DI_MeanDifference"),
		DI_OTHER("DI_Other"),
		DI_PROBABILITY("DI_Probability"),
		DI_PROPORTION("DI_Proportion"),
		DI_RATIO("DI_Ratio"),
		DI_RELATIVERISK("DI_RelativeRisk"),
		DI_SENSITIVITY("DI_Sensitivity"),
		DI_SPECIFICITY("DI_Specificity"),
		DI_TIMETOEVENT("DI_TimeToEvent"),
		DI_UPPER95CONFIDENCELIMIT("DI_Upper95ConfidenceLimit"),
		DI_UTILITY("DI_Utility"),
		DI_UNDEFINED("Undefined");

		private final String instanceName;
		private DataItemType(String instanceName) {
			this.instanceName = instanceName;
		}
		/**
		 * @return the shortName
		 */
		public String getInstanceName() {
			return instanceName;
		}		
	}

	/**
	 * @param file
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(File file, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
		super(file, PREFIX);
		this.instancePrefix = instancePrefix;
		this.modelItems = new TreeSet<>();
		setWorkingModelInstance(workingModelName);
	}

	/**
	 * @param path
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(String path, String workingModelName, String instancePrefix) throws OWLOntologyCreationException {
		super(path, PREFIX);
		this.instancePrefix = instancePrefix;
		this.modelItems = new TreeSet<>();
		setWorkingModelInstance(workingModelName);
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
		modelItems.addAll(ObjectProperty.INCLUDES_MODEL_ITEM.getValues(this, this.workingModelInstance));
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
	
	public String getAttributeInstanceName(String attributeName) {
		return instancePrefix + STR_ATTRIBUTE_PREFIX + attributeName; 
	}
	
	public String getPopulationAttributeValueInstanceName(String populationName, String attributeName) {
		return getPopulationInstanceName(populationName) + STR_SEP + attributeName; 
	}
	
	public String getParameterInstanceName(String paramName) {
		return instancePrefix + paramName;
	}
	
	public void createValuable(String instanceName, Clazz clazz, String source, String expression, DataItemType dataType) {
		clazz.add(this, instanceName);
		ObjectProperty.HAS_DATA_ITEM_TYPE.add(this, instanceName, dataType.getInstanceName());
		DataProperty.HAS_SOURCE.add(this, instanceName, source);
		DataProperty.HAS_EXPRESSION.add(this, instanceName, expression);		
		includeInModel(instanceName);
	}
	
	public void createParameter(String instanceName, Clazz clazz, String description, String source, int year, String expression, DataItemType dataType) {
		createValuable(instanceName, clazz, source, expression, dataType);
		DataProperty.HAS_DESCRIPTION.add(this, instanceName, description);
		DataProperty.HAS_YEAR.add(this, instanceName, "" + year);
	}

	public void createAttributeValue(String instanceName, String attributeInstanceName, String source, String expression, DataItemType dataType) {
		createValuable(instanceName, Clazz.ATTRIBUTE_VALUE, source, expression, dataType);
		ObjectProperty.IS_VALUE_OF_ATTRIBUTE.add(this, instanceName, attributeInstanceName);
	}
	
	public void createCost(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, DataItemType currency) {
		createParameter(instanceName, Clazz.COST, description, source, year, expression, currency);
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(this, instanceName, tmpBehavior.getShortName());
	}
	
	public void createCost(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String deterministic, String uncertainty, DataItemType currency) {
		createCost(instanceName, description, source, tmpBehavior, year, deterministic, currency);
		if (uncertainty != null && !("".equals(uncertainty))) {
			createParameter(instanceName + STR_UNCERTAINTY_SUFFIX, Clazz.COST, description, source, year, uncertainty, currency);
			DataProperty.HAS_TEMPORAL_BEHAVIOR.add(this, instanceName, tmpBehavior.getShortName());
		}
	}

	public void createUtility(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String expression, UtilityType utilityType) {
		createParameter(instanceName, Clazz.UTILITY, description, source, year, expression, utilityType.getType());
		DataProperty.HAS_TEMPORAL_BEHAVIOR.add(this, instanceName, tmpBehavior.getShortName());
	}
	
	public void createUtility(String instanceName, String description, String source, TemporalBehavior tmpBehavior, int year, String deterministic, String uncertainty, UtilityType utilityType) {
		createUtility(instanceName, description, source, tmpBehavior, year, deterministic, utilityType);
		if (uncertainty != null && !("".equals(uncertainty))) {
			createParameter(instanceName + STR_UNCERTAINTY_SUFFIX, Clazz.UTILITY, description, source, year, uncertainty, utilityType.getType());
			DataProperty.HAS_TEMPORAL_BEHAVIOR.add(this, instanceName, tmpBehavior.getShortName());
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
	public void createWorkingModel(ModelType type, String author, String description, String geoContext, int year, String reference) {
		type.getClazz().add(this, workingModelInstance);
		DataProperty.HAS_AUTHOR.add(this, workingModelInstance, author);
		DataProperty.HAS_DESCRIPTION.add(this, workingModelInstance, description);
		DataProperty.HAS_GEOGRAPHICAL_CONTEXT.add(this, workingModelInstance, geoContext);
		DataProperty.HAS_REF_TO.add(this, workingModelInstance, reference);
		DataProperty.HAS_YEAR.add(this, workingModelInstance, "" + year);		
	}

	public void createDisease(String instanceName, String description, String refToDO, String refToICD, String refToOMIM, String refToSNOMED) {
		Clazz.DISEASE.add(this, instanceName);		
		DataProperty.HAS_DESCRIPTION.add(this, instanceName, description);
		DataProperty.HAS_REF_TO_DO.add(this,  instanceName, refToDO);
		DataProperty.HAS_REF_TO_ICD.add(this,  instanceName, refToICD);
		DataProperty.HAS_REF_TO_OMIM.add(this,  instanceName, refToOMIM);
		DataProperty.HAS_REF_TO_SNOMED.add(this,  instanceName, refToSNOMED);
		includeInModel(instanceName);
	}
	
	public void createManifestation(String instanceName, ManifestationType type, String description, Set<String> exclusions, String diseaseInstanceName) {
		type.getClazz().add(this, instanceName);		
		DataProperty.HAS_DESCRIPTION.add(this, instanceName, description);
		ObjectProperty.HAS_MANIFESTATION.add(this, diseaseInstanceName, instanceName);
		includeInModel(instanceName);
		for (String excludedManif : exclusions) {
			ObjectProperty.EXCLUDES_MANIFESTATION.add(this, instanceName, getManifestationInstanceName(excludedManif));
		}
	}
	
	public void createGroupOfManifestations(String instanceName, Set<String> manifestationNames) {
		Clazz.GROUP.add(this, instanceName);
		includeInModel(instanceName);
		for (String manifestation : manifestationNames) {
			ObjectProperty.HAS_COMPONENT.add(this, instanceName, getManifestationInstanceName(manifestation));			
			ObjectProperty.BELONGS_TO_GROUP.add(this, getManifestationInstanceName(manifestation), instanceName);			
		}
	}
	
	public void createPopulation(String instanceName, String description, double minAge, double maxAge, int size, int year) {
		Clazz.POPULATION.add(this, instanceName);
		DataProperty.HAS_DESCRIPTION.add(this, instanceName, description);
		DataProperty.HAS_MIN_AGE.add(this, instanceName, "" + minAge);
		DataProperty.HAS_MAX_AGE.add(this, instanceName, "" + maxAge);
		DataProperty.HAS_SIZE.add(this, instanceName, "" + size);
		DataProperty.HAS_YEAR.add(this, instanceName, "" + year);
		
		includeInModel(instanceName);
		
	}
	
	private void includeInModel(String instanceName) {
		ObjectProperty.INCLUDED_BY_MODEL.add(this, instanceName, workingModelInstance);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(this, workingModelInstance, instanceName);
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
		final String strYear = OSDiWrapper.DataProperty.HAS_YEAR.getValue(this, individualIRI, "" + currentYear);
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
			System.out.println(DataProperty.HAS_REF_TO.getValue(wrap, "T1DM_StdModelDES", "NOPE!"));
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
