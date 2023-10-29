package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Parses an expression defined in the ontology
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionWrapper {
	private static final String DIST_NAME = "DISTNAME";
	private static final String DIST_SCALE = "DISTSCALE";
	private static final String DIST_SCALE_SIGN = "DISTSCALESIGN";
	private static final String DIST_OFFSET = "DISTOFFSET";
	private static final String DIST_PARAM1 = "DISTPARAM1";
	private static final String DIST_PARAM2 = "DISTPARAM2";
	private static final String DIST_PARAM3 = "DISTPARAM3";
	private static final String DIST_PARAM4 = "DISTPARAM4";
	private static final String REG_EXP = "^((?<" + 
					DIST_OFFSET + ">[+-]?[0-9]+\\.?[0-9]*)?(?<" +
					DIST_SCALE_SIGN + ">[+-])?((?<" + 
					DIST_SCALE + ">[0-9]+\\.?[0-9]*)\\*)?(?<" + 
					DIST_NAME +">[A-Za-z]+)\\((?<" + 
					DIST_PARAM1 + ">[+-]?[0-9]+\\.?[0-9]*)(,(?<" + 
					DIST_PARAM2 + ">[+-]?[0-9]+\\.?[0-9]*))?(,(?<" + 
					DIST_PARAM3 + ">[+-]?[0-9]+\\.?[0-9]*))?(,(?<" + 
					DIST_PARAM4 + ">[+-]?[0-9]+\\.?[0-9]*))?\\))$";

	private static final String DISTRIBUTION_NAME_SUFFIX = "Variate";
	private static final Pattern PATTERN = Pattern.compile(REG_EXP);
	
	public enum SupportedType {
		CONSTANT,
		PROBABILITY_DISTRIBUTION,
		EXPRESSION_LANGUAGE
	}
	private final double constantValue;
	private final RandomVariate rnd;
	private final String exprToEvaluate;
	private final SupportedType type; 

	public ExpressionWrapper(String expression) throws MalformedOSDiModelException {
		constantValue = parseExpressionAsConstant(expression);
		if (!Double.isNaN(constantValue)) {
			type = SupportedType.CONSTANT;
			rnd = null;
			exprToEvaluate = null;
		}
		else {
			rnd = parseExpressionAsProbabilityDistribution(expression);
			if (rnd != null) {
				type = SupportedType.PROBABILITY_DISTRIBUTION;
				exprToEvaluate = null;				
			}
			else {
				exprToEvaluate = expression;				
				type = SupportedType.EXPRESSION_LANGUAGE;
			}
		}
	}
	
	/**
	 * Processes an expression for an individual and returns a double representation of its value. If the string has a wrong format, returns NaN.  
	 * @param expression A string with a constant expression
	 * @return a double representation of the an expression for an individual
	 */
	private static double parseExpressionAsConstant(String expression) {
		try {
			return Double.parseDouble(expression);
		} catch(NumberFormatException ex) {
			return Double.NaN;
		}		
	}
	
	/**
	 * Initializes a probability distribution from a string in the form [&ltOFFSET&gt + &ltSCALE&gt * ] &ltDISTRIBUTION_NAME&gt (&ltPARAM1&gt, [...])
	 * @param expression
	 */
	private static RandomVariate parseExpressionAsProbabilityDistribution(String expression) {
		String valueNormalized = expression.replace(" ", "");
		Matcher matcher = PATTERN.matcher(valueNormalized);
		try {
			if (matcher.find()) {	
				final String distributionName = matcher.group(DIST_NAME);
				final String[] parameters = new String[4];
				parameters[0] = matcher.group(DIST_PARAM1);
				parameters[1] = matcher.group(DIST_PARAM2);
				parameters[2] = matcher.group(DIST_PARAM3);
				parameters[3] = matcher.group(DIST_PARAM4);
				final RandomVariate rnd = buildDistributionVariate(distributionName, parameters);
				final String strScale = matcher.group(DIST_SCALE);
				final String strOffset = matcher.group(DIST_OFFSET);					
				if (strScale != null || strOffset != null) {
					final String strSign = matcher.group(DIST_SCALE_SIGN);
					final double sign = "-".equals(strSign) ? -1.0 : 1.0;  
					return RandomVariateFactory.getInstance("ScaledVariate", rnd, sign * 
								((strScale == null) ? 1.0 : Double.parseDouble(strScale)), (strOffset == null) ? 0.0 : Double.parseDouble(strOffset));
				}
				return rnd;
			}
			return null;
		}
		catch(IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * @return the constantValue
	 */
	public double getConstantValue() {
		return constantValue;
	}

	/**
	 * @return the rnd
	 */
	public RandomVariate getRnd() {
		return rnd;
	}

	/**
	 * @return the exprToEvaluate
	 */
	public String getExprToEvaluate() {
		return exprToEvaluate;
	}

	/**
	 * @return the type
	 */
	public SupportedType getType() {
		return type;
	}

	/**
	 * 
	 * @param distributionName
	 * @param parameters
	 * @return
	 */
	private static RandomVariate buildDistributionVariate(String distributionName, String[] parameters) {
		int validParams = 3;
		while ((validParams > 0) && parameters[validParams] == null)
			validParams--;
		double []numParams = new double[validParams + 1];
		for (int i = 0; i <= validParams; i++)
			numParams[i] = Double.parseDouble(parameters[i]);
		if (validParams == 3)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1], numParams[2], numParams[3]);
		if (validParams == 2)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1], numParams[2]);
		if (validParams == 1)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1]);
		return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0]);
	}

}
