/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SeizuresManifestation extends ChronicManifestation {
	private final static int COST_YEAR = 2013;
	private final static double COST = 3665.56;
	private final static double DU = 0.04;

	/**
	 * @param secParams
	 * @param disease
	 */
	public SeizuresManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationSeizure", "Seizures", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		OtherParamDescriptions.ONSET_AGE.addParameter(secParams, this, "", 0.0);			
		OtherParamDescriptions.END_AGE.addParameter(secParams, this, "", 1.0);			
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "Test", COST_YEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));		
		ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
	}

}
