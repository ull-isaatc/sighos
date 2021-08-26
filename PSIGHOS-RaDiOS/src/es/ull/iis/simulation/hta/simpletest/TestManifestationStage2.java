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
public class TestManifestationStage2 extends ChronicManifestation {
	private final static double ANNUAL_COST = 1000.0;
	private final static double DISUTILITY = 0.5;
	private final static double IMR = 5.0;
	private final static double P_DIAG = 0.8;

	/**
	 * @param secParams
	 * @param disease
	 */
	public TestManifestationStage2(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "MANIF2", "Severe manifestation of test disease", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addIMRParam(this, "Increased mortaility due to " + this, "Test", IMR, RandomVariateFactory.getInstance("UniformVariate", IMR - 1, IMR + 1));
		secParams.addCostParam(this, "Annual cost for " + this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addDisutilityParam(this, "Disutility for " + this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.1, DISUTILITY + 0.1));
		secParams.addDiagnosisProbParam(this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
