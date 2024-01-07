package es.ull.iis.simulation.hta.osdi.ontology;

import java.util.Set;

public enum OSDiClasses {
	ACUTE_MANIFESTATION("AcuteManifestation"),
	AGENT_BASED_MODEL("AgentBasedModel"),
	ATTRIBUTE("Attribute"),
	BERNOULLI_DISTRIBUTION_EXPRESSION("BernoulliDistributionExpression"),
	BETA_DISTRIBUTION_EXPRESSION("BetaDistributionExpression"),
	BIRTH_PREVALENCE("BirthPrevalence"),
	CALCULATED_PARAMETER("CalculatedParameter"),
	CHRONIC_MANIFESTATION("ChronicManifestation"),
	CONDITION_EXPRESSION("ConditionExpression"),
	COST("Cost"),
	CURRENCY("Currency"),
	DATA_ITEM_TYPE("DataItemType"),
	DECISION_TREE_MODEL("DecisionTreeModel"),
	DETECTION_INTERVENTION("DetectionIntervention"),
	DETECTION_STRATEGY("DetectionStrategy"),
	DETERMINISTIC_PARAMETER("DeterministicParameter"),
	DEVELOPMENT("Development"),
	DEVELOPMENT_PATHWAY("DevelopmentPathway"),
	DIAGNOSIS_INTERVENTION("DiagnosisIntervention"),
	DIAGNOSIS_STRATEGY("DiagnosisStrategy"),
	DISCRETE_EVENT_SIMULATION_MODEL("DiscreteEventSimulationModel"),
	DISEASE("Disease"),
	DISEASE_PATHWAY("DiseasePathway"),
	DISEASE_PROGRESSION("DiseaseProgression"),
	DISEASE_PROGRESSION_PATHWAY("DiseaseProgressionPathway"),
	DRUG("Drug"),
	EPIDEMIOLOGICAL_PARAMETER("EpidemiologicalParameter"),
	EXPONENTIAL_DISTRIBUTION_EXPRESSION("ExponentialDistributionExpression"),
	EXPRESSION_LANGUAGE("ExpressionLanguage"),
	FIRST_ORDER_UNCERTAINTY_PARAMETER("FirstOrderUncertaintyParameter"),
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
	MARKOV_MODEL("MarkovModel"),
	MEASURED_DATA_ITEM_TYPE("MeasuredDataItemType"),
	MODEL("Model"),
	MODEL_ITEM("ModelItem"),
	NORMAL_DISTRIBUTION_EXPRESSION("NormalDistributionExpression"),
	PARAMETER("Parameter"),
	PARAMETER_NATURE("ParameterNature"),
	PATHWAY("Pathway"),
	POISSON_DISTRIBUTION_EXPRESSION("PoissonDistributionExpression"),
	POPULATION("Population"),
	PREVALENCE("Prevalence"),
	PROBABILITY_DISTRIBUTION_EXPRESSION("ProbabilityDistributionExpression"),
	PROPORTION_WITHIN_GROUP("ProportionWithinGroup"),
	RARE_DISEASE("RareDisease"),
	SCREENING_INTERVENTION("ScreeningIntervention"),
	SCREENING_STRATEGY("ScreeningStrategy"),
	SECOND_ORDER_UNCERTAINTY_PARAMETER("SecondOrderUncertaintyParameter"),
	STAGE("Stage"),
	STRATEGY("Strategy"),
	THERAPEUTIC_INTERVENTION("TherapeuticIntervention"),
	TREATMENT("Treatment"),
	UNIFORM_DISTRIBUTION_EXPRESSION("UniformDistributionExpression"),
	UTILITY("Utility");
	/** The short name that is used as IRI of this class in the ontology */
	private final String shortName;
	private OSDiClasses(String shortName) {
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
		OSDiWrapper.currentWrapper.addIndividual(shortName, individualIRI);
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
		final Set<String> results = OSDiWrapper.currentWrapper.getIndividuals(shortName);
		if (restrictToWorkingModel)
			results.retainAll(OSDiWrapper.currentWrapper.getModelItems());
		return results;
	}
	
	/**
	 * Returns true if the specified individual is an instance of this class (or any of its subclasses)
	 * @param individualIRI The IRI of an individual in the ontology
	 * @return true if the specified individual is an instance of this class (or any of its subclasses)
	 */
	public boolean containsIntance(String individualIRI) {
		return OSDiWrapper.currentWrapper.isInstanceOf(individualIRI, shortName);
	}
}