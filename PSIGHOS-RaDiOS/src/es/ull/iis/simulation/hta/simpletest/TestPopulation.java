/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPopulation extends StdPopulation {

	/**
	 * @param disease
	 */
	public TestPopulation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, disease);
	}

	@Override
	protected double getPMan() {
		return 0.5;
	}

	@Override
	protected RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

	@Override
	public double getPDisease() {
		return 1.0;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	protected double getPDiagnosed() {
		return 1.0;
	}

}
