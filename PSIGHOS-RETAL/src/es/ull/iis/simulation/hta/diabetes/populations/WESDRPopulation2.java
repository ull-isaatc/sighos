/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * From Klein et al. 10.1016/S0161-6420(98)91020-X
 * Adaptado para tratar de generar los pacientes siguiendo las distribuciones empíricas de edad y duración de diabetes. No mejora la validación!!!
 * @author Iván Castilla Rodríguez
 *
 */
public class WESDRPopulation2 implements DiabetesPopulation {
	/** Average and SD HbA1c in the population at baseline. */
	private static final double[] BASELINE_HBA1C = {10.6, 2.0}; 
	/** The average age at baseline */
	private static final double[] BASELINE_AGE = {26.8, 11.2}; 
	private static final int[][] EMPIRIC_BASELINE_AGE = {
			{10, 24},
			{15, 77},
			{20, 137},
			{25, 127},
			{30, 94},
			{35, 99},
			{Integer.MAX_VALUE, 130}
	};
	private static final double MAX_DURATION = BasicConfigParams.DEF_MAX_AGE / 2.0;
	/** Duration of diabetes at baseline */
	private static final double[] BASELINE_DURATION = {12.6, 9.0}; 
	private static final int[][] EMPIRIC_BASELINE_DURATION = {
			{3, 72},
			{5, 81},
			{10, 224},
			{15, 124},
			{20, 74},
			{25, 44},
			{30, 35},
			{Integer.MAX_VALUE, 34}
	};
	private static final double P_MAN = 0.492;  
	private static final double P_SMOKER = 0.249;
	/** SBP at baseline */
	private static final double[] BASELINE_SBP = {120, 16.0}; 
	/** Distribution to set the HbA1c level of the patients when created */
	private final RandomVariate baselineHBA1c;
	/** Probability of a patient of having atrial fibrillation */
	private final double pAtrialFib;
	/** Distribution to set the SBP of the patients when created */
	private final RandomVariate baselineSBP;
	/** Distribution to set the lipid ratio T:H of the patients when created */
	private final RandomVariate baselineLipidRatio;
	/** Random number generator */
	private final RandomNumber rng;
	private final double [][]pAge;
	private final double [][]pDuration;
	private final int nPatients;
	private int patientCounter = 0;

	/**
	 */
	public WESDRPopulation2(int nPatients) {
		this.nPatients = nPatients;
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			baselineHBA1c = RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C[0]);
		else
			baselineHBA1c = RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
		baselineSBP = RandomVariateFactory.getInstance("NormalVariate", BASELINE_SBP[0], BASELINE_SBP[1]);
		pAtrialFib = BasicConfigParams.DEFAULT_P_ATRIAL_FIB;
		baselineLipidRatio = RandomVariateFactory.getInstance("Constant", BasicConfigParams.DEFAULT_LIPID_RATIO);
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
		pAge = new double[EMPIRIC_BASELINE_AGE.length][2];
		double total = 0.0;
		double cum = 0.0;
		for (int i = 0; i < EMPIRIC_BASELINE_AGE.length; i++) {
			pAge[i][0] = EMPIRIC_BASELINE_AGE[i][0];
			total += EMPIRIC_BASELINE_AGE[i][1];
		}
		pAge[pAge.length - 1][0] = getMaxAge();
		for (int i = 0; i < EMPIRIC_BASELINE_AGE.length; i++) {
			cum += EMPIRIC_BASELINE_AGE[i][1];
			pAge[i][1] = cum / total;
		}
		pDuration = new double[EMPIRIC_BASELINE_DURATION.length][2];
		total = 0.0;
		cum = 0.0;
		for (int i = 0; i < EMPIRIC_BASELINE_DURATION.length; i++) {
			pDuration[i][0] = EMPIRIC_BASELINE_DURATION[i][0];
			total += EMPIRIC_BASELINE_DURATION[i][1];
		}
		pDuration[pDuration.length - 1][0] = MAX_DURATION;
		for (int i = 0; i < EMPIRIC_BASELINE_DURATION.length; i++) {
			cum += EMPIRIC_BASELINE_DURATION[i][1];
			pDuration[i][1] = cum / total;
		}
	}

	@Override
	public DiabetesPatientProfile getPatientProfile() {
		final int sex = (rng.draw() < P_MAN) ? BasicConfigParams.MAN : BasicConfigParams.WOMAN;
	
		final double p = (double)patientCounter / (double)nPatients;
		int i = 0;
		while (p > pAge[i][1])
			i++;
		final double rnd1 = rng.draw();
		final double initAge = (BasicConfigParams.USE_FIXED_BASELINE_AGE) ? BASELINE_AGE[0] : ((i == 0) ? rnd1 * pAge[0][0] : pAge[i - 1][0] + (pAge[i][0] - pAge[i - 1][0]) * rnd1);

		final double maxDuration = Math.max(initAge * 2.0 / 3.0, initAge - 18);			
		i = 0;  
		while (p > pDuration[i][1])
			i++;
		final double rnd2 = rng.draw();
		final double initDuration = (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES) ? BASELINE_DURATION[0] : ((i == 0) ? rnd2 * pDuration[0][0] : pDuration[i - 1][0] + (pDuration[i][0] - pDuration[i - 1][0]) * rnd2);
		
		patientCounter++;
		return new DiabetesPatientProfile(initAge, sex, Math.min(initDuration, maxDuration), baselineHBA1c.generate(), 
				rng.draw() < P_SMOKER, rng.draw() < pAtrialFib, baselineSBP.generate(), baselineLipidRatio.generate());
	}
	
	public DiabetesType getType() {
		return DiabetesType.T2;
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	@Override
	public int getMaxAge() {
		return 80;
	}

}
