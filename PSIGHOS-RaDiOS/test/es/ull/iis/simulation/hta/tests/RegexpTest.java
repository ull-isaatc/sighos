package es.ull.iis.simulation.hta.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpTest {
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

	private static final Pattern PATTERN = Pattern.compile(REG_EXP);
	
	public static void main(String[] args) {
		final String [] testTexts = new String[] {"Normal(0.5, 1.23)", "3-2*Beta(2, 56)"};
		for (String testText : testTexts) {
			String valueNormalized = testText.replace(" ", "");
			Matcher matcher = PATTERN.matcher(valueNormalized);
			try {
				if (matcher.find()) {	
					System.out.println("Original string: " + testText);
					System.out.println("Distribution name: " + matcher.group(DIST_NAME));
					System.out.println("Parameter 1: " + matcher.group(DIST_PARAM1));
					System.out.println("Parameter 2: " + matcher.group(DIST_PARAM2));
					System.out.println("Parameter 3: " + matcher.group(DIST_PARAM3));
					System.out.println("Parameter 4: " + matcher.group(DIST_PARAM4));
					System.out.println("Scale: " + matcher.group(DIST_SCALE));
					System.out.println("Scale sign: " + matcher.group(DIST_SCALE_SIGN));
					System.out.println("Offset: " + matcher.group(DIST_OFFSET));
				}
				else
					System.err.println("String " + testText + " did not match the regular expression");
			}
			catch(IllegalArgumentException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}
}


