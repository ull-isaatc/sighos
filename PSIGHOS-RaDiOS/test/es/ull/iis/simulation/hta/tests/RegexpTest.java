package es.ull.iis.simulation.hta.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpTest {
	private static final String REGEXP = "^([*+-/])([0-9]+(\\.[0-9]+)?)$";
	private static Pattern pattern = Pattern.compile(REGEXP);

	public static void main(String[] args) {
		Matcher matcher = pattern.matcher("*0.0".trim());
		if (matcher.find()) {
			System.out.println(matcher.group(1) + " -- " + matcher.group(2));
		}

		matcher = pattern.matcher("+0.0".trim());
		if (matcher.find()) {
			System.out.println(matcher.group(1) + " -- " + matcher.group(2));
		}
		
		System.out.println(Double.parseDouble("0"));
	}
}
