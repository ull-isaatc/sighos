/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.StagedDisease;
import es.ull.iis.simulation.hta.progression.Transition;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease1 extends StagedDisease {
	final private Manifestation manif1;
	final private Manifestation manif2;
	final private Transition manif1_manif2;
	final private Transition healthy_manif1;
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public TestRareDisease1(SecondOrderParamsRepository secParams) {
		super(secParams, "RD", "Test rare disease 1");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		addManifestation(manif1);
		addManifestation(manif2);
		healthy_manif1 = new HealthyToMildManifestationTransition(secParams); 
		addTransition(healthy_manif1);
		manif1_manif2 = new MildToSevereManifestationTransition(secParams); 
		addTransition(manif1_manif2);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(getManifestations()[0], "", 0.1, RandomVariateFactory.getInstance("UniformVariate", 0.09, 0.11));
		secParams.addProbParam(getManifestations()[0], getManifestations()[1], "", 0.3, RandomVariateFactory.getInstance("UniformVariate", 0.25, 0.35));
	}

	final private class HealthyToMildManifestationTransition extends Transition {

		public HealthyToMildManifestationTransition(SecondOrderParamsRepository secParams) {
			super(secParams, getNullManifestation(), manif1, true);
		}

		@Override
		protected TimeToEventParam getTimeToEventParam(int id) {
			return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
					secParams.getProbability(getManifestations()[0], id), SecondOrderParamsRepository.NO_RR);
		}
		
	}

	final private class MildToSevereManifestationTransition extends Transition {

		public MildToSevereManifestationTransition(SecondOrderParamsRepository secParams) {
			super(secParams, manif1, manif2, true);
		}

		@Override
		protected TimeToEventParam getTimeToEventParam(int id) {
			return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
//					secParams.getProbability(getManifestations()[0], getManifestations()[1], id), SecondOrderParamsRepository.NO_RR);
			// FIXME: Esto no funciona. Averiguar por qué
					secParams.getProbability(getManifestations()[0], getManifestations()[1], id), ((SimpleRareDiseaseRepository)secParams).getInterventionRR());
		}
		
	}
}
