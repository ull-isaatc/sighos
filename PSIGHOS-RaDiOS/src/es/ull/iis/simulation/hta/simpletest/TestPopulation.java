/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestPopulation extends StdPopulation {

	/**
	 * @param disease
	 */
	public TestPopulation(Disease disease) {
		super(disease);
		// TODO Auto-generated constructor stub
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
	public void generate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerSecondOrderParameters() {
		// TODO Auto-generated method stub
		
	}

}
