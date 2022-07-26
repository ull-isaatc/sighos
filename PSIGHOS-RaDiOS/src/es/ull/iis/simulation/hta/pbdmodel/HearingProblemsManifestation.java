/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class HearingProblemsManifestation extends ChronicManifestation {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 31169.95;
	private final static double ANNUAL_COST = 155.14;
	private final static double DU = 0.01;
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public HearingProblemsManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationHearingProblems", "Hearing problems", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		OtherParamDescriptions.ONSET_AGE.addParameter(secParams, this, "", 1.0);			
		OtherParamDescriptions.END_AGE.addParameter(secParams, this, "", 2.0);			
		secParams.addCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, DIAGNOSTIC_COST, SecondOrderParamsRepository.getRandomVariateForCost(DIAGNOSTIC_COST), true);		
		secParams.addCostParam(this, "Annual cost for " + this, "Test", COST_YEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));		
		ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		secParams.addUtilityParam(this, "Disutility for " + this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2), true);
	}

}
