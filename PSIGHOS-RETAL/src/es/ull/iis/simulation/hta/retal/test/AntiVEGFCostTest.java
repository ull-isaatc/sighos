/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import es.ull.iis.simulation.retal.params.OphthalmologicResource;
import es.ull.iis.simulation.retal.params.ResourceUsageItem;

/**
 * @author Iván Castilla
 *
 */
public class AntiVEGFCostTest {
	/** Annual frequency of antiVEGF treatment the first two years and later on for CNV AMD */
	final private static int[] N_ANTIVEGF_CNV = {12, 4};
	/** Annual frequency of antiVEGF treatment the first two years and later on for CSME */
	final private static int[] N_ANTIVEGF_CSME = {13, 4};

	/**
	 * 
	 */
	public AntiVEGFCostTest() {
		// TODO Auto-generated constructor stub
	}

	private static double getAntiVEGFCost(int[] usage, double ageAtTreatment, double initAge, double endAge) {
		double cost = 0.0;
		// The treatment started more than two years ago
		if (initAge - ageAtTreatment >= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.TEST, usage[1]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for less than two years 
		else if (endAge - ageAtTreatment <= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.TEST, usage[0]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for more than two years 
		else {
			cost += new ResourceUsageItem(OphthalmologicResource.TEST, usage[0]).computeCost(initAge, ageAtTreatment + 2.0);
			cost += new ResourceUsageItem(OphthalmologicResource.TEST, usage[1]).computeCost(ageAtTreatment + 2.0, endAge);
		}
		return cost;
	}
	
	private static void testConfig(boolean CNV, double ageAtTreatment, double initAge, double endAge) {
		final double cost = getAntiVEGFCost(CNV ? N_ANTIVEGF_CNV : N_ANTIVEGF_CSME, ageAtTreatment, initAge, endAge);
		System.out.println((CNV ? "CNV\t" : "CSME\t") + ageAtTreatment + "\t" + initAge + "\t" + endAge + "\t" + cost);
	}
	
	public static void main(String[] args) {
		System.out.println("DISEASE\tAgeAtTreatment\tInitAge\tEndAge\tCost");
		testConfig(true, 0.0, 1.0, 2.0);
		testConfig(true, 0.0, 2.0, 3.0);
		testConfig(true, 0.0, 1.0, 4.0);
		testConfig(true, 0.0, 1.0, 1.25);
		testConfig(true, 0.0, 1.25, 2.0);
	}
	
}
