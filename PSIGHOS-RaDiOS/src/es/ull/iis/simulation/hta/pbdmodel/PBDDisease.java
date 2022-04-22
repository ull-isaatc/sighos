/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDDisease extends StandardDisease {
	final private static double DIAGNOSIS_COST = 509.65;
	final private static double TREATMENT_COST = 66.2475;
	final private static double FOLLOW_UP_COST = 616.75672;
	final private static String STR_C_DIAGNOSIS = SecondOrderParamsRepository.STR_COST_PREFIX + "DiagnosticPBD";
	final private static String STR_C_TREATMENT = SecondOrderParamsRepository.STR_COST_PREFIX + "TreatmentPBD";
	final private static String STR_C_FOLLOW_UP = SecondOrderParamsRepository.STR_COST_PREFIX + "FollowUpPBD";
	final private Manifestation skinProblems;
	final private Manifestation hypotonia;
	final private Manifestation seizures;
	final private Manifestation visionLoss;
	final private Manifestation hearingProblems;
	final private Manifestation mentalDelay;
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public PBDDisease(SecondOrderParamsRepository secParams) {
		super(secParams, "PBD", "Profound Biotidinase Deficiency");
		skinProblems = new SkinProblemsManifestation(secParams, this);
		registerBasicManifestation(skinProblems);
		hypotonia = new HypotoniaManifestation(secParams, this);
		registerBasicManifestation(hypotonia);
		seizures = new SeizuresManifestation(secParams, this);
		registerBasicManifestation(seizures);
		visionLoss = new VisionLossManifestation(secParams, this);
		registerBasicManifestation(visionLoss);
		hearingProblems = new HearingProblemsManifestation(secParams, this);
		registerBasicManifestation(hearingProblems);
		mentalDelay = new MentalDelayManifestation(secParams, this);
		registerBasicManifestation(mentalDelay);
	}

	private void registerBasicManifestation(Manifestation manif) {
		new ManifestationPathway(secParams, manif, new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manif), secParams, manif));
	}
	
	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(skinProblems, "Test", 0.41, RandomVariateFactory.getInstance("BetaVariate", 24, 34));
		secParams.addProbParam(hypotonia, "Test", 0.457, RandomVariateFactory.getInstance("BetaVariate", 17, 20));
		secParams.addProbParam(seizures, "Test", 0.564, RandomVariateFactory.getInstance("BetaVariate", 65, 50));
		secParams.addProbParam(visionLoss, "Test", 0.175, RandomVariateFactory.getInstance("BetaVariate", 19, 91));
		secParams.addProbParam(hearingProblems, "Test", 0.515, RandomVariateFactory.getInstance("BetaVariate", 65, 61));
		secParams.addProbParam(mentalDelay, "Test", 0.557, RandomVariateFactory.getInstance("BetaVariate", 14, 6));
		secParams.addCostParam(new SecondOrderCostParam(secParams, STR_C_DIAGNOSIS, "Cost of diagnosing PBD", "", 2013, DIAGNOSIS_COST, RandomVariateFactory.getInstance("UniformVariate", 409.65, 609.65)));
		secParams.addCostParam(new SecondOrderCostParam(secParams, STR_C_TREATMENT, "Cost of treating PBD", "", 2013, TREATMENT_COST));
		secParams.addCostParam(new SecondOrderCostParam(secParams, STR_C_FOLLOW_UP, "Cost of following up PBD", "", 2013, FOLLOW_UP_COST));
	}

	@Override
	public double getDiagnosisCost(Patient pat) {
		return secParams.getCostParam(STR_C_DIAGNOSIS, pat.getSimulation());
	}

	@Override
	public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
		return secParams.getCostParam(STR_C_TREATMENT, pat.getSimulation()) + secParams.getCostParam(STR_C_FOLLOW_UP, pat.getSimulation());
	}
}
