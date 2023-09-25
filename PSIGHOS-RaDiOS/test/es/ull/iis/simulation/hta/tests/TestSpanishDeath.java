/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.util.Arrays;

import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author Iván Castilla
 *
 */
public class TestSpanishDeath {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43126637), Math.exp(-11.40131427)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093183246, 0.099345217};

	/** Survival for men and women, in inverse order (the first value is the survival at 100 years old). Survival is computed by
	 * iteratively applying the mortality risk from the INE table to a hypotethical population of 10000 individuals */
	private static double[][] INV_SURVIVAL = {
		{180.5886298, 247.9029831, 334.1752564, 451.284885, 606.540786, 805.0895261, 1041.719246, 1324.501104, 
			1639.465451, 1981.322166, 2355.794297, 2755.146762, 3163.844415, 3575.603855, 3977.671332, 4369.295139, 
			4752.635127, 5114.885586, 5455.783642, 5784.453407, 6093.221276, 6381.096349, 6645.257812, 6896.789066, 
			7121.077629, 7318.772377, 7521.175229, 7706.811639, 7876.14544, 8038.448729, 8185.574726, 8326.405339, 
			8457.561376, 8579.997049, 8697.074657, 8804.342709, 8902.014295, 8994.651863, 9079.082141, 9157.842398, 
			9230.131356, 9297.636789, 9360.32571, 9418.22388, 9470.380518, 9517.772731, 9562.058741, 9599.469848, 
			9636.459435, 9668.863185, 9696.767545, 9721.327101, 9743.334848, 9764.212575, 9781.089694, 9795.681042, 
			9808.089014, 9819.483023, 9829.861453, 9839.222408, 9848.75532, 9856.277171, 9864.183937, 9871.108531, 
			9877.615054, 9883.441788, 9889.111473, 9894.649153, 9899.969511, 9904.226879, 9909.039721, 9913.925948, 
			9918.091121, 9921.947463, 9925.953186, 9929.90779, 9933.686123, 9937.135944, 9940.372328, 9943.638746, 
			9946.721133, 9949.445349, 9952.152314, 9954.42109, 9956.499633, 9957.559925, 9958.639944, 9959.52887, 
			9960.605057, 9961.154614, 9961.696341, 9962.455094, 9963.220521, 9963.817119, 9964.545272, 9965.208912, 
			9965.905751, 9967.06719, 9968.549861, 9970.951663, 10000},
		{423.7085092, 583.585077, 787.1139177, 1053.859897, 1377.290458, 1746.647096, 2166.350113, 2629.870883, 
			3120.783388, 3628.452575, 4157.851874, 4675.591221, 5164.964471, 5627.387659, 6052.279895, 6445.584963, 
			6803.47253, 7122.834478, 7409.218957, 7663.126053, 7884.08704, 8083.868669, 8261.520142, 8418.111711, 
			8550.225882, 8665.57719, 8778.883649, 8875.803811, 8961.972667, 9042.248909, 9110.787611, 9177.058042, 
			9237.165754, 9292.152323, 9343.728329, 9391.376027, 9434.324084, 9478.932494, 9519.482033, 9555.523771, 
			9589.667495, 9622.091961, 9651.47506, 9679.288195, 9705.937886, 9729.574328, 9750.330015, 9770.251414, 
			9788.032873, 9805.468246, 9822.017267, 9835.815057, 9849.056534, 9860.231095, 9869.517988, 9878.513774, 
			9886.535258, 9894.062465, 9900.025827, 9905.101748, 9910.209791, 9914.686685, 9919.391633, 9923.564057, 
			9926.544958, 9929.999153, 9932.442893, 9935.103623, 9937.428256, 9939.456101, 9941.334771, 9943.116334, 
			9944.942985, 9947.056494, 9948.650928, 9950.640319, 9952.333852, 9954.014806, 9955.388577, 9956.869449, 
			9958.627503, 9960.031134, 9961.243615, 9962.43125, 9963.695206, 9964.727946, 9966.090422, 9966.904582, 
			9967.412568, 9968.080336, 9968.782728, 9969.269052, 9970.169551, 9970.679322, 9971.541386, 9972.15983, 
			9972.854337, 9973.472054, 9974.237409, 9976.293046, 10000}
	}; 

	/** An increased mortality rate for testing */
	private static double TEST_IMR = 1.0;
	/** Maximum age for patients */
	private static double MAX_AGE = 100;
	
	/**
	 * Returns the time to death for a specific patient using an empirical distribution 
	 * @param sex Sex of the patient (0: male; 1: female)
	 * @param age Age of the patient
	 * @param rnd Random number
	 * @return the time to death for a specific patient
	 */
	private static double getEmpiricalTimeToDeath(int sex, double age, double rnd) {
		final double reference = INV_SURVIVAL[sex][100 - (int)age];
		final double index = rnd * reference / TEST_IMR;
		final int ageToDeath = 101 - Math.abs(Arrays.binarySearch(INV_SURVIVAL[sex], index));
		return Math.min((ageToDeath > age) ? ageToDeath - age + rnd : rnd, MAX_AGE - age);
	}
	
	/**
	 * Returns the time to death for a specific patient using an empirical distribution 
	 * @param sex Sex of the patient (0: male; 1: female)
	 * @param age Age of the patient
	 * @param rnd Random number
	 * @return the time to death for a specific patient
	 */
	private static double getGompertzTimeToDeath(int sex, double age, double rnd) {
		// If the prediction of the gompertz >= MAX_AGE, returns MAX_AGE 
		final double time = Math.min(
				Math.log(1-(BETA_DEATH[sex]/ALPHA_DEATH[sex])*Math.log(1-(rnd / TEST_IMR))*Math.exp(-BETA_DEATH[sex]*age))/BETA_DEATH[sex], 
				MAX_AGE - age);
		return time;
	}
	
	/**
	 * For testing a specific patient
	 */
	public static void testOne() {
		final int sex = 1;
		final double age = 18;
		final double rnd = 1 - 0.0000001;
		System.out.println("Pat\tSex\tAge\tRnd\tGompertz\tEmpirical");
		System.out.println(0 + "\t" + sex + "\t" + age + "\t" + rnd + "\t" + getGompertzTimeToDeath(sex, age, rnd) + "\t" + getEmpiricalTimeToDeath(sex, age, rnd));
		
	}
	
	/**
	 * For testing many patients
	 */
	public static void testMany() {
		final int npatients = 100000;
		final int MIN_AGE = 18;
		final RandomNumber rng = RandomNumberFactory.getInstance();
		final double rnd[] = new double[npatients];
		final int sex[] = new int[npatients];
		final double age[] = new double[npatients];
		final double time2Death[][] = new double[npatients][2];
		for (int i = 0; i < npatients; i++) {
			rnd[i] = rng.draw();
			sex[i] = (rng.draw() > 0.5) ? 1 : 0;
			age[i] = MIN_AGE;// + (MAX_AGE - MIN_AGE) * rng.draw();
		}
		// Test Gompertz
		System.out.println("Testing Gompertz...");
		long cputime = System.nanoTime();
		for (int i = 0; i < npatients; i++) {
			time2Death[i][0] = getGompertzTimeToDeath(sex[i], age[i], rnd[i]);
		}
		System.out.println("Time: " + (System.nanoTime() - cputime) / 1000);
		// Test empirical
		System.out.println("Testing Empirical...");
		cputime = System.nanoTime();
		for (int i = 0; i < npatients; i++) {
			time2Death[i][1] = getEmpiricalTimeToDeath(sex[i], age[i], 1 - rnd[i]);
		}
		System.out.println("Time: " + (System.nanoTime() - cputime) / 1000);
		System.out.println("Pat\tSex\tAge\tRnd\tGompertz\tEmpirical");
		for (int i = 0; i < npatients; i++) {
			System.out.println(i + "\t" + sex[i] + "\t" + age[i] + "\t" + rnd[i] + "\t" + time2Death[i][0] + "\t" + time2Death[i][1]);
		}
	}
	
	public static void main(String[] args) {
		testMany();
//		testOne();
	}
}
