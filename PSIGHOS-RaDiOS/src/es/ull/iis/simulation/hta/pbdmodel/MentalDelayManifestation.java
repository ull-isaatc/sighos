/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MentalDelayManifestation extends DiseaseProgression {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 1218.02;
	private final static double ANNUAL_COST = 217.74;
	private final static double DU = 0.07;

	/**
	 * @param secParams
	 * @param disease
	 */
	public MentalDelayManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationMentalDelay", "Mental delay", disease, Type.CHRONIC_MANIFESTATION);
	}
	
	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		OtherParamDescriptions.ONSET_AGE.addParameter(secParams, this, "", 1.0);			
		OtherParamDescriptions.END_AGE.addParameter(secParams, this, "", 2.0);			
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "Test", COST_YEAR, DIAGNOSTIC_COST, SecondOrderParamsRepository.getRandomVariateForCost(DIAGNOSTIC_COST));		
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Test", COST_YEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		OtherParamDescriptions.LIFE_EXPECTANCY_REDUCTION.addParameter(secParams, this, "", 9.6, RandomVariateFactory.getInstance("GammaVariate", 25, 0.38));
		ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
	}

}
