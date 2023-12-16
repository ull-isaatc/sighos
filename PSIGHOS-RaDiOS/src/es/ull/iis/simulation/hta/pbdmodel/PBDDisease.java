/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.ProportionBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDDisease extends Disease {
	final private static double DIAGNOSIS_COST = 509.65;
	final private static double TREATMENT_COST = 66.2475;
	final private static double FOLLOW_UP_COST = 616.75672;
	final private DiseaseProgression skinProblems;
	final private DiseaseProgression hypotonia;
	final private DiseaseProgression seizures;
	final private DiseaseProgression visionLoss;
	final private DiseaseProgression hearingProblems;
	final private DiseaseProgression mentalDelay;
	/**
	 * @param model
	 * @param name
	 * @param description
	 */
	public PBDDisease(HTAModel model) {
		super(model, "PBD", "Profound Biotidinase Deficiency");
		skinProblems = new SkinProblemsManifestation(model, this);
		registerBasicManifestation(model, skinProblems);
		hypotonia = new HypotoniaManifestation(model, this);
		registerBasicManifestation(model, hypotonia);
		seizures = new SeizuresManifestation(model, this);
		registerBasicManifestation(model, seizures);
		visionLoss = new VisionLossManifestation(model, this);
		registerBasicManifestation(model, visionLoss);
		hearingProblems = new HearingProblemsManifestation(model, this);
		registerBasicManifestation(model, hearingProblems);
		mentalDelay = new MentalDelayManifestation(model, this);
		registerBasicManifestation(model, mentalDelay);
	}

	private void registerBasicManifestation(HTAModel model, DiseaseProgression manif) {
		new DiseaseProgressionPathway(model, "PATH_"+ manif.name(), "Pathway for " + manif.name(), manif);
	}

	@Override
	public void createParameters() {
		createParametersForManifestation(skinProblems, 0.41, 24, 34);
		createParametersForManifestation(hypotonia, 0.457, 17, 20);
		createParametersForManifestation(seizures, 0.564, 65, 50);
		createParametersForManifestation(visionLoss, 0.175, 19, 91);
		createParametersForManifestation(hearingProblems, 0.515, 65, 61);
		createParametersForManifestation(mentalDelay, 0.557, 14, 6);
		StandardParameter.DISEASE_DIAGNOSIS_COST.addParameter(model, this, "", 2013, DIAGNOSIS_COST, RandomVariateFactory.getInstance("UniformVariate", 409.65, 609.65));
		StandardParameter.FOLLOW_UP_COST.addParameter(model, this, "", 2013, FOLLOW_UP_COST);
		StandardParameter.TREATMENT_COST.addParameter(model, this, "", 2013, TREATMENT_COST);
	}
	
	private void createParametersForManifestation(DiseaseProgression manif, double proportion, int betaParam1, int betaParam2) {
		StandardParameter.PROPORTION.addParameter(model, manif, "", proportion, RandomVariateFactory.getInstance("BetaVariate", betaParam1, betaParam2));
		model.addParameter(new ProportionBasedTimeToEventParameter(model, StandardParameter.TIME_TO_EVENT.createName(manif), "Time to " +  manif.getDescription(), "", 2013, manif, StandardParameter.PROPORTION.createName(manif)));
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}
}
