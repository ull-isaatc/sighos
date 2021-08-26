/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HypotoniaManifestation extends ChronicManifestation {
	private final static int COST_YEAR = 2013;
	private final static double COST = 3665.56;

	/**
	 * @param secParams
	 * @param disease
	 */
	public HypotoniaManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationHypotonia", "Hypotonia", disease, 0.0, 1.0);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addTransitionCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST));		
		secParams.addDiagnosisProbParam(this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
	}

}
