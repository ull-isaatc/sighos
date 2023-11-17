/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class CostCalculator implements ParameterCalculator {
	private final ParameterCalculator originalCalculator;
	/** Year when the calculated cost was originally estimated */
	private final int year;
	/**
	 * 
	 */
	public CostCalculator(int year, ParameterCalculator originalCalculator) {
		this.originalCalculator = originalCalculator;
		this.year = year;
	}

	@Override
	public double getValue(Patient pat) {
		return SpanishCPIUpdate.updateCost(originalCalculator.getValue(pat), year, SecondOrderParamsRepository.getStudyYear());
	}
	
	/**
	 * Returns the year when the parameter was originally estimated
	 * @return the year when the parameter was originally estimated
	 */
	public int getYear() {
		return year;
	}

}
