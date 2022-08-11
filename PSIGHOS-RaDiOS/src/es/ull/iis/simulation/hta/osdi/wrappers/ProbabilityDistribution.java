package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;

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
	private static final String REG_EXP = 
			"^(?<"+ DET_VALUE + ">[0-9\\.,E-]+)?(#?(?<" + 
					DIST_OFFSET + ">[+-]?[0-9]+\\.?[0-9]*)?(?<" +
					DIST_SCALE_SIGN + ">[+-])?((?<" + 
					DIST_SCALE + ">[0-9]+\\.?[0-9]*)\\*)?(?<" + 
					DIST_NAME +">[A-Z]+)\\((?<" + 
					DIST_PARAM1 + ">[+-]?[0-9]+\\.?[0-9]*)(,(?<" + 
					DIST_PARAM2 + ">[+-]?[0-9]+\\.?[0-9]*))?\\))?$";

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
		String valueNormalized = text.toUpperCase().replace(" ", "");
		Matcher matcher = PATTERN.matcher(valueNormalized);
		try {
			if (matcher.find()) {	
				final String detValue = matcher.group(DET_VALUE);
				deterministicValue = (detValue == null) ? null : Double.parseDouble(detValue);
				final String distributionName = matcher.group(DIST_NAME);
				if (distributionName == null)
					probabilisticValue = RandomVariateFactory.getInstance("ConstantVariate", deterministicValue);
				else {
					final String firstParameter = matcher.group(DIST_PARAM1);
					final String secondParameter = matcher.group(DIST_PARAM2);
					final RandomVariate rnd = buildDistributionVariate(distributionName, firstParameter, secondParameter);
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
	private static RandomVariate buildDistributionVariate(String distributionName, String firstParameter, String secondParameter) {
		if (distributionName.contains("EXP")) {
			distributionName = "Exponential";
		}
	   	if (secondParameter != null) {
	   		return RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, Double.parseDouble(firstParameter), Double.parseDouble(secondParameter));
	   	} else {
	   		return RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, Double.parseDouble(firstParameter));
	   	}
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
