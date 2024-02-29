/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import es.ull.iis.simulation.hta.params.Discount;

/**
 * Testing for the discount methods
 * @author Iván Castilla
 *
 */
public class TestDiscounts {
	private static final  short DEF_PRECISION = 5; 
	private static final double VALUE = 100.0;
	private static final double DISCOUNT_RATE = 0.03;
	private static final double[][] TEST_ARRAY = {
		{0.5, 0.8, 29.429201704951634, 30.000000000000004},
		{0.0, 1.0, 98.53651495829747, 100.0},
		{0.0, 3.0, 287.0831502352739, 291.3469695541521},
		{0.5, 3.8, 309.8033216761618, 314.5583023024049},
		{0.0, 11.0, 939.0729743815718, 953.0202836775827},
		{2.3, 9.8, 628.4706966785525, 637.9173016262486} 
	};

	private static boolean checkDouble(double reference, double comp, int precision) {
		return String.format("%." + precision + "g%n", comp).equals(String.format("%." + precision + "g%n", reference));
	}

	@TestFactory
	Stream<DynamicTest> testDifferentContDiscount() {
		final Discount disc = new Discount(DISCOUNT_RATE);
		return Arrays.stream(TEST_ARRAY).map(testArray -> {
			double initT = testArray[0];
			double endT = testArray[1];
			double expectedCont = testArray[2];
			return dynamicTest("Discounting by continuous method " + VALUE + " from " + initT + " to " + endT, () -> {
				assertTrue(checkDouble(expectedCont, disc.applyDiscount(VALUE, initT, endT), DEF_PRECISION));
			});
		});
	}

	@TestFactory
	Stream<DynamicTest> testDifferentAnnualDiscount() {
		final Discount disc = new Discount(DISCOUNT_RATE);
		return Arrays.stream(TEST_ARRAY).map(testArray -> {
			double initT = testArray[0];
			double endT = testArray[1];
			double expectedCont = testArray[3];
			return dynamicTest("Discounting year by year " + VALUE + " from " + initT + " to " + endT, () -> {
				double [] res = disc.applyAnnualDiscount(VALUE, initT, endT);
				double total = 0.0;
				for (double val : res)
					total += val;
				assertTrue(checkDouble(expectedCont, total, DEF_PRECISION));
			});
		});
	}
}
