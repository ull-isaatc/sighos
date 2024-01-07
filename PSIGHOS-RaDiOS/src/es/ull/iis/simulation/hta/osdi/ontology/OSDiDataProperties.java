package es.ull.iis.simulation.hta.osdi.ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.vocab.OWL2Datatype;

public enum OSDiDataProperties {
	APPLIES_ONE_TIME("appliesOneTime"),
	FAILS_IF("failsIf"),
	HAS_ALFA_PARAMETER("hasAlfaParameter"),
	HAS_AUTHOR("hasAuthor"),
	HAS_AVERAGE_PARAMETER("hasAverageParameter"),
	HAS_BETA_PARAMETER("hasBetaParameter"),
	HAS_CALCULATION_METHOD("hasCalculationMethod"),
	HAS_DESCRIPTION("hasDescription"),
	HAS_DISUTILITY_COMBINATION_METHOD("hasDisutilityCombinationMethod"),
	HAS_EXPECTED_VALUE("hasExpectedValue"),
	HAS_EXPRESSION_VALUE("hasExpressionValue"),
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
	HAS_UNIT("hasUnit"),
	HAS_UPPER_LIMIT_PARAMETER("hasUpperLimitParameter"),
	HAS_YEAR("hasYear"),
	IS_TRUE_EPIDEMIOLOGICAL_PARAMETER_ESTIMATE("isTrueEpidemiologicalParameterEstimate"),
	TOP_DATA_PROPERTY("topDataProperty");
	
	private final String shortName;
	private OSDiDataProperties(String shortName) {
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
			OSDiWrapper.currentWrapper.addDataPropertyValue(individualIRI, shortName, value);
		}
	}
	
	public void add(String individualIRI, String value, OWL2Datatype dataType) {
		if(!("".equals(value))) {
			OSDiWrapper.currentWrapper.addDataPropertyValue(individualIRI, shortName, value, dataType);
		}
	}

	public String getValue(String individualIRI) {
		return getValue(individualIRI, null);
	}

	public String getValue(String individualIRI, String defaultValue) {
		ArrayList<String> values = getValues(individualIRI);
		if (values.size() > 1)
			OSDiWrapper.currentWrapper.printWarning(individualIRI, this, "Found more than one value for the data property. Using only " + values.toArray()[0]);
		if (values.size() == 0)
			return defaultValue;
		return values.get(0);
	}

	public ArrayList<String> getValues(String individualIRI) {
		return OSDiWrapper.currentWrapper.getDataPropertyValue(individualIRI, shortName);
	}
}