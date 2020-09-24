/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.Named;

/**
 * Acute complication included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public class AcuteComplication implements Named {
	private final String name;	
	private final String description;
	
	private AcuteComplication(String name, String description) {
		this.description = description;
		this.name = name;
	}

	/**
	 * Returns the description of the complication
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

	
}
