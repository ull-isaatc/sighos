/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	public final static int MAX_AGE = 100;
	public final static int MIN_AGE = 40;
	public final static int MAN = 0;
	public final static int WOMAN = 1;
	private final static double P_MEN = 0.5;
	// TODO: change by a real value
	private final static double P_DM1 = 0.1;
	
	// Parameters for death. Source: Spanish 2014 Mortality risk. INE
	// Adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 40 to 100.
	private final static Random RNG_DEATH = new Random();
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.72495061), Math.exp(-12.03468863)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.097793214, 0.108276765};

	private final DiabetesParam diabetes;
	private final VAParam vaParam;
	private final VAtoUtilityParam vaToUtility;
	
	/**
	 * 
	 */
	public CommonParams(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		diabetes = new DiabetesParam(simul, baseCase);
		vaParam = new VAParam(simul, baseCase);
		vaToUtility = new VAtoUtilityParam(simul, baseCase);
	}

	/**
	 * @return Years to death of the patient or years to MAX_AGE 
	 */
	public long getTimeToDeath(Patient pat) {
		final double time = Math.min(generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), RNG_DEATH.nextDouble()), MAX_AGE - pat.getAge());
		return pat.getTs() + simul.getTimeUnit().convert(time, TimeUnit.YEAR);
	}
	
	public int getSex(Patient pat) {
		return (pat.getRandomNumber(RandomForPatient.ITEM.SEX) < P_MEN) ? 0 : 1;
	}
	
	public double getInitAge() {
		return MIN_AGE;		
	}

	/** Returns true if a patient is diabetic at a specified age. Only valid for initializing a patient. */
	public boolean isDiabetic(Patient pat) {
		return diabetes.isDiabetic(pat);
	}
	
	public int getDiabetesType(Patient pat) {
		return (pat.getRandomNumber(RandomForPatient.ITEM.DIABETES_TYPE) < P_DM1) ? 1 : 2; 
	}
	
	public double getDurationOfDM(Patient pat) {
		return diabetes.getDurationOfDM(pat);
	}
	
	public long getTimeToDiabetes(Patient pat) {
		return diabetes.getValidatedTimeToEvent(pat);
	}

	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, EyeState incidentState) {
		return vaParam.getVAProgression(pat, eyeIndex, incidentState, null);
	}

	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, CNVStage incidentCNVStage) {
		return vaParam.getVAProgression(pat, eyeIndex, EyeState.AMD_CNV, incidentCNVStage);
	}

	public ArrayList<VAProgressionPair> getVAProgressionToDeath(Patient pat, int eyeIndex) {
		return vaParam.getVAProgression(pat, eyeIndex, null, null);
	}
	
	public double getInitialVA(Patient pat, int eyeIndex) {
		return vaParam.getInitialVA(pat, eyeIndex);
	}
	
	public double getUtilityFromVA(Patient pat) {
		final double age = pat.getAge();
		return Math.max(vaToUtility.getUtility(age, pat.getVA(0)), vaToUtility.getUtility(age, pat.getVA(1)));		
	}
	
}
