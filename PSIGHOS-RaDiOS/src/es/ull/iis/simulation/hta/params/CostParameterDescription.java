package es.ull.iis.simulation.hta.params;

/**
 * The minimum required information to characterize a cost parameter within an HTA simulation. Includes the year when the cost was originally estimated.
 * @author Iván Castilla Rodríguez
 */

public class CostParameterDescription extends ParameterDescription {
	/** Year when the calculated cost was originally estimated */
	private final int year;

    public CostParameterDescription(String description, String source, int year) {
        super(description, source);
        this.year = year;
    }
	
	/**
	 * Returns the year when the parameter was originally estimated
	 * @return the year when the parameter was originally estimated
	 */
	public int getYear() {
		return year;
	}
}