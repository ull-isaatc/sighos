/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.expressionEvaluators;

import java.util.regex.Pattern;

import com.fathzer.soft.javaluator.AbstractVariableSet;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class JavaluatorPatient implements AbstractVariableSet<Double> {
	private final Patient pat;

	/**
	 * 
	 */
	public JavaluatorPatient(Patient pat) {
		this.pat = pat;
	}

	@Override
	public Double get(String variableName) {
		final String Digits     = "(\\p{Digit}+)";
  		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally
		// signed decimal integer.
		final String Exp = "[eE][+-]?"+Digits;
		final String fpRegex = ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
       "[+-]?(" + // Optional sign character
       "NaN|" +           // "NaN" string
       "Infinity|" +      // "Infinity" string
       "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
       // . Digits ExponentPart_opt FloatTypeSuffix_opt
       "(\\.("+Digits+")("+Exp+")?)|"+
       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +
        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
        ")[pP][+-]?" + Digits + "))" +
       "[fFdD]?))" +
       "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (Pattern.matches(fpRegex, variableName))
			return Double.valueOf(variableName); // Will not throw NumberFormatException
		if ("AGE".equals(variableName))
			return pat.getAge();
		if ("SEX".equals(variableName))
			return (double) pat.getSex();
		if ("DIAGNOSED".equals(variableName))
			return pat.isDiagnosed() ? 1.0 : 0.0;
		if ("INTERVENTION".equals(variableName))
			return (double) pat.getnIntervention();
		double paramValue = pat.getSimulation().getModel().getParameterValue(variableName, pat);
		if (!Double.isNaN(paramValue))
			return paramValue;
		return Double.NaN;
	}

}
