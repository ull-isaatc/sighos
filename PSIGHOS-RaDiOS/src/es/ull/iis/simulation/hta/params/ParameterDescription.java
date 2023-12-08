package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.model.Describable;

/**
 * The minimum required information to characterize a parameter within an HTA simulation.
 * @author Iván Castilla Rodríguez
 */
public class ParameterDescription implements Describable {
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;

	public ParameterDescription() {
		this("","");
	}

	/**
	 * 
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 */
    public ParameterDescription(String description, String source) {
        this.description = description;
        this.source = source;
    }

	/**
	 * Returns the full description of the parameter
	 * @return the full description of the parameter
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the reference from which this parameter was estimated/taken
	 * @return the reference from which this parameter was estimated/taken
	 */
	public String getSource() {
		return source;
	}

}
