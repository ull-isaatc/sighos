/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.Random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	public final static int MAX_AGE = 104;
	// Parameters for death
	private final static Random RNG_DEATH = new Random();
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.09626023), Math.exp(-11.27134293)};
	private final static double BETA_DEATH[] = new double[] {0.090616954, 0.099117727};

	/**
	 * 
	 */
	public CommonParams(boolean baseCase) {
		super(baseCase);
	}

	/**
	 * @return Years to death of the patient or years to MAX_AGE 
	 */
	public double getDeathTime(double initAge, int sex) {
		return Math.min(generateGompertz(ALPHA_DEATH[sex], BETA_DEATH[sex], initAge, RNG_DEATH.nextDouble()), MAX_AGE - initAge);
	}
	

}
