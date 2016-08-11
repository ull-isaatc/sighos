/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	public final static int MAX_AGE = 100;
	public final static int MIN_AGE = 40;
	private final static double P_MEN = 0.5;
	
	// Parameters for death. Source: Spanish 2014 Mortality risk. INE
	// Adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 40 to 100.
	private final static Random RNG_DEATH = new Random();
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.72495061), Math.exp(-12.03468863)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.097793214, 0.108276765};

	/**
	 * 
	 */
	public CommonParams(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}

	/**
	 * @return Years to death of the patient or years to MAX_AGE 
	 */
	public long getDeathTime(Patient pat) {
		final double time = Math.min(generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), RNG_DEATH.nextDouble()), MAX_AGE - pat.getAge());
		return pat.getTs() + simul.getTimeUnit().convert(time, TimeUnit.YEAR);
	}
	
	public double getPMen() {
		return P_MEN;		
	}
	
	public double getInitAge() {
		return MIN_AGE;		
	}

}
