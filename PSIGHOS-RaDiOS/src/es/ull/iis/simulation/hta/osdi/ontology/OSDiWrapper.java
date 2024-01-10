/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.ontology;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.ontology.OWLOntologyWrapper;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;

/**
 * A wrapper for the OSDi ontology. It provides some useful methods to access the ontology.
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
		DES(OSDiClasses.DISCRETE_EVENT_SIMULATION_MODEL),
		AGENT(OSDiClasses.AGENT_BASED_MODEL),
		MARKOV(OSDiClasses.MARKOV_MODEL),
		DECISION_TREE(OSDiClasses.DECISION_TREE_MODEL);
		private final OSDiClasses clazz;
		private ModelType(OSDiClasses clazz) {
			this.clazz = clazz;
		}
		/**
		 * Returns the associated class in the ontology
		 * @return The associated class in the ontology
		 */
		public OSDiClasses getClazz() {
			return clazz;
		}
	}

	public enum ParameterNature {
		DETERMINISTIC(OSDiClasses.DETERMINISTIC_PARAMETER),
		CALCULATED(OSDiClasses.CALCULATED_PARAMETER),
		FIRST_ORDER(OSDiClasses.FIRST_ORDER_UNCERTAINTY_PARAMETER),
		SECOND_ORDER(OSDiClasses.SECOND_ORDER_UNCERTAINTY_PARAMETER);
		private final OSDiClasses clazz;
		private ParameterNature(OSDiClasses clazz) {
			this.clazz = clazz;
		}
		/**
		 * Returns the associated class in the ontology
		 * @return The associated class in the ontology
		 */
		public OSDiClasses getClazz() {
			return clazz;
		}
	}

	public enum InterventionType {
		DIAGNOSIS(OSDiClasses.DIAGNOSIS_INTERVENTION),
		SCREENING(OSDiClasses.SCREENING_INTERVENTION),
		THERAPEUTIC(OSDiClasses.THERAPEUTIC_INTERVENTION);
		private final OSDiClasses clazz;
		private InterventionType(OSDiClasses clazz) {
			this.clazz = clazz;
		}
		/**
		 * Returns the associated class in the ontology
		 * @return The associated class in the ontology
		 */
		public OSDiClasses getClazz() {
			return clazz;
		}
	}
	
	public enum DiseaseProgressionType {
		ACUTE_MANIFESTATION(OSDiClasses.ACUTE_MANIFESTATION),
		CHRONIC_MANIFESTATION(OSDiClasses.CHRONIC_MANIFESTATION),
		STAGE(OSDiClasses.STAGE);
		private final OSDiClasses clazz;
		private DiseaseProgressionType(OSDiClasses clazz) {
			this.clazz = clazz;
		}
		/**
		 * @return the clazz
		 */
		public OSDiClasses getClazz() {
			return clazz;
		}
	}
	
	public enum ExpressionLanguage {
		JAVALUATOR("Exp_Javaluator"),
		JEXL("Exp_JEXL"),
		JAVA("Exp_Java"),
		EXCEL("Exp_Excel");
		private final String instanceName;
		private ExpressionLanguage(String instanceName) {
			this.instanceName = instanceName;
		}
		/**
		 * @return the instanceName
		 */
		public String getInstanceName() {
			return instanceName;
		}
	}

	private final static String STR_MODIFICATION_SUFFIX = STR_SEP + "Modification";
	private final static TreeMap<OSDiClasses, ModelType> reverseModelType = new TreeMap<>(); 
	private final static TreeMap<OSDiClasses, InterventionType> reverseInterventionType = new TreeMap<>(); 
	private final static TreeMap<String, OSDiDataItemTypes> reverseDataItemType = new TreeMap<>(); 
	static OSDiWrapper currentWrapper = null;

	static {
		reverseModelType.put(OSDiClasses.DISCRETE_EVENT_SIMULATION_MODEL, ModelType.DES);
		reverseModelType.put(OSDiClasses.AGENT_BASED_MODEL, ModelType.AGENT);
		reverseModelType.put(OSDiClasses.MARKOV_MODEL, ModelType.MARKOV);
		reverseModelType.put(OSDiClasses.DECISION_TREE_MODEL, ModelType.DECISION_TREE);
		reverseInterventionType.put(OSDiClasses.DIAGNOSIS_INTERVENTION, InterventionType.DIAGNOSIS);
		reverseInterventionType.put(OSDiClasses.SCREENING_INTERVENTION, InterventionType.SCREENING);
		reverseInterventionType.put(OSDiClasses.THERAPEUTIC_INTERVENTION, InterventionType.THERAPEUTIC);
		for (OSDiDataItemTypes type : OSDiDataItemTypes.values()) {
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
		OSDiWrapper.setCurrentWrapper(this, workingModelName);
		this.parameterWrappers = new TreeMap<>();
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
		OSDiWrapper.setCurrentWrapper(this, workingModelName);
		this.parameterWrappers = new TreeMap<>();
	}

	/**
	 * Gets the current static wrapper, which is unique in the application
	 * @return the currentWrapper
	 */
	public static OSDiWrapper getCurrentWrapper() {
		return currentWrapper;
	}

	/**
	 * Sets the current static wrapper, which will be unique in the application
	 * @param currentWrapper the currentWrapper to set
	 * @param workingModelName The name of the working model
	 */
	public static void setCurrentWrapper(OSDiWrapper currentWrapper, String workingModelName) {
		OSDiWrapper.currentWrapper = currentWrapper;
		currentWrapper.setWorkingModelInstance(workingModelName);
	}

	/**
	 * Gets the instance of the working model
	 * @return the instance of the working model
	 */
	public String getWorkingModelInstance() {
		return workingModelInstance;
	}
	
	/**
	 * Sets the instance of the working model
	 * @param workingModelName The name of the working model
	 */
	public void setWorkingModelInstance(String workingModelName) {
		this.workingModelInstance = instancePrefix + workingModelName;
		modelItems.clear();
		modelItems.addAll(OSDiObjectProperties.INCLUDES_MODEL_ITEM.getValues(this.workingModelInstance));
	}
	
	/**
	 * Returns the items belonging to the working model
	 * @return the items belonging to the working model
	 */
	public Set<String> getModelItems() {
		return modelItems;
	}
	
	/**
	 * @return the parameterWrappers
	 */
	public ParameterWrapper getParameterWrapper(String parameterIRI, String defaultDescription) throws MalformedOSDiModelException {
		if (parameterWrappers.containsKey(parameterIRI))
			return parameterWrappers.get(parameterIRI);
		if (OSDiObjectProperties.HAS_DATA_ITEM_TYPE.getValues(parameterIRI).size() > 1) {
			printWarning(parameterIRI, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Parameter has more than one data item type. Using the first one");
		}
		ParameterWrapper wrapper = new ParameterWrapper(this, parameterIRI, defaultDescription);
		parameterWrappers.put(wrapper.getOriginalIndividualIRI(), wrapper);
		return wrapper;
	}

	public ParameterModifierWrapper getParameterModifierWrapper(String parameterIRI, Intervention intervention) throws MalformedOSDiModelException {
		if (parameterWrappers.containsKey(parameterIRI))
			return (ParameterModifierWrapper) parameterWrappers.get(parameterIRI);
		if (OSDiObjectProperties.HAS_DATA_ITEM_TYPE.getValues(parameterIRI).size() > 1) {
			printWarning(parameterIRI, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Parameter has more than one data item type. Using the first one");
		}
		ParameterModifierWrapper wrapper = new ParameterModifierWrapper(this, parameterIRI, intervention);
		parameterWrappers.put(wrapper.getOriginalIndividualIRI(), wrapper);
		return wrapper;
	}

	public Collection<ParameterWrapper> getParameterWrappers() {
		return parameterWrappers.values();
	}
	
	public static OSDiDataItemTypes getDataItemType(String individualIRI) {
		try {
			return reverseDataItemType.get(individualIRI);
		} catch(IllegalArgumentException ex) {
			return OSDiDataItemTypes.DI_UNDEFINED;
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
		final String strYear = OSDiDataProperties.HAS_YEAR.getValue(individualIRI, "" + currentYear);
		try {
			currentYear = Integer.parseInt(strYear);
		} catch(NumberFormatException ex) {
			printWarning(individualIRI, OSDiDataProperties.HAS_YEAR, "Wrong year format. Found " + strYear + ". Using " + currentYear + " instead");
		}
		return currentYear;
		
	}
	
	public void printWarning(String individualIRI, OSDiDataProperties prop, String msg) {
		printWarning(individualIRI + "\t" + prop.getShortName() + "\tWARNING\t\"" + msg + "\"");
	}
	
	public void printWarning(String individualIRI, OSDiObjectProperties prop, String msg) {
		printWarning(individualIRI + "\t" + prop.getShortName() + "\tWARNING\t\"" + msg + "\"");
	}
	
	public void printWarning(String msg) {
		if (ENABLE_WARNINGS)
			System.err.println("WARNING: " + msg);
	}
	
	public void printWarning(boolean condition, String individualIRI, OSDiDataProperties prop, String msg) {
		if (condition)
			printWarning(individualIRI, prop, msg);
	}
	
	public void printWarning(boolean condition, String individualIRI, OSDiObjectProperties prop, String msg) {
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
