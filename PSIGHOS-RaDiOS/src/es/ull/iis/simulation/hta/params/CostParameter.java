/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class CostParameter extends Parameter {
	private final Parameter originalParameter;
	/** Year when the calculated cost was originally estimated */
	private final int year;
	/**
	 * 
	 */
	public CostParameter(int year, Parameter originalParameter) {
		super(originalParameter.getRepository(), originalParameter.getType(), originalParameter.getName(), originalParameter.getDescription(), originalParameter.getSource());
		this.originalParameter = originalParameter;
		this.year = year;
	}

	@Override
	public double calculateValue(Patient pat) {
		return SpanishCPIUpdate.updateCost(originalParameter.getValue(pat), year, SecondOrderParamsRepository.getStudyYear());
	}
	
	/**
	 * Returns the year when the parameter was originally estimated
	 * @return the year when the parameter was originally estimated
	 */
	public int getYear() {
		return year;
	}

}
