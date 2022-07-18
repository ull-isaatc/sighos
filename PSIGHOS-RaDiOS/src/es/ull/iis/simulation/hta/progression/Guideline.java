/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public class Guideline implements Named, Describable {
	private final String description;
	private final String name;

	/**
	 * 
	 */
	public Guideline(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

}
