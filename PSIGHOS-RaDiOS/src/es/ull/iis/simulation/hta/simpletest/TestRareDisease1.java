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

/**
 * A disease with two chronic manifestations in increasing order or severity. Each manifestation supposes a new stage in the development of the disease and replaces the previous one
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease1 extends StagedDisease {
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF1_MANIF2 = 0.3;
	/** First stage of the disease (mild) */
	final private Manifestation manif1;
	/** Second stage of the disease (severe) */
	final private Manifestation manif2;
	/** Transition from the first to the second stage */
	final private Transition manif1_manif2;
	/** Transition from no manifestations to the first stage of the disease */
	final private Transition healthy_manif1;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease1(SecondOrderParamsRepository secParams) {
		super(secParams, "RD1", "Test rare disease 1");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		addManifestation(manif1);
		addManifestation(manif2);
		healthy_manif1 = new Transition(secParams, getNullManifestation(), manif1, true) {
			@Override
			protected TimeToEventParam getTimeToEventParam(int id) {
				return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
						secParams.getProbability(manif1, id), SecondOrderParamsRepository.NO_RR);
			}
		}; 
		addTransition(healthy_manif1);
		manif1_manif2 = new Transition(secParams, manif1, manif2, true) {
			@Override
			protected TimeToEventParam getTimeToEventParam(int id) {			
				return new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), 
						secParams.getProbability(manif1, manif2, id), ((TestSimpleRareDiseaseRepository)secParams).getInterventionRR());
			}
		}; 
		addTransition(manif1_manif2);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		secParams.addProbParam(manif1, manif2, "Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}
}
