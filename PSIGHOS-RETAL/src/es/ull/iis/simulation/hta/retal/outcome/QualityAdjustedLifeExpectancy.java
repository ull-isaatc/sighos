/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import java.util.LinkedList;

import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.params.VAProgressionPair;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class QualityAdjustedLifeExpectancy extends Outcome {

	/**
	 * @param simul
	 * @param discountRate
	 */
	public QualityAdjustedLifeExpectancy(HTASimulation simul, double discountRate) {
		super(simul, "Quality Adjusted Life Expectancy", "QALY", discountRate);
	}

	public void update(RetalPatient pat, LinkedList<VAProgressionPair> progression) { 
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
