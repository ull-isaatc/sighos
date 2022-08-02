package es.ull.iis.simulation.hta.osdi.utils;

public interface Constants {
	public static String CONSTANT_DO_NOTHING = "DO_NOTHING";
	public static String CONSTANT_EMPTY_STRING = "";
	public static String CONSTANT_EMPTY_JSON = "{}";
	public static String CONSTANT_DEFAULT_SPACES_SHOW_NODE = "      ";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_3DEC = "%.3f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_6DEC = "%.6f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_9DEC = "%.9f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_12DEC = "%.12f";
	public static String CONSTANT_DOUBLE_TYPE = "#double";
	public static String CONSTANT_STRING_TYPE = "#string";
	public static String CONSTANT_HASHTAG = "#";
	public static String CONSTANT_NEGATION = "!";
	public static String CONSTANT_UNDEFINED_TYPE = "#string";
	public static String CONSTANT_SPLIT_TYPE = "::";
	public static String CONSTANT_DECISION_NODE = "(*)";
	public static String CONSTANT_SENSITIVITY_100 = "1.0";
	public static String CONSTANT_SPECIFICITY_100 = "1.0";
	public static String CONSTANT_VALUE_ZERO = "0.0";
	public static String CONSTANT_VALUE_ONE = "1.0";
	public static String CONSTANT_DISTRUBUTION_SUFFIX = "Distribution";
	
	public static String REGEX_ANYEXPRESION_TYPE = ".*::[#a-zA-Z0-9(),]+";
	public static String REGEX_NUMERICVALUE_DISTRO = "[0-9.,E-]+#[a-zA-Z0-9(),.#]+";
	public static String REGEX_OPERATION_NUMERICVALUE = "[*/+-][0-9.,E-]+";
   public static String REGEX_NUMERICVALUE = "[0-9.,E-]+";	


	// Data properties that do not belong to the ontology, but are generated on the fly
	public static String CUSTOM_PROPERTY_CUMULATIVE_COST = "#hasCumulativeCost";
	public static String CUSTOM_PROPERTY_CUMULATIVE_PROBABILITY = "#hasCumulativeProbability";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM_WITH_DISCOUNT = "#hasUtilityValueMinimumWithDiscount";
	
	public static String UTILITY_DEFAULT_CALCULATION_METHOD = "Unknown";
}
