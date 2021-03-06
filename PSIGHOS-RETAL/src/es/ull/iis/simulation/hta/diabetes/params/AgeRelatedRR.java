/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * A relative risk that changes with age
 * @author Iv�n Castilla
 *
 */
public class AgeRelatedRR implements RRCalculator {
	/** An age-ordered array of pairs {age, RR} */
	final private double[][] agesNRR;
	
	/**
	 * Creates a new instance of this relative risk calculator
	 * @param agesNRR An age-ordered array of pairs {age, RR}
	 */
	public AgeRelatedRR(double[][] agesNRR) {
		this.agesNRR = agesNRR;
	}

	@Override
	public double getRR(DiabetesPatient pat) {
		final double age = pat.getAge();
		for (double[] pair : agesNRR) {
			if (pair[0] > age)
				return pair[1];
		}
		return 1.0;
	}

//	public static void main(String[] args) {
//		double[][] agesNRR = {{9,2},{18,1.4},{53, 1.1},{Double.MAX_VALUE, 1}};
//		for (int age = 1; age < 100; age++) {
//			System.out.print(age + "\t");
//			for (double[] pair : agesNRR) {
//				if (pair[0] > (double)age) {
//					System.out.println(pair[1]);
//					break;
//				}
//			}
//		}
//	}
}
