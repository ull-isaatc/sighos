package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.SpanishIPCUpdate;
import simkit.random.RandomVariate;

/**
 * A special kind of second order parameter that updates the values according to the Spanish IPC.
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderCostParam extends SecondOrderParam {
	/** Year when the cost was originally estimated */
	final private int year;

	/**
	 * Creates a second order parameter for a unit cost of a specified year that must be updated to the simulation year,
	 * as specified in {@link BasicConfigParams#STUDY_YEAR}. The parameter value is fixed.
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 */
	public SecondOrderCostParam(String name, String description, String source, int year, double detValue) {
		super(name, description, source, detValue);
		this.year = year;
	}
	
	/**
	 * 
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public SecondOrderCostParam(String name, String description, String source, int year, double detValue, RandomVariate rnd) {
		super(name, description, source, detValue, rnd);
		this.year = year;
	}

	@Override
	public void generate(SecondOrderParamsRepository secOrder) {
		super.generate(secOrder);
		generatedValues.replaceAll(e -> SpanishIPCUpdate.updateCost(e, year, BasicConfigParams.STUDY_YEAR));
/*		for (int i = 1; i < n; i++)
			generatedValues.set(i, SpanishIPCUpdate.updateCost(getValue(i), year, BasicConfigParams.STUDY_YEAR));*/		
	}
	
	/**
	 * Returns the year when the cost was originally estimated
	 * @return the year when the cost was originally estimated
	 */
	public int getYear() {
		return year;
	}
}