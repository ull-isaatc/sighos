/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class DiabetesParam extends EmpiricTimeToEventParam {
	/** Parameters for an empirical distribution of the incidence of DM2 on men. 
	 * Sources: INE: Encuesta Europea de Salud 2009
	 * M. Mata-Cases et al. Incidencia de diabetes tipo 2 y análisis del proceso diagnóstico en un centro de atención primaria durante 
	 * la década de los noventa. Gaceta Sanitaria, 20(2), 124-131. 2006
	 */
	private final static double[][] DM2_MEN_INCIDENCE = {
			{14, 30, 0.2, 1196.24862177062},
			{30, 40, 1.7, 1584.69527618636},
			{40, 50, 4.5, 1665.72608366663},
			{50, 60, 8.7, 1198.75472921846},
			{60, 70, 9.7, 960.674521673333},
			{70, 80, 3.9, 725.935790725327},
			{80, CommonParams.MAX_AGE, 0.4, 395.964976759269}
	};
	/** Parameters for an empirical distribution of the incidence of DM2 on women. 
	 * Sources: INE: Encuesta Europea de Salud 2009
	 * M. Mata-Cases et al. Incidencia de diabetes tipo 2 y análisis del proceso diagnóstico en un centro de atención primaria durante 
	 * la década de los noventa. Gaceta Sanitaria, 20(2), 124-131. 2006
	 */
	private final static double[][] DM2_WOMEN_INCIDENCE = {
			{14, 30, 0.2, 1045.37603287329},
			{30, 40, 0.8, 1430.14348117379},
			{40, 50, 3.3, 1567.92015722006},
			{50, 60, 9.1, 1233.22715619277},
			{60, 70, 9.6, 1101.92562419045},
			{70, 80, 6, 1005.80570816026},
			{80, CommonParams.MAX_AGE, 1.7, 669.601840189379}			
	};
	/** Minimum age from which prevalence has been taken. Source: INE: Encuesta Europea de Salud 2014 */
	private final static int MIN_AGE_PREVALENCE = 40;
	/** Parameters for an empirical distribution of the prevalence of DM on men. 
	 * Source: INE: Encuesta Europea de Salud 2014
	 */
	private final static double[][] DM_MEN_PREVALENCE = {
			{5, 236}, {3, 211}, {3, 227}, {7, 224}, {5, 209}, {3, 222}, {10, 210}, {7, 224}, {11, 212}, {13, 205},
			{9, 204}, {15, 193}, {20, 199}, {16, 154}, {15, 204}, {15, 200}, {15, 146}, {24, 183}, {15, 182}, {22, 160},
			{14, 165}, {24, 175}, {23, 152}, {39, 189}, {28, 147}, {31, 149}, {21, 152}, {38, 142}, {37, 161}, {30, 144},
			{25, 141}, {26, 134}, {28, 108}, {32, 135}, {16, 114}, {18, 85}, {26, 106}, {22, 101}, {23, 96}, {17, 107},
			{23, 114}, {25, 88}, {20, 83}, {19, 92}, {12, 60}, {10, 66}, {9, 53}, {10, 44}, {8, 43}, {3, 24},
			{7, 20}, {4, 21}, {1, 12}, {1, 9}, {4, 9}, {2, 6}, {0, 1}, {1, 2}, {0, 1}, {0, 1},
			{0, 1} // Actually, there are 0 men with 100 years old, but it's been changed to prevent DIV0 errors  
	};
	/** Parameters for an empirical distribution of the prevalence of DM2 on women. 
	 * Source: INE: Encuesta Europea de Salud 2014
	 */
	private final static double[][] DM_WOMEN_PREVALENCE = {
			{6, 253}, {3, 250}, {4, 272}, {6, 205}, {2, 246}, {3, 209}, {7, 209}, {3, 214}, {11, 211}, {6, 198},
			{5, 195}, {10, 194}, {13, 218}, {9, 192}, {11, 209}, {11, 207}, {14, 203}, {16, 193}, {9, 192}, {15, 187},
			{18, 172}, {11, 184}, {18, 201}, {19, 172}, {19, 159}, {26, 202}, {19, 182}, {35, 186}, {23, 180}, {30, 197},
			{32, 178}, {34, 181}, {25, 158}, {25, 142}, {27, 139}, {18, 97}, {32, 155}, {27, 160}, {42, 170}, {35, 143},
			{35, 174}, {41, 162}, {30, 164}, {33, 132}, {26, 134}, {26, 108}, {23, 98}, {14, 95}, {17, 67}, {11, 61},
			{13, 62}, {5, 39}, {6, 39}, {8, 31}, {8, 27}, {1, 7}, {0, 6}, {0, 5}, {1, 10}, {0, 4},
			{0, 1}			
	};
	// FIXME: change by better adjusted value
	/** The percentage of DM1 among diabetics. Source: Report from Federacion Española de Diabeticos */
	private final static double P_DM1 = 0.13;
	/** 
	 * Additional mortality risk for people affected by diabetes. As it is used in 
	 * Rein, D.B. et al., 2011. The cost-effectiveness of three screening alternatives for people with diabetes with 
	 * no or early diabetic retinopathy. Health services research, 46(5), pp.1534–61.  
	 */
	private final static double ADDITIONAL_MORTALITY_RISK = 2.0;
	
	/** Duration of diagnosed diabetes by age. Source: Rein */
	private final static double[][] DURATION_OF_DM2 = {
			{30, 40, 8.35},
			{40, 50, 10.17},
			{50, 60, 7.94},
			{60, 70, 6.74},
			{70, 80, 6.86},
			{80, CommonParams.MAX_AGE, 1.19}
	};
	/** An internal list of generated times to event to be used when creating validated times to event */
	protected final LinkedList<Long> queue = new LinkedList<Long>();	
	private final double[][] incidenceMen;
	private final double[][] incidenceWomen;
	private final double[] prevalenceMen;
	private final double[] prevalenceWomen;
	private final double pDM1;
	private final double addMortalityRisk;
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public DiabetesParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		pDM1 = P_DM1;
		addMortalityRisk = ADDITIONAL_MORTALITY_RISK;
		incidenceMen = new double[DM2_MEN_INCIDENCE.length][3];
		incidenceWomen = new double[DM2_WOMEN_INCIDENCE.length][3];
		initProbabilities(DM2_MEN_INCIDENCE, incidenceMen);
		initProbabilities(DM2_WOMEN_INCIDENCE, incidenceWomen);
		prevalenceMen = new double[DM_MEN_PREVALENCE.length];
		for (int i = 0; i < DM_MEN_PREVALENCE.length; i++)
			prevalenceMen[i] = DM_MEN_PREVALENCE[i][0] / DM_MEN_PREVALENCE[i][1];
		prevalenceWomen = new double[DM_WOMEN_PREVALENCE.length];
		for (int i = 0; i < DM_WOMEN_PREVALENCE.length; i++)
			prevalenceWomen[i] = DM_WOMEN_PREVALENCE[i][0] / DM_WOMEN_PREVALENCE[i][1];
	}

	/**
	 * Returns whether a patient, according to his/her age and sex, should be diabetic 
	 * @param pat A patient
	 * @return True if the patient is diabetic; false else
	 */
	public boolean isDiabetic(Patient pat) {
		final int age = (int)pat.getAge();
		final double prob = (pat.getSex() == CommonParams.MAN) ? prevalenceMen[age - MIN_AGE_PREVALENCE] : prevalenceWomen[age - MIN_AGE_PREVALENCE];
		return (pat.draw(RandomForPatient.ITEM.DIABETIC) < prob);
	}
	
	/**
	 * Returns the probability that a diabetic patient has DM1, as opposed to DM2
	 * @return the probability that a diabetic patient has DM1, as opposed to DM2
	 */
	public double getProbabilityDM1() {
		return pDM1;
	}

	/**
	 * @return the addMortalityRisk
	 */
	public double getAdditionalMortalityRisk() {
		return addMortalityRisk;
	}

	/**
	 * Returns the duration of the diabetes in a patient 
	 * @param pat A patient
	 * @return the duration of the diabetes in a patient
	 */
	public double getDurationOfDM(Patient pat) {
		final int age = (int)pat.getAge();
		final int type = pat.getDiabetesType();
		if (type == 1) {
			return age;
		}
		else {
			for (int i = 0; i < DURATION_OF_DM2.length; i++) {
				if (DURATION_OF_DM2[i][1] > age)
					return DURATION_OF_DM2[i][2];
			}			
		}	
		return -1.0;
	}
	
	private long getTimeToEvent(Patient pat) {
		final double time;
		if (pat.getSex() == CommonParams.MAN) {
			final double []rnd = pat.draw(RandomForPatient.ITEM.DIABETES_INCIDENCE, incidenceMen.length);
			time = getTimeToEvent(incidenceMen, pat.getAge(), rnd);			
		}
		else {
			final double []rnd = pat.draw(RandomForPatient.ITEM.DIABETES_INCIDENCE, incidenceWomen.length);
			time = getTimeToEvent(incidenceWomen, pat.getAge(), rnd);						
		}
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + pat.getSimulationEngine().getTimeUnit().convert(time, unit);
	}

	/** 
	 * Returns the time to suffer diabetes, adjusted by the predicted age for death.
	 * @param pat A patient
	 * @return
	 */
	public long getValidatedTimeToEvent(Patient pat) {
		long timeToDiabetes;
		final long timeToDeath = pat.getTimeToDeath();
		final long currentTime = pat.getTs();
		
		// If there are no stored values in the queue, generate a new one
		if (queue.isEmpty()) {
			timeToDiabetes = getTimeToEvent(pat);
		}
		// If there are stored values in the queue, I try with them in the first place
		else {
			final Iterator<Long> iter = queue.iterator();
			do {
				timeToDiabetes = iter.next();
				if (timeToDiabetes < timeToDeath)
					iter.remove();
				// Check if the stored time already passed --> If so, discharge
				if (timeToDiabetes <= currentTime)
					timeToDiabetes = Long.MAX_VALUE;
			} while (iter.hasNext() && timeToDiabetes >= timeToDeath);
			// If no valid event is found, generate a new one
			if (timeToDiabetes >= timeToDeath)
				timeToDiabetes = getTimeToEvent(pat);
		}
		// Generate new times to event until we get a valid one
		while (timeToDiabetes != Long.MAX_VALUE && timeToDiabetes >= timeToDeath) {
			queue.push(timeToDiabetes);
			timeToDiabetes = getTimeToEvent(pat);
		}
		return timeToDiabetes;
	}
}
