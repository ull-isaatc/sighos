package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;

import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import simkit.random.RandomVariate;

/**
 * Parses an expression defined in the ontology
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionWrapper {
	public enum SupportedType {
		CONSTANT,
		PROBABILITY_DISTRIBUTION,
		EXPRESSION_LANGUAGE,
		UNKNOWN
	}
	private final double constantValue;
	private final RandomVariate rnd;
	private final JexlExpression exprToEvaluate;
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
				exprToEvaluate = parseExpressionAsExpressionLanguage(expression);				
				if (exprToEvaluate == null) {
					type = SupportedType.UNKNOWN;					
					throw new MalformedOSDiModelException("Expression does not match with a constant, probability distribution or valid expression language: " + expression);
				}
				else {
					type = SupportedType.EXPRESSION_LANGUAGE;
				}
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
	
	private static RandomVariate parseExpressionAsProbabilityDistribution(String expression) {
		try {
			return ProbabilityDistribution.getInstanceFromExpression(expression);
		} catch (MalformedOSDiModelException e) {
			return null;
		}		
	}
	
	private static JexlExpression parseExpressionAsExpressionLanguage(String expression) {
		try {
			return OSDiGenericRepository.JEXL.createExpression(expression);
		} catch(JexlException ex) {
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
	public JexlExpression getExprToEvaluate() {
		return exprToEvaluate;
	}

	/**
	 * @return the type
	 */
	public SupportedType getType() {
		return type;
	}


}
