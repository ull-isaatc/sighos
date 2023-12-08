/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.ProportionBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public PBDDisease(SecondOrderParamsRepository secParams) {
		super(secParams, "PBD", "Profound Biotidinase Deficiency");
		skinProblems = new SkinProblemsManifestation(secParams, this);
		registerBasicManifestation(secParams, skinProblems);
		hypotonia = new HypotoniaManifestation(secParams, this);
		registerBasicManifestation(secParams, hypotonia);
		seizures = new SeizuresManifestation(secParams, this);
		registerBasicManifestation(secParams, seizures);
		visionLoss = new VisionLossManifestation(secParams, this);
		registerBasicManifestation(secParams, visionLoss);
		hearingProblems = new HearingProblemsManifestation(secParams, this);
		registerBasicManifestation(secParams, hearingProblems);
		mentalDelay = new MentalDelayManifestation(secParams, this);
		registerBasicManifestation(secParams, mentalDelay);
	}

	private void registerBasicManifestation(SecondOrderParamsRepository secParams, DiseaseProgression manif) {
		new DiseaseProgressionPathway(secParams, manif, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(manif));
	}
	
	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, skinProblems, "Test", 0.41, RandomVariateFactory.getInstance("BetaVariate", 24, 34));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, hypotonia, "Test", 0.457, RandomVariateFactory.getInstance("BetaVariate", 17, 20));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, seizures, "Test", 0.564, RandomVariateFactory.getInstance("BetaVariate", 65, 50));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, visionLoss, "Test", 0.175, RandomVariateFactory.getInstance("BetaVariate", 19, 91));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, hearingProblems, "Test", 0.515, RandomVariateFactory.getInstance("BetaVariate", 65, 61));
		RiskParamDescriptions.PROBABILITY.addParameter(secParams, mentalDelay, "Test", 0.557, RandomVariateFactory.getInstance("BetaVariate", 14, 6));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(skinProblems), skinProblems, 
				RiskParamDescriptions.PROBABILITY.getParameterName(skinProblems)));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(hypotonia), hypotonia,
				RiskParamDescriptions.PROBABILITY.getParameterName(hypotonia)));	
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(seizures), seizures, 
				RiskParamDescriptions.PROBABILITY.getParameterName(seizures)));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(visionLoss), visionLoss,
				RiskParamDescriptions.PROBABILITY.getParameterName(visionLoss)));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(hearingProblems), hearingProblems,	
				RiskParamDescriptions.PROBABILITY.getParameterName(hearingProblems)));
		RiskParamDescriptions.TIME_TO_EVENT.addParameter(secParams, new ProportionBasedTimeToEventParameter(secParams, RiskParamDescriptions.TIME_TO_EVENT.getParameterName(mentalDelay), mentalDelay,	
				RiskParamDescriptions.PROBABILITY.getParameterName(mentalDelay)));
		
		CostParamDescriptions.DIAGNOSIS_COST.addParameter(secParams, this, "", 2013, DIAGNOSIS_COST, RandomVariateFactory.getInstance("UniformVariate", 409.65, 609.65));
		CostParamDescriptions.TREATMENT_COST.addParameter(secParams, this, "", 2013, TREATMENT_COST);
		CostParamDescriptions.FOLLOW_UP_COST.addParameter(secParams, this, "", 2013, FOLLOW_UP_COST);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}
}
