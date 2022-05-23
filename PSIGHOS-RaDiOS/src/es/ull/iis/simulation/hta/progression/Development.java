/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * A development for a disease. A disease may have different developments (natural vs modified, severe vs mild, ...).
 * @author Iván Castilla Rodríguez
 *
 */
public class Development implements Named, Describable {
	private final Disease disease;
	private final String name;
	private final String description;
	/**
	 * 
	 */
	public Development(String name, String description, Disease disease) {
		this.disease = disease;
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
