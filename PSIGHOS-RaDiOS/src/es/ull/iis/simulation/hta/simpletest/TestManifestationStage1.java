/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestManifestationStage1 extends ChronicManifestation {
	private final static double ANNUAL_COST = 100.0;
	private final static double DISUTILITY = 0.2;

	/**
	 * @param secParams
	 * @param disease
	 */
	public TestManifestationStage1(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "MANIF1", "Mild manifestation of test disease", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addIMRParam(this, "Increased mortaility due to " + this, "Test", 1.5, RandomVariateFactory.getInstance("UniformVariate", 1.3, 1.7));
		secParams.addCostParam(this, "Annual cost for " + this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
	}

}
