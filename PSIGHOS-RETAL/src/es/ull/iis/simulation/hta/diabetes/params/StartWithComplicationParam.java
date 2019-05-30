/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StartWithComplicationParam extends UniqueEventParam<Boolean> {
	private final double initProbability;
	
	public StartWithComplicationParam(RandomNumber rng, int nPatients, double initProbability) {
		super(rng, nPatients);
		this.initProbability = initProbability;
	}

	@Override
	public Boolean getValue(DiabetesPatient pat) {
		return draw(pat) < initProbability;
	}

}
