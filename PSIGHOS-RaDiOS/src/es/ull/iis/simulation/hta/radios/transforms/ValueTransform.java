package es.ull.iis.simulation.hta.radios.transforms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;

import es.tenerife.ull.ontology.radios.Constants;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class ValueTransform {
	private static int DETERMINISTIC_VALUE_POS = 2;
	private static int DISTRIBUTION_NAME_POS = 3;
	private static int FIRST_PARAM_4_DISTRIBUTION_POS = 4;
	private static int SECOND_PARAM_4_DISTRIBUTION_POS = 6;

	private static String DISTRIBUTION_NAME_SUFFIX = "Variate";
	
   private static RandomVariate buildDistributionVariate(String distributionName, String firstParameter, String secondParameter) {
   	if (distributionName.toUpperCase().contains("EXP")) {
   		distributionName = "Exponential";
   	}
   	
   	if (secondParameter != null) {
   		RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, new Double(firstParameter), new Double(secondParameter));
   	} else {
   		RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, new Double(firstParameter));
   	}
		return null;   	
   }
	
	public static ProbabilityDistribution splitProbabilityDistribution (String value) {
		ProbabilityDistribution result = null;		
		String valueNormalized = value.toUpperCase().replace(" ", "");
		Pattern pattern = Pattern.compile(Constants.REGEX_NUMERICVALUE_DISTRO_EXTENDED);
		Matcher matcher = pattern.matcher(valueNormalized);
		if (matcher.find()) {			
			Double deterministicValue = (matcher.group(DETERMINISTIC_VALUE_POS) != null) ? new Double(matcher.group(DETERMINISTIC_VALUE_POS)) : null;
			String distributionName = matcher.group(DISTRIBUTION_NAME_POS);
			String firstParameter = matcher.group(FIRST_PARAM_4_DISTRIBUTION_POS);
			String secondParameter = matcher.group(SECOND_PARAM_4_DISTRIBUTION_POS);
			System.out.println("Deterministic value: " + deterministicValue + "\t\t" + distributionName + " -> " + firstParameter + " -> " + secondParameter + "\n");
			result = new ProbabilityDistribution(deterministicValue, buildDistributionVariate(distributionName, firstParameter, secondParameter));
		}
		return result;		
	}
}
