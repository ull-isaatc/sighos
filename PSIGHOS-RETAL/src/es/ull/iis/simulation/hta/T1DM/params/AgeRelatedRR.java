/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla
 *
 */
public class AgeRelatedRR implements RRCalculator {
	/** An age-ordered array of pairs {age, RR} */
	final private double[][] agesNRR;
	
	/**
	 * @param agesNRR An age-ordered array of pairs {age, RR}
	 */
	public AgeRelatedRR(double[][] agesNRR) {
		this.agesNRR = agesNRR;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.RRCalculator#getRR(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public double getRR(T1DMPatient pat) {
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
