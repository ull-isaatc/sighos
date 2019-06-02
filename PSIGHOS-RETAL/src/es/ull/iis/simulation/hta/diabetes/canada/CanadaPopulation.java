/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author icasrod
 *
 */
public class CanadaPopulation extends DiabetesStdPopulation {

	/**
	 * @param secParams
	 * @param type
	 */
	public CanadaPopulation() {
		super(DiabetesType.T1);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getPMan()
	 */
	@Override
	public double getPMan() {
		return 0.5;
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", 8.8);
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", 27);
	}

	@Override
	public RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}
	
}
