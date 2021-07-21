/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.StagedDisease;
import es.ull.iis.simulation.hta.progression.Transition;

/**
 * A disease with a single acute manifestation with recurrent episodes
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease2 extends StagedDisease {
	final private static double P_MANIF1 = 0.1;
	/** The acute manifestation */
	final private Manifestation acuteManif1;
	/** Transition to the first episode of the acute manifestation */
	final private Transition healthy_manif1;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease2(SecondOrderParamsRepository secParams) {
		super(secParams, "RD2", "Test rare disease 2");
		acuteManif1 = new TestAcuteManifestation1(secParams, this);
		addManifestation(acuteManif1);
		healthy_manif1 = new Transition(secParams, getAsymptomaticManifestation(), acuteManif1); 
		addTransition(healthy_manif1);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(getAsymptomaticManifestation(), acuteManif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
	}

	@Override
	public double getDiagnosisCost(Patient pat) {
		return 0;
	}

	@Override
	public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
		return 0;
	}
}
