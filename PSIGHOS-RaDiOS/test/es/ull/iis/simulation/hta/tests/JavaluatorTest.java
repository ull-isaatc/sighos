/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.util.Iterator;
import java.util.TreeMap;

import com.fathzer.soft.javaluator.AbstractVariableSet;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

/**
 * @author Iván Castilla
 *
 */
public class JavaluatorTest {
	private final static String[] TEST_STR = {
			"T1DM_Manif_ALB1_Incidence10 * ((Attribute_HbA1c / 10) ^ T1DM_Manif_ALB1_Beta)"};
//	private final static Operator REF = new Operator("#", 1, Operator.Associativity.RIGHT, 0);
	
//	private final static Parameters PARAMETERS = DoubleEvaluator.getDefaultParameters();
	private final static TreeMap<String,Double> PAIRS = new TreeMap<>();
	
	static {
//		PARAMETERS.add(REF);
		PAIRS.put("T1DM_Manif_ALB1_Incidence10", 0.0436);
		PAIRS.put("Attribute_HbA1c", 9.0);
		PAIRS.put("T1DM_Manif_ALB1_Beta", 3.25);
	}
	
	private final static AbstractVariableSet<Double> CONTEXT = new AbstractVariableSet<Double>() {

		@Override
		public Double get(String variableName) {
			return PAIRS.get(variableName);
		}
		
	};
//	private final static DoubleEvaluator EVALUATOR = new DoubleEvaluator(PARAMETERS) {
//		protected Double evaluate(Operator operator, Iterator<Double> operands, Object evaluationContext) {
//			if (operator.equals(REF)) {
//				return PAIRS.get(operands.next());
//			}
//			else {
//				return super.evaluate(operator, operands, evaluationContext);
//			}
//		}
//	};
	/**
	 * 
	 */
	public JavaluatorTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		final DoubleEvaluator evaluator = new DoubleEvaluator();
		System.out.println("CONTEXT");
		for (String var : PAIRS.keySet())
			System.out.println(var + ": " + PAIRS.get(var));
			
		System.out.println();
		System.out.println("EXPRESSIONS");
		for (String str : TEST_STR) {
			System.out.println("Testing " + str);
			System.out.println("Result " + evaluator.evaluate(str, CONTEXT));
		}
	}
}
