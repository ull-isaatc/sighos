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
	private final Parameter originalParameter;
	/** Year when the calculated cost was originally estimated */
	private final int year;

	/**
	 * 
	 * @param originalParameter
	 * @param year
	 */
	public CostParameter(Parameter originalParameter, int year) {
		super(originalParameter.getRepository(), originalParameter.getType(), originalParameter.getName(), originalParameter.getDescription(), originalParameter.getSource());
		this.originalParameter = originalParameter;
		this.year = year;
	}

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
	public CostParameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source, double value, int year) {
		this(new ConstantParameter(secParams, type, name, description, source, value), year);
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
	public CostParameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source, double detValue, RandomVariate rnd, int year) {
		this(new SecondOrderParameter(secParams, type, name, description, source, detValue, rnd), year);
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
	public CostParameter(SecondOrderParamsRepository secParams, DescribesParameter type, String name,
			String description, String source, ParameterCalculator calc, int year) {
		this(new CalculatedParameter(secParams, type, name, description, source, calc), year);
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
