package es.ull.iis.simulation.hta.osdi.ontology;

import java.util.Set;

public enum OSDiObjectProperties {
	BELONGS_TO_GROUP("belongsToGroup"),
	DEPENDS_ON_ATTRIBUTE("dependsOnAttribute"),
	DEPENDS_ON_PARAMETER("dependsOnParameter"),
	EXCLUDES_MANIFESTATION("excludesManifestation"),
	FOLLOWED_BY_STRATEGY("followedByStrategy"),
	HAS_AGE("hasAge"),
	HAS_ATTRIBUTE_VALUE("hasAttributeValue"),
	HAS_COMPONENT("hasComponent"),
	HAS_CONDITION_EXPRESSION("hasConditionExpression"),
	HAS_COST("hasCost"),
	HAS_DATA_ITEM_TYPE("hasDataItemType"),
	HAS_DEVELOPMENT("hasDevelopment"),
	HAS_DIAGNOSIS_COST("hasDiagnosisCost"),
	HAS_DIAGNOSIS_STRATEGY("hasDiagnosisStrategy"),
	HAS_DURATION("hasDuration"),
	HAS_END_AGE("hasEndAge"),
	HAS_EPIDEMIOLOGICAL_PARAMETER("hasEpidemiologicalParameter"),
	HAS_EXPRESSION("hasExpression"),
	HAS_EXPRESSION_LANGUAGE("hasExpressionLanguage"),
	HAS_FOLLOW_UP_COST("hasFollowUpCost"),
	HAS_FOLLOW_UP_STRATEGY("hasFollowUpStrategy"),
	HAS_GUIDELINE("hasGuideline"),
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
	HAS_PROBABILITY_OF_DEATH("hasProbabilityOfDeath"),
	HAS_PROBABILITY_OF_DIAGNOSIS("hasProbabilityOfDiagnosis"),
	HAS_PROPORTION_WITHIN_GROUP("hasProportionWithinGroup"),
	HAS_RISK_CHARACTERIZATION("hasRiskCharacterization"),
	HAS_SCREENING_STRATEGY("hasScreeningStrategy"),
	HAS_SENSITIVITY("hasSensitivity"),
	HAS_SEX("hasSex"),
	HAS_SPECIFICITY("hasSpecificity"),
	HAS_STAGE("hasStage"),
	HAS_STRATEGY("hasStrategy"),
	HAS_SUBPOPULATION("hasSubpopulation"),
	HAS_TEMPORARY_THRESHOLD("hasTemporaryThreshold"),
	HAS_TREATMENT_COST("hasTreatmentCost"),
	HAS_UNCERTAINTY_CHARACTERIZATION("hasUncertaintyCharacterization"),
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
	private OSDiObjectProperties(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	public void add(String srcIndividualIRI, String destIndividualIRI) {
		OSDiWrapper.currentWrapper.addObjectPropertyValue(srcIndividualIRI, shortName, destIndividualIRI);
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
			OSDiWrapper.currentWrapper.printWarning(individualIRI, this, "Found more than one value for the object property. Using only " + values.toArray()[0]);
		if (values.size() == 0)
			return null;
		return (String)values.toArray()[0];
	}
	
	public Set<String> getValues(String individualIRI) {
		return getValues(individualIRI, false);
	}
	
	public Set<String> getValues(String individualIRI, boolean restrictToWorkingModel) {
		final Set<String> results = OSDiWrapper.currentWrapper.getObjectPropertyValue(individualIRI, shortName);
		if (restrictToWorkingModel)
			results.retainAll(OSDiWrapper.currentWrapper.getModelItems());
		return results;
	}
}