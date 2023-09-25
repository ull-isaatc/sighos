/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.outcomes.Strategy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 * TODO Either implement here or in the Disease class
 */
public class StrategyBasedDisease extends Disease {
	private Strategy diagnosisStrategy = null;
	private Strategy screeningStrategy = null;
	private Strategy treatmentStrategy = null;
	private Strategy followUpStrategy = null;
	private Strategy lineOfTherapy = null;
	
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public StrategyBasedDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

}
