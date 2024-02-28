/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.diab.manifestations.BackgroundRetinopathy;
import es.ull.iis.simulation.hta.diab.manifestations.Neuropathy;
import es.ull.iis.simulation.hta.params.FirstOrderNatureParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.populations.FirstOrderOnlyPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.calculator.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;
import es.ull.iis.util.Statistics;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Secondary prevention cohort of DCCT
 * @author masbe
 *
 */
public class DCCT_SecondaryPopulation extends FirstOrderOnlyPopulation {
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
	 * @param model
	 * @param disease
	 */
	public DCCT_SecondaryPopulation(HTAModel model, Disease disease) throws MalformedSimulationModelException {
		super(model, "DCCTPop", "Secondary prevention cohort of DCCT", disease);
	}


	@Override
	public void createParameters() {
		final double alfaHbA1c = ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD) * ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD);
		final double betaHbA1c = (BASELINE_HBA1C_SD * BASELINE_HBA1C_SD) / (BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN);
		final RandomVariate rndHbA1c = RandomVariateFactory.getInstance("GammaVariate", alfaHbA1c, betaHbA1c);

		model.addParameter(new FirstOrderNatureParameter(getModel(), T1DMModel.STR_HBA1C, "", "", 2013, ParameterType.ATTRIBUTE, 
			RandomVariateFactory.getInstance("ScaledVariate", rndHbA1c, 1.0, BASELINE_HBA1C_MIN)));
		model.addParameter(new FirstOrderNatureParameter(getModel(), T1DMModel.STR_DURATION, "", "", 2013, ParameterType.ATTRIBUTE, 
			RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION_AVG, BASELINE_DURATION_SD)));

		disease.getDiseaseProgression(BackgroundRetinopathy.NAME).addUsedParameter(StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION, "DCCT", 
				"DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		disease.getDiseaseProgression(Neuropathy.NAME).addUsedParameter(StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION, "DCCT", 
				"DCCT", P_INI_NEU_BETA[0] / (P_INI_NEU_BETA[0] + P_INI_NEU_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_NEU_BETA[0], P_INI_NEU_BETA[1]));
	}

	@Override
	public int getMinAge() {
		return BASELINE_AGE_MIN;
	}

	@Override
	protected DiscreteRandomVariate initSexVariate() {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), P_WOMAN);
	}

	@Override
	protected DiscreteRandomVariate initDiseaseVariate() {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected DiscreteRandomVariate initDiagnosedVariate() {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate initBaselineAgeVariate() {
		// 28.4 has been established empirically to get a sd of 7.
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE_AVG, 28.4, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", getCommonRandomNumber(), betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

	@Override
	public TimeToEventCalculator getDeathCharacterization() {
		return new EmpiricalSpainDeathSubmodel(getModel());
	}

}
