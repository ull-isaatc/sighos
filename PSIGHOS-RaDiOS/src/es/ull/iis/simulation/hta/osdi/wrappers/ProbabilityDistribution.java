package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A wrapper for probability distributions together with deterministic values
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 *
 */
public class ProbabilityDistribution {
	private static final String DET_VALUE = "DETVALUE";
	private static final String DIST_NAME = "DISTNAME";
	private static final String DIST_SCALE = "DISTSCALE";
	private static final String DIST_SCALE_SIGN = "DISTSCALESIGN";
	private static final String DIST_OFFSET = "DISTOFFSET";
	private static final String DIST_PARAM1 = "DISTPARAM1";
	private static final String DIST_PARAM2 = "DISTPARAM2";
	private static final String DIST_PARAM3 = "DISTPARAM3";
	private static final String DIST_PARAM4 = "DISTPARAM4";
	private static final String REG_EXP = 
			"^(?<"+ DET_VALUE + ">[0-9\\.,E-]+)?(#?(?<" + 
					DIST_OFFSET + ">[+-]?[0-9]+\\.?[0-9]*)?(?<" +
					DIST_SCALE_SIGN + ">[+-])?((?<" + 
					DIST_SCALE + ">[0-9]+\\.?[0-9]*)\\*)?(?<" + 
					DIST_NAME +">[A-Za-z]+)\\((?<" + 
					DIST_PARAM1 + ">[+-]?[0-9]+\\.?[0-9]*)(,(?<" + 
					DIST_PARAM2 + ">[+-]?[0-9]+\\.?[0-9]*))?(,(?<" + 
					DIST_PARAM3 + ">[+-]?[0-9]+\\.?[0-9]*))?(,(?<" + 
					DIST_PARAM4 + ">[+-]?[0-9]+\\.?[0-9]*))?\\))?$";

	private static final String DISTRIBUTION_NAME_SUFFIX = "Variate";
	private static final Pattern PATTERN = Pattern.compile(REG_EXP);

	private final Double deterministicValue;
	private final RandomVariate probabilisticValue;

	/**
	 * Initializes a probability distribution with both the deterministic and probabilistic values
	 * @param deterministicValue
	 * @param probabilisticValue
	 */
	public ProbabilityDistribution(Double deterministicValue, RandomVariate probabilisticValue) {
		this.deterministicValue = deterministicValue;
		this.probabilisticValue = probabilisticValue;
	}

	/**
	 * Initializes a probability distribution from a string in the form &ltdeterministic_value#probabilistic_distribution&gt
	 * FIXME: By default, a constant variate is assigned if not defined. Depending on the type of parameter, it would be desirable to use specific distributions by default
	 * @param text
	 */
	public ProbabilityDistribution(String text) throws TranspilerException {
		String valueNormalized = text.replace(" ", "");
		Matcher matcher = PATTERN.matcher(valueNormalized);
		try {
			if (matcher.find()) {	
				final String detValue = matcher.group(DET_VALUE);
				deterministicValue = (detValue == null) ? null : Double.parseDouble(detValue);
				final String distributionName = matcher.group(DIST_NAME);
				if (distributionName == null)
					probabilisticValue = RandomVariateFactory.getInstance("ConstantVariate", deterministicValue);
				else {
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
						probabilisticValue = RandomVariateFactory.getInstance("ScaledVariate", rnd, sign * 
									((strScale == null) ? 1.0 : Double.parseDouble(strScale)), (strOffset == null) ? 0.0 : Double.parseDouble(strOffset));
					}
					else
						probabilisticValue = rnd;
				}
			}
			else
				throw new TranspilerException("Error parsing probability distribution " + text);
		}
		catch(IllegalArgumentException ex) {
			throw new TranspilerException("Error parsing probability distribution " + text, ex);			
		}
	}

	/**
	 * 
	 * @param detValue
	 * @param distributionName
	 * @param firstParameter
	 * @param secondParameter
	 * @return
	 * @throws TranspilerException
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
	
	
	public Double getDeterministicValue() {
		return deterministicValue;
	}

	public RandomVariate getProbabilisticValue() {
		return probabilisticValue;
	}

	// FIXME: Currently, this function is useless as far as ValuePArser initializes the probabilisticValue by default to constant
	public RandomVariate getProbabilisticValueInitializedForProbability() {
		if (probabilisticValue == null && deterministicValue != null) {
			if (deterministicValue == 0.0 || deterministicValue == 1.0) {
				return RandomVariateFactory.getInstance("ConstantVariate", getDeterministicValue());
			} else {
				return SecondOrderParamsRepository.getRandomVariateForProbability(getDeterministicValue());
			}
		}
		return probabilisticValue;
	}

	// FIXME: Currently, this function is useless as far as ValuePArser initializes the probabilisticValue by default to constant
	public RandomVariate getProbabilisticValueInitializedForCost() {
		if (probabilisticValue == null && deterministicValue != null) {
			return SecondOrderParamsRepository.getRandomVariateForCost(getDeterministicValue());
		}
		return probabilisticValue;
	}

	private String getDeterministicValueOrEmpty () {
		return getDeterministicValue() != null ? getDeterministicValue().toString() : "";
	}
	
	private String getProbabilisticValueOrEmpty () {
		return getProbabilisticValue() != null ? getProbabilisticValue().toString() : "";
	}
	
	@Override
	public String toString() {
		if (getDeterministicValue() != null && getProbabilisticValue() != null) {
			return String.format("%s#%s", getDeterministicValueOrEmpty(), getProbabilisticValueOrEmpty()); 
		} else if (getDeterministicValue() != null) {
			return String.format("%s", getDeterministicValueOrEmpty()); 
		} else if (getProbabilisticValue() != null) {
			return String.format("%s", getProbabilisticValueOrEmpty()); 
		} else {
			return "";
		}
	}
}
