/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.manifestations.Neuropathy;
import es.ull.iis.simulation.hta.params.ConstantNatureParameter;
import es.ull.iis.simulation.hta.params.FirstOrderNatureParameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.calculator.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;
import es.ull.iis.util.Statistics;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Primary prevention cohort of DCCT
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTPopulation1 extends StdPopulation {
	private static final double BASELINE_HBA1C_MIN = 6.6; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_HBA1C_AVG = 8.8; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_HBA1C_SD = 1.7; // SD from conventional therapy (the highest): https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final int BASELINE_AGE_MIN = 13; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_MAX = 40; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_AGE_AVG = (26 * 378 + 27 *348) / (double)(378 + 348); // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_AVG = 2.6; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double BASELINE_DURATION_SD = 1.4; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double P_WOMAN = 1.0 - (0.54 *378 + 0.49 * 348) / (378 + 348); // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401
	private static final double []P_INI_NEU_BETA = {8 + 17, 378 + 348 - 8 - 17}; // DCCT: https://www.nejm.org/doi/10.1056/NEJM199309303291401

	/**
	 * @param model
	 * @param disease
	 */
	public DCCTPopulation1(HTAModel model, Disease disease) throws MalformedSimulationModelException {
		super(model, "DCCTPop", "Primary prevention cohort of DCCT", disease);
	}


	@Override
	public void createParameters() {
		if (T1DMDisease.FIXED_BASE_VALUES) {
			model.addParameter(new ConstantNatureParameter(getModel(), T1DMModel.STR_HBA1C, "", "", ParameterType.ATTRIBUTE, BASELINE_HBA1C_AVG));
			model.addParameter(new ConstantNatureParameter(getModel(), T1DMModel.STR_DURATION, "", "", ParameterType.ATTRIBUTE, BASELINE_DURATION_AVG));
		}
		else {
			final double alfaHbA1c = ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD) * ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD);
			final double betaHbA1c = (BASELINE_HBA1C_SD * BASELINE_HBA1C_SD) / (BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN);
			final RandomVariate rndHbA1c = RandomVariateFactory.getInstance("GammaVariate", alfaHbA1c, betaHbA1c);
			model.addParameter(new FirstOrderNatureParameter(getModel(), T1DMModel.STR_HBA1C, "", "", 2013, ParameterType.ATTRIBUTE, 
					RandomVariateFactory.getInstance("ScaledVariate", rndHbA1c, 1.0, BASELINE_HBA1C_MIN)));
			model.addParameter(new FirstOrderNatureParameter(getModel(), T1DMModel.STR_DURATION, "", "", 2013, ParameterType.ATTRIBUTE, 
					RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION_AVG, BASELINE_DURATION_SD)));
		}
		if (!T1DMDisease.DISABLE_NEU)
			disease.getDiseaseProgression(Neuropathy.NAME).addUsedParameter(StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION, "DCCT",
				"DCCT", P_INI_NEU_BETA[0] / (P_INI_NEU_BETA[0] + P_INI_NEU_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_NEU_BETA[0], P_INI_NEU_BETA[1]));
	}

	@Override
	public int getMinAge() {
		return BASELINE_AGE_MIN;
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), P_WOMAN);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		if (T1DMDisease.FIXED_BASE_VALUES) {
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE_AVG);			
		}
		else {
			// 28.4 has been established empirically to get a sd of 7.
			final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE_AVG, 28.4, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
			final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", getCommonRandomNumber(), betaParams[0], betaParams[1]); 
			return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
		}
	}

	@Override
	public TimeToEventCalculator getDeathCharacterization() {
		return new EmpiricalSpainDeathSubmodel(getModel());
	}

}
