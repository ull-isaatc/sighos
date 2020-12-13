/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Transition;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease1 extends Disease {

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public TestRareDisease1(SecondOrderParamsRepository secParams) {
		super(secParams, "RD", "Test rare disease 1");
		addManifestation(new TestManifestationStage1(secParams, this));
		addManifestation(new TestManifestationStage2(secParams, this));
		addTransition(new HealthyToMildManifestationTransition(secParams));
		addTransition(new MildToSevereManifestationTransition(secParams));
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(getManifestations()[0], "", 0.1, RandomVariateFactory.getInstance("UniformVariate", 0.09, 0.11));
		secParams.addProbParam(getManifestations()[0], getManifestations()[1], "", 0.3, RandomVariateFactory.getInstance("UniformVariate", 0.25, 0.35));
	}

	@Override
	public DiseaseProgression getProgression(Patient pat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
		// TODO Auto-generated method stub
		return 0;
	}

	final private class HealthyToMildManifestationTransition extends Transition {

		public HealthyToMildManifestationTransition(SecondOrderParamsRepository secParams) {
			super(secParams, getManifestations()[0]);
		}

		@Override
		protected TimeToEventParam getTimeToEventParam(int id) {
			return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
					secParams.getProbability(getManifestations()[0], id), SecondOrderParamsRepository.NO_RR);
		}
		
	}

	final private class MildToSevereManifestationTransition extends Transition {

		public MildToSevereManifestationTransition(SecondOrderParamsRepository secParams) {
			super(secParams, getManifestations()[0], getManifestations()[1]);
		}

		@Override
		protected TimeToEventParam getTimeToEventParam(int id) {
			return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
					secParams.getProbability(getManifestations()[0], getManifestations()[1], id), SecondOrderParamsRepository.NO_RR);
		}
		
	}
}
