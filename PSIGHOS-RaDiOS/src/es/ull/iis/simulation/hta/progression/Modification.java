/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;

/**
 * Represents a modification of the development of a disease or a manifestation
 * @author Iván Castilla
 *
 */
public class Modification extends SecondOrderParam {
	public enum Type {
		DIFF,	// A difference from the previous value
		RR,		// Relative risk
		SET		// Establish a new value
	}
	final private Type type;
	/**
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param detValue
	 * @param rnd
	 */
	public Modification(SecondOrderParamsRepository secParams, Type type, String name, String description, String source,
			double detValue, RandomVariate rnd) {
		super(secParams, name, description, source, detValue, rnd);
		this.type = type;
	}
	/**
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param detValue
	 * @param rndFunction
	 * @param params
	 */
	public Modification(SecondOrderParamsRepository secParams, Type type, String name, String description, String source,
			double detValue, String rndFunction, Object... params) {
		super(secParams, name, description, source, detValue, rndFunction, params);
		this.type = type;
	}
	/**
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param detValue
	 */
	public Modification(SecondOrderParamsRepository secParams, Type type, String name, String description, String source,
			double detValue) {
		super(secParams, name, description, source, detValue);
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

}
