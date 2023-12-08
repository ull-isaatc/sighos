/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.manifestations.HeartFailure;
import es.ull.iis.simulation.hta.diab.manifestations.LowExtremityAmputation;
import es.ull.iis.simulation.hta.diab.manifestations.MyocardialInfarction;
import es.ull.iis.simulation.hta.diab.manifestations.ProliferativeRetinopathy;
import es.ull.iis.simulation.hta.diab.manifestations.Stroke;
import es.ull.iis.simulation.hta.params.FirstOrderNatureParameter;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.ParameterType;
import es.ull.iis.simulation.hta.params.ParameterDescription;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
import es.ull.iis.util.Statistics;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Combined characteristics from the GOLD and DIAMOND studies. Both compared DEXCOM G4 on T1DM population with uncontrolled HbA1c.
 * Sources:
 * <ul>
 * <li>Lind M, Polonsky W, Hirsch IB, Heise T, Bolinder J, Dahlqvist S, et al. Continuous glucose monitoring vs conventional therapy for glycemic control in adults with type 1 diabetes treated with multiple daily insulin injections the gold randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):379–87.</li>
 * <li>Beck RW, Riddlesworth T, Ruedy K, Ahmann A, Bergenstal R, Haller S, et al. Effect of continuous glucose monitoring on glycemic control in adults with type 1 diabetes using insulin injections the diamond randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):371–8.</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMGoldDiamondPopulation extends StdPopulation {
	/** Mean and SD of baseline HbA1c. SD is the highest from all the cohorts */
	private static final double []BASELINE_HBA1C = {8.5382, 0.9};
	/** Minimum and maximum baseline HbA1c, as stated in the DIAMOND study */
	private static final double []MIN_MAX_BASELINE_HBA1C = {7.5, 9.9};
	/** Mean and SD of baseline age. SD is the highest from all the cohorts */
	private static final double []BASELINE_AGE = {46.217, 14};
	/** Minimum and maximum of baseline age. From DIAMOND */
	private static final double []MIN_MAX_BASELINE_AGE = {26, 73};
	/** Mean and SD of baseline duration of diabetes. SD from GOLD */
	private static final double []BASELINE_DURATION = {22.16619718, 11.8};
	/** Beta parameters (cases, no cases) for the initial proportion of proliferative retinopathy, according to the GOLD study */
	private static final double []P_INI_PRET_BETA = {28, 300-28}; 
	/** Beta parameters (cases, no cases) for the initial proportion of lower amputation, according to the GOLD study */
	private static final double []P_INI_LEA_BETA = {1, 300-1}; 
	/** Beta parameters (cases, no cases) for the initial proportion of myocardial infarction, according to the GOLD study */
	private static final double []P_INI_MI_BETA = {3, 300-3}; 
	/** Beta parameters (cases, no cases) for the initial proportion of stroke, according to the GOLD study */
	private static final double []P_INI_STROKE_BETA = {2, 300-2}; 
	/** Beta parameters (cases, no cases) for the initial proportion of heart failure, according to the GOLD study */
	private static final double []P_INI_HF_BETA = {1, 300-1}; 
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	private static final double DEF_U_GENERAL_POP = 0.911400915;

	/**
	 * @param secParams
	 * @param disease
	 */
	public T1DMGoldDiamondPopulation(SecondOrderParamsRepository secParams, Disease disease) throws MalformedSimulationModelException {
		super(secParams, "GOLD_DIAM_POP", "Population for T1DM according to GOLD and Diamond studies", disease);
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 132.0 / 300.0);
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
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		final double mode = Statistics.betaModeFromMeanSD(BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_HBA1C[0], mode, MIN_MAX_BASELINE_HBA1C[0], MIN_MAX_BASELINE_HBA1C[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);			

		secParams.addParameter(new FirstOrderNatureParameter(getRepository(), T1DMRepository.STR_HBA1C, new ParameterDescription(T1DMRepository.STR_HBA1C, ""), 
				RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_HBA1C[1] - MIN_MAX_BASELINE_HBA1C[0], MIN_MAX_BASELINE_HBA1C[0])), ParameterType.ATTRIBUTE);
		secParams.addParameter(new FirstOrderNatureParameter(getRepository(), T1DMRepository.STR_DURATION, new ParameterDescription(T1DMRepository.STR_DURATION, ""), 
				RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1])), ParameterType.ATTRIBUTE);

		UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, this, "From adult Spanish population but those with DM", DEF_U_GENERAL_POP);
		RiskParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getDiseaseProgression(ProliferativeRetinopathy.NAME), 
				"GOLD", P_INI_PRET_BETA[0] / (P_INI_PRET_BETA[0] + P_INI_PRET_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_PRET_BETA[0], P_INI_PRET_BETA[1]));
		RiskParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getDiseaseProgression(LowExtremityAmputation.NAME), 
				"GOLD", P_INI_LEA_BETA[0] / (P_INI_LEA_BETA[0] + P_INI_LEA_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_LEA_BETA[0], P_INI_LEA_BETA[1]));
		RiskParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getDiseaseProgression(MyocardialInfarction.NAME), 
				"GOLD", P_INI_MI_BETA[0] / (P_INI_MI_BETA[0] + P_INI_MI_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_MI_BETA[0], P_INI_MI_BETA[1]));
		RiskParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getDiseaseProgression(Stroke.NAME), 
				"GOLD", P_INI_STROKE_BETA[0] / (P_INI_STROKE_BETA[0] + P_INI_STROKE_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_STROKE_BETA[0], P_INI_STROKE_BETA[1]));
		RiskParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getDiseaseProgression(HeartFailure.NAME), 
				"GOLD", P_INI_HF_BETA[0] / (P_INI_HF_BETA[0] + P_INI_HF_BETA[1]), RandomVariateFactory.getInstance("BetaVariate", P_INI_HF_BETA[0], P_INI_HF_BETA[1]));
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		final double mode = Statistics.betaModeFromMeanSD(BASELINE_AGE[0], BASELINE_AGE[1]);
		
		final double[] betaParams = Statistics.betaParametersFromEmpiricData(BASELINE_AGE[0], mode, MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[1]);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", getCommonRandomNumber(), betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_BASELINE_AGE[1] - MIN_MAX_BASELINE_AGE[0], MIN_MAX_BASELINE_AGE[0]);			
	}

	@Override
	public int getMinAge() {
		return (int)MIN_MAX_BASELINE_AGE[0];
	}
	
	@Override
	public int getMaxAge() {
		return (int)MIN_MAX_BASELINE_AGE[1];
	}

	@Override
	public DiseaseProgression getDeathCharacterization() {
		return new EmpiricalSpainDeathSubmodel(getRepository(), disease);
	}
	
}
