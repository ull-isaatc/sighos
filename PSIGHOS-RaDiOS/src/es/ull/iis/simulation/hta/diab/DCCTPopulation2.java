/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.diab.manifestations.BackgroundRetinopathy;
import es.ull.iis.simulation.hta.diab.manifestations.Neuropathy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.ClinicalParameter;
import es.ull.iis.simulation.hta.populations.InitiallySetClinicalParameter;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.util.Statistics;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Secondary prevention cohort of DCCT
 * @author masbe
 *
 */
public class DCCTPopulation2 extends StdPopulation {
	private static final double BASELINE_HBA1C_MIN = 6.6; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_HBA1C_AVG = (8.9 * 352 + 9 * 363) / (352 + 363); // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_HBA1C_SD = 1.5; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final int BASELINE_AGE_MIN = 13; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_MAX = 40; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_AVG = 27; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_AVG = (8.6 * 352 + 8.9 * 363) / (352 + 363); // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_SD = 3.8; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double P_WOMAN = 1.0 - (0.54 * 352 + 0.53 * 363) / (352 + 363); // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	/** {Deterministic value, cases, no cases} for initial proportion of clinical neuropathy */
	private static final double []P_INI_NEU_BETA = {33 + 34, 352 + 363 - 33 - 34}; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401

	/**
	 * @param secParams
	 * @param disease
	 */
	public DCCTPopulation2(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, disease);
	}


	@Override
	public void registerSecondOrderParameters() {
		secParams.addInitProbParam(disease.getManifestation(BackgroundRetinopathy.NAME), "DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		secParams.addInitProbParam(disease.getManifestation(Neuropathy.NAME), "DCCT", P_INI_NEU_BETA[0] / (P_INI_NEU_BETA[0] + P_INI_NEU_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_NEU_BETA[0], P_INI_NEU_BETA[1]));
	}

	@Override
	public int getMinAge() {
		return BASELINE_AGE_MIN;
	}

	@Override
	protected List<ClinicalParameter> getPatientParameterList() {
		final ArrayList<ClinicalParameter> paramList = new ArrayList<>();
		
		final double alfaHbA1c = ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD) * ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD);
		final double betaHbA1c = (BASELINE_HBA1C_SD * BASELINE_HBA1C_SD) / (BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN);
		final RandomVariate rndHbA1c = RandomVariateFactory.getInstance("GammaVariate", alfaHbA1c, betaHbA1c);

		paramList.add(new InitiallySetClinicalParameter(T1DMRepository.STR_HBA1C, RandomVariateFactory.getInstance("ScaledVariate", rndHbA1c, 1.0, BASELINE_HBA1C_MIN)));
		paramList.add(new InitiallySetClinicalParameter(T1DMRepository.STR_DURATION, RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION_AVG, BASELINE_DURATION_SD)));
		return paramList;
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), P_WOMAN);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul) {
		// 28.4 has been established empirically to get a sd of 7.
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE_AVG, 28.4, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", getCommonRandomNumber(), betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

}
