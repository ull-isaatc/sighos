/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;

/**
 * @author Iván Castilla
 *
 */
public class CostParameter extends Parameter {
	/** Year when the calculated cost was originally estimated */
	private final int year;

	/**
	 * 
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param value
	 * @param year
	 */
	public CostParameter(final SecondOrderParamsRepository secParams, String name, String description, String source, double value, int year) {
		super(secParams, name, description, source, new ConstantParameterCalculator(value));
		this.year = year;
	}

	/**
	 * 
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param detValue
	 * @param rnd
	 * @param year
	 */
	public CostParameter(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd, int year) {
		super(secParams, name, description, source, new SecondOrderParameterCalculator(secParams, detValue, rnd));
		this.year = year;
	}

	/**
	 * 
	 * @param secParams
	 * @param type
	 * @param name
	 * @param description
	 * @param source
	 * @param calc
	 * @param year
	 */
	public CostParameter(SecondOrderParamsRepository secParams, String name, String description, String source, ParameterCalculator calc, int year) {
		super(secParams, name, description, source, calc);
		this.year = year;		
	}
	
	@Override
	public double getValue(Patient pat) {
		return SpanishCPIUpdate.updateCost(super.getValue(pat), year, SecondOrderParamsRepository.getStudyYear());
	}
	
	/**
	 * Returns the year when the parameter was originally estimated
	 * @return the year when the parameter was originally estimated
	 */
	public int getYear() {
		return year;
	}

}
