/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;
import java.util.LinkedList;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	public final static boolean ANTIVEGF_2YEARS_ASSUMPTION = true;
	public final static int MAX_AGE = 100;
	public final static int MIN_AGE = 40;
	public final static int MAN = 0;
	public final static int WOMAN = 1;
	private final static double P_MEN = 0.5;
	// FIXME: Should be related to simulation time unit 
	public final static long MIN_TIME_TO_EVENT = 30;
	
	// Parameters for death. Source: Spanish 2014 Mortality risk. INE
	// Adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 40 to 100.
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.72495061), Math.exp(-12.03468863)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.097793214, 0.108276765};

	private final DiabetesParam diabetes;
	private final VAParam vaParam;
	private final VAtoUtilityParam vaToUtility;
	private final ResourceUsageParam resourceUsage;
	
	/**
	 * 
	 */
	public CommonParams(boolean baseCase) {
		super(baseCase);
		diabetes = new DiabetesParam(baseCase);
		vaParam = new VAParam(baseCase);
		vaToUtility = new VAtoUtilityParam(baseCase);
		resourceUsage = new ResourceUsageParam(baseCase);
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables.
	 * The risk of death can be increased by the specified factor.
	 * @param pat A patient
	 * @param addRisk Additional risk of death, expressed as a factor 
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	private long getTimeToDeath(Patient pat, double addRisk) {
		final double time = Math.min(generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), pat.draw(RandomForPatient.ITEM.DEATH) / addRisk), MAX_AGE - pat.getAge());
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}
	
	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	public long getTimeToDeath(Patient pat) {
		return getTimeToDeath(pat, 1.0);
	}
	
	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables, 
	 * and taking into account diabetes. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	public long getTimeToDeathDiabetic(Patient pat) {
		return getTimeToDeath(pat, diabetes.getAdditionalMortalityRisk());
	}
	
	public int getSex(Patient pat) {
		return (pat.draw(RandomForPatient.ITEM.SEX) < P_MEN) ? 0 : 1;
	}
	
	public double getInitAge() {
		return MIN_AGE;		
	}

	/** Returns true if a patient is diabetic at a specified age. Only valid for initializing a patient. */
	public boolean isDiabetic(Patient pat) {
		return diabetes.isDiabetic(pat);
	}
	
	public int getDiabetesType(Patient pat) {
		return (pat.draw(RandomForPatient.ITEM.DIABETES_TYPE) < diabetes.getProbabilityDM1()) ? 1 : 2; 
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
	
	public LinkedList<VAProgressionPair> getUtilityFromVA(Patient pat) {
		double age = MIN_AGE;
		ArrayList<VAProgressionPair> progression = vaParam.mergeVAProgressions(pat, 0.0, pat.getVaProgression(0), pat.getVaProgression(1));
		LinkedList<VAProgressionPair> utilities = new LinkedList<VAProgressionPair>();
		for (VAProgressionPair pair : progression) {
			age += pair.timeToChange / 365.0;
			utilities.add(new VAProgressionPair(pair.timeToChange, vaToUtility.getUtility(age, pair.va)));
		}
		return utilities;		
	}
	
	public double getCostForState(Patient pat, double initAge, double endAge) {
		return resourceUsage.getResourceUsageCost(pat, initAge, endAge);
	}
	
	public double getDiagnosisCost(Patient pat) {
		return resourceUsage.getDiagnosisCost(pat);
	}
	
	public double getScreeningCost(Patient pat) {
		return resourceUsage.getScreeningCost(pat);
	}
}
