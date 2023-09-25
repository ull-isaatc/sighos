/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.util.Arrays;

import es.ull.iis.simulation.hta.params.Discount;

/**
 * @author Iván Castilla
 *
 */
public class TestDiscounts {
	
	public static void testPeriod(Discount disc, double value, double[] testArray) {
		double total = disc.applyDiscount(value, testArray[0], testArray[1]);
		if (TestUtils.checkDouble(testArray[2], total))
			TestUtils.printCheckedMessage("CONTINUOUS:\t\t" + testArray[0] + "-" + testArray[1] + ":\t" + total);
		else
			TestUtils.printErrorMessage("CONTINUOUS:\t\t" + testArray[0] + "-" + testArray[1] + ":\t" + total + " should be " + testArray[2]);
			
		double [] result = disc.applyAnnualDiscount(value, testArray[0], testArray[1]);
		total = 0.0;
		for (double val : result)
			total += val;
		if (TestUtils.checkDouble(testArray[3], total)) {
			TestUtils.printCheckedMessage("ANNUAL:\t\t\t" + testArray[0] + "-" + testArray[1] + ":\t" + total);
		}
		else {
			TestUtils.printErrorMessage("ANNUAL:\t\t\t" + testArray[0] + "-" + testArray[1] + ":\t" + total + " should be " + testArray[3]);
		}
		System.out.println("\t\tANNUAL (per year):\t" + testArray[0] + "-" + testArray[1] + ":\t" + Arrays.toString(result)); 
		System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[][] testArray = {
				{0.5, 0.8, 29.429201704951634, 30.000000000000004},
				{0.0, 1.0, 98.53651495829747, 100.0},
				{0.0, 3.0, 287.0831502352739, 291.3469695541521},
				{0.5, 3.8, 309.8033216761618, 314.5583023024049},
				{0.0, 11.0, 939.0729743815718, 953.0202836775827},
				{2.3, 9.8, 628.4706966785525, 637.9173016262486} 
		};
		final double value = 100.0;
		final double discountRate = 0.03;
		final Discount disc = new Discount(discountRate);
		System.out.println("VALUE: " + value + " DISCOUNTED AT: " + discountRate);
		for (int i = 0; i < testArray.length; i++)
			testPeriod(disc, value, testArray[i]);
	}

}
