package es.ull.iis.simulation.hta.osdi.ontology;

/**
 * Individuals defined in the ontology as DataItemTypes. The enum name must be the upper case version of the individual IRI.
 * The DI_UNDEFINED value does not exist in the ontology and is used only in this code.
 * @author Iván Castilla Rodríguez
 *
 */
public enum OSDiDataItemTypes {
	CURRENCY_DOLLAR("Currency_Dollar", 0.0),
	CURRENCY_EURO("Currency_Euro", 0.0),
	CURRENCY_POUND("Currency_Pound", 0.0),
	DI_BIRTH_PREVALENCE("DI_BirthPrevalence", 1.0),
	DI_CONTINUOUS_VARIABLE("DI_Continuous_Variable", 0.0),
	DI_COUNT("DI_Count", 0.0),
	DI_DISUTILITY("DI_Disutility", 0.0),
	DI_FACTOR("DI_Factor", 1.0),
	DI_INCIDENCE("DI_Incidence", 1.0),
	DI_LOWER_95_CONFIDENCE_LIMIT("DI_Lower95ConfidenceLimit", 0.0),
	DI_MEAN_DIFFERENCE("DI_MeanDifference", 0.0),
	DI_OTHER("DI_Other", 0.0),
	DI_PREVALENCE("DI_Prevalence", 1.0),
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
	private OSDiDataItemTypes(String instanceName, double defaultValue) {
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