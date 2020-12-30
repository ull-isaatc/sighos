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
public class TestNotDiagnosedPopulation extends StdPopulation {
	private static final double BIRTH_PREVALENCE = 0.1;

	/**
	 * @param disease
	 */
	public TestNotDiagnosedPopulation(SecondOrderParamsRepository secParams, Disease disease) {
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
		return BIRTH_PREVALENCE;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	protected double getPDiagnosed() {
		return 0.0;
	}

}
