/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DiabPlusExplorationPopulation extends DiabetesStdPopulation {
	private static final double[] HBA1C_LIMITS = new double[] {6.0, 14.0};
	private static final double[] AGE_LIMITS = new double[] {20.0, 40.0};
	private static final double[] DURATION_LIMITS = new double[] {2.0, 35.0};
	
	public DiabPlusExplorationPopulation() {
		super(DiabetesType.T1);
	}

	@Override
	protected double getPMan() {
		return 0.5;
	}

	@Override
	protected RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("UniformVariate", HBA1C_LIMITS[0], HBA1C_LIMITS[1]);
	}

	@Override
	protected RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("UniformVariate", AGE_LIMITS[0], AGE_LIMITS[1]);
	}

	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("UniformVariate", DURATION_LIMITS[0], DURATION_LIMITS[1]);
	}

}
