/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class OriginalMonitoDM1Population extends DiabetesStdPopulation {

	/**
	 * @param type
	 */
	public OriginalMonitoDM1Population() {
		super(DiabetesType.T1);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getPMan()
	 */
	@Override
	protected double getPMan() {
		return 0.5;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineHBA1c()
	 */
	@Override
	protected RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", 8.5);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineAge()
	 */
	@Override
	protected RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", 26);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation#getBaselineDurationOfDiabetes()
	 */
	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("ConstantVariate", 6);
	}

}
