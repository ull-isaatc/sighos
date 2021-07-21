/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.ClinicalParameter;
import es.ull.iis.simulation.hta.populations.InitiallySetClinicalParameter;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Combined characteristics from the GOLD and DIAMOND studies. Both compared DEXCOM G4 on T1DM population with uncontrolled HbA1c.
 * Sources:
 * <ul>
 * <li>Lind M, Polonsky W, Hirsch IB, Heise T, Bolinder J, Dahlqvist S, et al. Continuous glucose monitoring vs conventional therapy for glycemic control in adults with type 1 diabetes treated with multiple daily insulin injections the gold randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):379�87.</li>
 * <li>Beck RW, Riddlesworth T, Ruedy K, Ahmann A, Bergenstal R, Haller S, et al. Effect of continuous glucose monitoring on glycemic control in adults with type 1 diabetes using insulin injections the diamond randomized clinical trial. JAMA - J Am Med Assoc. 2017;317(4):371�8.</li>
 * </ul>
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class T1DMSimpleTestPopulation extends StdPopulation {
	/** Mean baseline HbA1c */
	private static final double BASELINE_HBA1C = 8.5382;
	/** Mean baseline age.  */
	private static final double BASELINE_AGE = 46.217;
	/** Mean baseline duration of diabetes */
	private static final double BASELINE_DURATION = 22.16619718;

	/**
	 * @param secParams
	 * @param disease
	 */
	public T1DMSimpleTestPopulation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, disease);
	}

	@Override
	public double getPDisease(DiseaseProgressionSimulation simul) {
		return 1.0;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	protected List<ClinicalParameter> getPatientParameterList() {
		final ArrayList<ClinicalParameter> paramList = new ArrayList<>();

		paramList.add(new InitiallySetClinicalParameter(T1DMRepository.STR_HBA1C, RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C)));
		paramList.add(new InitiallySetClinicalParameter(T1DMRepository.STR_DURATION, RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION)));
		return paramList;
	}
	
	@Override
	protected double getPMan(DiseaseProgressionSimulation simul) {
		return 168.0 / 300.0;
	}

	@Override
	protected double getPDiagnosed(DiseaseProgressionSimulation simul) {
		return 1.0;
	}

	@Override
	protected RandomVariate getBaselineAge(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE);			
	}
	
}