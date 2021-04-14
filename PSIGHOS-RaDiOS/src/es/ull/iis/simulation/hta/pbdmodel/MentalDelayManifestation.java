/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MentalDelayManifestation extends Manifestation {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 1218.02;
	private final static double ANNUAL_COST = 217.74;
	private final static double DU = 0.07;

	/**
	 * @param secParams
	 * @param disease
	 */
	public MentalDelayManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationMentalDelay", "Mental delay", disease, Type.CHRONIC, 1.0, 2.0);
	}
	
	@Override
	public void registerSecondOrderParameters() {
		secParams.addTransitionCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, DIAGNOSTIC_COST, SecondOrderParamsRepository.getRandomVariateForCost(DIAGNOSTIC_COST));		
		secParams.addCostParam(this, "Annual cost for " + this, "Test", COST_YEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addLERParam(this, "Life expectancy reduction for mental delay", "", 9.6, RandomVariateFactory.getInstance("GammaVariate", 25, 0.38));
		secParams.addDiagnosisProbParam(this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
	}

}
