/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

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

	@Override
	public void update(Patient pat, double value, double initAge, double endAge) {
		final int patientId = pat.getIdentifier();
		final int interventionId = pat.getnIntervention();
		value = applyDiscount(value, initAge, endAge);
		qalys[patientId][interventionId] += value;
		aggregated[interventionId] += value;
	}

	@Override
	public void update(Patient pat, double value, double age) {
		final int interventionId = pat.getnIntervention();
		value = applyPunctualDiscount(value, age);
		qalys[pat.getIdentifier()][interventionId] += value;
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
