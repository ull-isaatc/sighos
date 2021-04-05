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
 * A disease with two chronic (and independent) manifestations, and one acute manifestation. The first chronic manifestation increases the risk of the second. 
 * The intervention reduces the risk of the acute and the second chronic manifestation
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease4 extends StagedDisease {
	final private static double P_ACUTE_MANIF1 = 0.15;
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF2 = 0.02;
	final private static double P_MANIF1_MANIF2 = 0.05;
	/** The acute manifestation */
	final private Manifestation acuteManif1;
	/** First manifestation (mild) */
	final private Manifestation manif1;
	/** Second manifestation (severe) */
	final private Manifestation manif2;
	/** Transition from the first to the second manifestation */
	final private Transition manif1_manif2;
	/** Transition to the first manifestation */
	final private Transition healthy_manif1;
	/** Transition to the second manifestation */
	final private Transition healthy_manif2;
	/** Transition to the acute manifestation */
	final private Transition toAcuteManif1;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease4(SecondOrderParamsRepository secParams) {
		super(secParams, "RD4", "Test rare disease 4");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		acuteManif1 = new TestAcuteManifestation1(secParams, this);
		addManifestation(acuteManif1);
		addManifestation(manif1);
		addManifestation(manif2);
		healthy_manif1 = new Transition(secParams, getNullManifestation(), manif1, true); 
		addTransition(healthy_manif1);
		healthy_manif2 = new Transition(secParams, getNullManifestation(), manif2, true); 
		addTransition(healthy_manif2);
		manif1_manif2 = new Transition(secParams, manif1, manif2, false); 
		addTransition(manif1_manif2);
		toAcuteManif1 = new Transition(secParams, getNullManifestation(), acuteManif1, false); 
		addTransition(toAcuteManif1);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(getNullManifestation(), acuteManif1, "Test", P_ACUTE_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_ACUTE_MANIF1));
		secParams.addProbParam(getNullManifestation(), manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		secParams.addProbParam(getNullManifestation(), manif2, "Test", P_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF2));
		secParams.addProbParam(manif1, manif2, "Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
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
