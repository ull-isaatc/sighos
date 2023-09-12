/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import es.ull.iis.ontology.OWLOntologyWrapper;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class OSDiWrapper extends OWLOntologyWrapper {
	private final static String PREFIX = "http://www.ull.es/iis/simulation/ontologies/disease-simulation#";

	/**
	 * @param file
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(File file) throws OWLOntologyCreationException {
		super(file, PREFIX);
	}

	/**
	 * @param path
	 * @throws OWLOntologyCreationException
	 */
	public OSDiWrapper(String path) throws OWLOntologyCreationException {
		super(path, PREFIX);
	}

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
		 * @return the clazz
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
		
		public ArrayList<String> getValues(OSDiWrapper wrap, String individualIRI) {
			return wrap.getDataPropertyValue(individualIRI, shortName);
		}
	}

	public enum ObjectProperty {
		BELONGS_TO_GROUP("belongsToGroup"),
		EXCLUDES_MANIFESTATION("excludesManifestation"),
		FOLLOWED_BY_STRATEGY("followedByStrategy"),
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
		HAS_LIFETIME_REDUCTION("hasLifetimeReduction"),
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
	
		public void add(OSDiWrapper wrap, String srcIndividualIRI, String destIndividualIRI) {
			wrap.addObjectPropertyValue(srcIndividualIRI, shortName, destIndividualIRI);
		}
		
		public ArrayList<String> getValues(OSDiWrapper wrap, String individualIRI) {
			return wrap.getObjectPropertyValue(individualIRI, shortName);
		}
	}

	public void createModel(String name, ModelType type, String author, String description, String geoContext, int year, String reference) {
		type.getClazz().add(this, name);
		DataProperty.HAS_AUTHOR.add(this, name, author);
		DataProperty.HAS_DESCRIPTION.add(this, name, description);
		DataProperty.HAS_GEOGRAPHICAL_CONTEXT.add(this, name, geoContext);
		DataProperty.HAS_REF_TO.add(this, name, reference);
		DataProperty.HAS_YEAR.add(this, name, "" + year);		
	}

	public void createDisease(String name, String description, String modelName, String refToDO, String refToICD, String refToOMIM, String refToSNOMED) {
		Clazz.DISEASE.add(this, name);		
		DataProperty.HAS_DESCRIPTION.add(this, name, description);
		DataProperty.HAS_REF_TO_DO.add(this,  name, refToDO);
		DataProperty.HAS_REF_TO_ICD.add(this,  name, refToICD);
		DataProperty.HAS_REF_TO_OMIM.add(this,  name, refToOMIM);
		DataProperty.HAS_REF_TO_SNOMED.add(this,  name, refToSNOMED);
		ObjectProperty.INCLUDED_BY_MODEL.add(this, name, modelName);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(this, modelName, name);
	}
	
	public void createManifestation(String name, ManifestationType type, String description, String modelName, String diseaseName) {
		type.getClazz().add(this, name);		
		DataProperty.HAS_DESCRIPTION.add(this, name, description);
		ObjectProperty.HAS_MANIFESTATION.add(this, diseaseName, name);
		ObjectProperty.INCLUDED_BY_MODEL.add(this, name, modelName);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(this, modelName, name);
	}
	
	public void createGroupOfManifestations(String name, String modelName, Set<String> manifestationNames) {
		Clazz.GROUP.add(this, name);		
		ObjectProperty.INCLUDED_BY_MODEL.add(this, name, modelName);
		ObjectProperty.INCLUDES_MODEL_ITEM.add(this, modelName, name);
		for (String manifestation : manifestationNames) {
			ObjectProperty.HAS_COMPONENT.add(this, name, manifestation);			
			ObjectProperty.BELONGS_TO_GROUP.add(this, manifestation, name);			
		}
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
	
	public static void printEverythingAsEnum(String path) {
		try {
			final OSDiWrapper wrap = new OSDiWrapper(path);

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
	 * @param args
	 */
	public static void main(String[] args) {
		printEverythingAsEnum("resources/OSDi.owl");
//		try {
//			final OSDiWrapper wrap = new OSDiWrapper("resources/OSDi.owl");
//			for (String str : DataProperty.HAS_REF_TO_SNOMED.getValues(wrap, "T1DM_Disease")) {
//				System.out.println(str);
//			}
//			for (String str : ObjectProperty.HAS_MANIFESTATION.getValues(wrap, "T1DM_Disease")) {
//				System.out.println(str);
//			}

//			for (String  str : wrap.getEnglishCommentForClass(wrap.getObjectProperty(ObjectProperty.BELONGS_TO_AREA.getShortName()).getIRI()))
//				System.out.println(str);
//		} catch (OWLOntologyCreationException e) {
//			e.printStackTrace();
//		}
	}
	
}
