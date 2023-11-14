package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;

/**
 * A special kind of second order parameter that updates the values according to the Spanish IPC.
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderCostParam extends SecondOrderParam {
	/** Year when the cost was originally estimated */
	final private int year;
	/** Type of parameter. Affects its temporal behavior, among other issues */ 
	final private CostParamDescriptions type;

	/**
	 * Creates a second order parameter for a unit cost of a specified year that must be updated to the simulation year,
	 * as specified in {@link BasicConfigParams#STUDY_YEAR}. The parameter value is fixed.
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param type Type of parameter. Affects its temporal behavior, among other issues 
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 */
	public SecondOrderCostParam(SecondOrderParamsRepository secParams, String name, String description, String source, CostParamDescriptions type, int year, double detValue) {
		super(secParams, name, description, source, detValue);
		this.year = year;
		generatedValues[0] = SpanishCPIUpdate.updateCost(generatedValues[0], year, secParams.getStudyYear());
		this.type = type;
	}
	
	/**
	 * 
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public SecondOrderCostParam(SecondOrderParamsRepository secParams, String name, String description, String source, CostParamDescriptions type, int year, double detValue, RandomVariate rnd) {
		super(secParams, name, description, source, detValue, rnd);
		this.year = year;
		generatedValues[0] = SpanishCPIUpdate.updateCost(generatedValues[0], year, secParams.getStudyYear());
		this.type = type;
	}

	@Override
	public double getValue(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (Double.isNaN(generatedValues[id])) {
				generatedValues[id] = SpanishCPIUpdate.updateCost(rnd.generate(), year, secParams.getStudyYear());
		}
		return generatedValues[id];
	}
	
	/**
	 * Returns the year when the cost was originally estimated
	 * @return the year when the cost was originally estimated
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Returns the type of parameter. Affects its temporal behavior, among other issues
	 * @return the type of parameter
	 */
	public CostParamDescriptions getType() {
		return type;
	}
}