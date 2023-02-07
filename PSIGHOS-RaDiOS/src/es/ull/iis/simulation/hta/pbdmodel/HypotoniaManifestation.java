/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
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
		super(secParams, "#PBD_ManifestationHypotonia", "Hypotonia", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addOtherParam(new SecondOrderParam(secParams, getOnsetAgeParameterString(false), getOnsetAgeParameterString(true), "", 0.0));
		secParams.addOtherParam(new SecondOrderParam(secParams, getEndAgeParameterString(false), getEndAgeParameterString(true), "", 1.0));
		secParams.addCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, COST, SecondOrderParamsRepository.getRandomVariateForCost(COST), true);		
		secParams.addDiagnosisProbParam(this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
	}

}
