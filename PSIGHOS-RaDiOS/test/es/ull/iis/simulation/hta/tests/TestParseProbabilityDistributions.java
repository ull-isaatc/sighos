/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import simkit.random.RandomVariate;

/**
 * @author masbe
 *
 */
public class TestParseProbabilityDistributions {
	private static class TestItem {
		public final String text;
		public final boolean causeError;
		public final Double detValue;
		public final String dist;
		
		public TestItem(String text, boolean causeError, Double detValue) {
			this(text, causeError, detValue, null);
		}
		public TestItem(String text, boolean causeError, String dist) {
			this(text, causeError, null, dist);			
		}
		public TestItem(String text, boolean causeError, Double detValue, String dist) {
			this.text = text;
			this.causeError = causeError;
			this.detValue = detValue;
			this.dist = dist;
		}		
		
		private boolean checkDeterministicPart(Double obtainedValue) {
			if (obtainedValue == null) {
				if (detValue != null) {
					TestUtils.printErrorMessage("Testing " + text + ".\tDeterministic value does not match. Got null, should be: " + detValue);
					return false;
				}
			}
			else if (!obtainedValue.equals(detValue)) { 
				TestUtils.printErrorMessage("Testing " + text + ".\tDeterministic value does not match. Got: " + obtainedValue + ". Should be " + detValue);
				return false;
			}
			return true;			
		}
		
		private boolean checkProbabilisticPart(boolean resultDet, RandomVariate obtainedDist) {
			if (resultDet) {
				if (obtainedDist == null) {
					if (dist != null) {
						TestUtils.printErrorMessage("Testing " + text + ".\tProbabilistic value does not match. Got: " + obtainedDist + ". Should be: null");
						return false;
					}
				}
				else if (!obtainedDist.toString().equals(dist)) {
					TestUtils.printErrorMessage("Testing " + text + ".\tProbabilistic value does not match. Got: " + obtainedDist + ". Should be: " + dist);
					return false;
				}
			}
			return true;
		}

		public void check() {
			ProbabilityDistribution dist = null;
			try {
				dist = new ProbabilityDistribution(text);
				if (causeError)
					TestUtils.printErrorMessage("Testing " + text + ".\tShould have caused error. Instead, got " + dist);
				else {
					if (checkProbabilisticPart(checkDeterministicPart(dist.getDeterministicValue()), dist.getProbabilisticValue()))
						TestUtils.printCheckedMessage("Testing " + text + ".\tGot: " + dist);						
				}
			} catch (TranspilerException e) {
				if (causeError)
					TestUtils.printCheckedMessage("Testing " + text + ".\tObtained error as expected.");
				else {
					TestUtils.printErrorMessage("Testing " + text + ". Malformed expression");
					e.printStackTrace();
				}
			}
		}
	};

	public static void main(String[] args) {
		final TestItem [] TESTS = {
			new TestItem("1.0", false, 1.0, "Constant (1.0)"), // Only deterministic value
			new TestItem("#Exponential(1.0)", false, "Exponential (1.0)"), // Only single parameter distribution
			new TestItem("#Normal(10.5, 5)", false, "Normal (10.5, 5.0)"), // Only double parameter distribution
			new TestItem("1.0#Exponential(1.0)", false, 1.0, "Exponential (1.0)"), // Single parameter distribution
			new TestItem("1.0#EXP(1.0)", false, 1.0, "Exponential (1.0)"), // Single parameter distribution (shorten name)
			new TestItem("10.5#Normal(10.5, 5)", false, 10.5, "Normal (10.5, 5.0)"), // Double parameter distribution
			new TestItem("10.5#2*Normal(5.25, 2.5)", false, 10.5, "Scaled [2.0, 0.0] Normal (5.25, 2.5)"), // Scaled double parameter distribution
			new TestItem("10.5#10+Normal(0.5, 5)", false, 10.5, "Scaled [1.0, 10.0] Normal (0.5, 5.0)"), // Scaled double parameter distribution
			new TestItem("-10.5#-10-Normal(0.5, 5)", false, -10.5, "Scaled [-1.0, -10.0] Normal (0.5, 5.0)"), // Scaled double parameter distribution
			new TestItem("10.5#10+2*Normal(0.5, 5)", false, 10.5, "Scaled [2.0, 10.0] Normal (0.5, 5.0)"), // Scaled double parameter distribution
			new TestItem("    10.5#   Normal (  10.5,   5   )", false, 10.5, "Normal (10.5, 5.0)"), // Double parameter distribution with superfluous blanks
			new TestItem("10,5#Normal(10.5, 5)", true, 10.5), // Error in deterministic value: "," instead of "."
			new TestItem("10.5@Normal(10.5, 5)", true, 10.5), // Error in expression separator: "@" instead of "#"
			new TestItem("10.5#Normal[10.5, 5]", true, 10.5), // Error in expression: "[]" instead of "()"
			new TestItem("10.5#Nrmal(10.5, 5)", true, 10.5), // Error in distribution: "Nrmal" instead of "Normal"
			new TestItem("10.5#Normal(10,5, 5)", true, 10.5) // Error in distribution: "," instead of "."
		};
		for (TestItem item : TESTS)
			item.check();
	}
}
