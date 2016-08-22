/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class QualityAdjustedLifeExpectancy extends Outcome {
	private final double[][]qalys = new double[RETALSimulation.NPATIENTS][RETALSimulation.NINTERVENTIONS];
	private final double[]aggregated = new double[RETALSimulation.NINTERVENTIONS];

	/**
	 * @param simul
	 */
	public QualityAdjustedLifeExpectancy(RETALSimulation simul, double discountRate) {
		super(simul, "Quality Adjusted Life Expectancy", "QALY", discountRate);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.outcome.Outcome#getValue()
	 */
	@Override
	public double[] getValue() {
		return aggregated;
	}

	@Override
	public void update(Patient pat) {
		final int patientId = pat.getIdentifier() / RETALSimulation.NINTERVENTIONS;
		final int interventionId = pat.getIdentifier() % RETALSimulation.NINTERVENTIONS;
		final double value = applyDiscount(pat.getUtility(), TimeUnit.YEAR.convert(pat.getLastTs(), simul.getTimeUnit()), TimeUnit.YEAR.convert(pat.getTs(), simul.getTimeUnit()));
		qalys[patientId][interventionId] += value;
		aggregated[interventionId] += value;
	}

	@Override
	public void print(boolean detailed) {
		if (detailed) {
			for (int i = 0; i < RETALSimulation.NPATIENTS; i++) {
				System.out.print("[" + i + "]\t");
				for (int j = 0; j < RETALSimulation.NINTERVENTIONS; j++) {
					System.out.print(qalys[i][j] + "\t");
				}
				System.out.println();
			}
		}
		System.out.println(this + " summary:");
		for (int j = 0; j < RETALSimulation.NINTERVENTIONS; j++) {
			System.out.print(aggregated[j] / RETALSimulation.NPATIENTS + "\t");
		}
		System.out.println();
	}

}
