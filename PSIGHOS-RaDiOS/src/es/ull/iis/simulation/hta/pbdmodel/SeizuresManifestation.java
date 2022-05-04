/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
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
		super(secParams, "#PBD_ManifestationSeizure", "Seizures", disease, 0.0, 1.0);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST), true);		
		secParams.addDiagnosisProbParam(this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
		secParams.addUtilityParam(this, "Disutility for " + this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2), true);
	}

}
