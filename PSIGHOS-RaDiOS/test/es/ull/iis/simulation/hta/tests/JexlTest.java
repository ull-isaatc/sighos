package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.Test;

public class JexlTest {

	// @Test
	public void objectTest() {
		System.out.println("Starting test objectTest ...");

		boolean expectedResult = true;
		boolean result = true;

		try {
			// Create or retrieve an engine
			JexlEngine jexl = new JexlBuilder().create();

			// Create an expression
			String jexlExp = "patient.age > 30.0 && patient.weight < 80.0";
			JexlExpression e = jexl.createExpression(jexlExp);

			// Create a context and add data
			JexlContext jc = new MapContext();
			jc.set("patient", new Patient(81.0, 41.7));

			// Now evaluate the expression, getting the result
			Object o = e.evaluate(jc);
			// System.out.println(o);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished objectTest ...");
	}

	@Test
	public void standaloneValueTest() {
		System.out.println("Starting test standaloneValueTest ...");

		boolean expectedResult = true;
		boolean result = true;

		try {
			int n = 10000000;
			JexlEngine jexl = new JexlBuilder().create();
			String jexlExp = "weight > 25 || esplenectomia";
			jexlExp = "1234 * 56789 / 9876";
			JexlExpression e = jexl.createExpression(jexlExp);

			JexlContext jc = new MapContext();
			jc.set("weight", 12.9);
			jc.set("esplenectomia", false);

			Long timeA = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				Object o = e.evaluate(jc);
				// System.out.println("\t" + o);
			}
			System.out.println("Tiempo invertido (ms) = " + (System.currentTimeMillis() - timeA));

			Long timeB = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				Double tmp = 1234.0 * 56789.0 / 9876.0;
			}
			System.out.println("Tiempo invertido (ms) = " + (System.currentTimeMillis() - timeB));
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished standaloneValueTest ...");
	}

}
