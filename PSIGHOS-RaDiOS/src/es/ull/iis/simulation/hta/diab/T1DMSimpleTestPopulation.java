/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.ParameterType;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
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
public class T1DMSimpleTestPopulation extends StdPopulation {
	/** Mean baseline HbA1c */
	private static final double BASELINE_HBA1C = 8.5382;
	/** Mean baseline age.  */
	private static final double BASELINE_AGE = 46.217;
	/** Mean baseline duration of diabetes */
	private static final double BASELINE_DURATION = 22.16619718;
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	private static final double DEF_U_GENERAL_POP = 0.911400915;

	/**
	 * @param secParams
	 * @param disease
	 */
	public T1DMSimpleTestPopulation(SecondOrderParamsRepository secParams, Disease disease) throws MalformedSimulationModelException {
		super(secParams, "T1DMPop", "Simple test population for T1DM", disease);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		secParams.addParameter(new Parameter(getRepository(), T1DMRepository.STR_HBA1C, T1DMRepository.STR_HBA1C, "", BASELINE_HBA1C), ParameterType.ATTRIBUTE);
		secParams.addParameter(new Parameter(getRepository(), T1DMRepository.STR_DURATION, T1DMRepository.STR_DURATION, "", BASELINE_DURATION), ParameterType.ATTRIBUTE);
		
		UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, this, "From adult Spanish population but those with DM", DEF_U_GENERAL_POP);
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
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE);			
	}

	@Override
	public DiseaseProgression getDeathCharacterization() {
		return new EmpiricalSpainDeathSubmodel(getRepository(), disease);
	}
	
}
