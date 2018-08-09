/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import java.util.LinkedList;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.params.VAProgressionPair;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class QualityAdjustedLifeExpectancy extends Outcome {

	/**
	 * @param simul
	 */
	public QualityAdjustedLifeExpectancy(RETALSimulation simul, double discountRate) {
		super(simul, "Quality Adjusted Life Expectancy", "QALY", discountRate);
	}

	public void update(Patient pat, LinkedList<VAProgressionPair> progression) { 
		long t1 = 0;
		long t2;
		for (VAProgressionPair pair : progression) {
			t2 = t1 + pair.timeToChange;
			double utility = pair.va;
			final int interventionId = pat.getnIntervention();
			utility = applyDiscount(utility, TimeUnit.DAY.convert(t1, simul.getTimeUnit()) / 365.0, TimeUnit.DAY.convert(t2, simul.getTimeUnit()) / 365.0);
			values[interventionId][pat.getIdentifier()] += utility;
			aggregated[interventionId] += utility;
		}
	}
}
