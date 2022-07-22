package es.ull.iis.simulation.hta.params;

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
	 * Defines the ways the costs should be used: as an annual cost, i.e. multiplying the value by the years when it applies; 
	 * or as a one-time cost, i.e., applying the cost once 
	 */ 
	public static enum TemporalBehavior {
		ANNUAL,
		ONE_TIME
	}
	/** The way the cost should be used: as an annual cost, i.e. multiplying the value by the years when it applies; or as a one-time cost, i.e., applying the cost once */ 
	final private TemporalBehavior behavior;

	/**
	 * Creates a second order parameter for a unit cost of a specified year that must be updated to the simulation year,
	 * as specified in {@link BasicConfigParams#STUDY_YEAR}. The parameter value is fixed.
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param year Year when the cost was originally estimated
	 * @param detValue Deterministic/expected value
	 */
	public SecondOrderCostParam(SecondOrderParamsRepository secParams, String name, String description, String source, TemporalBehavior behavior, int year, double detValue) {
		super(secParams, name, description, source, detValue);
		this.year = year;
		generatedValues[0] = SpanishCPIUpdate.updateCost(generatedValues[0], year, BasicConfigParams.STUDY_YEAR);
		this.behavior = behavior;
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
	public SecondOrderCostParam(SecondOrderParamsRepository secParams, String name, String description, String source, TemporalBehavior behavior, int year, double detValue, RandomVariate rnd) {
		super(secParams, name, description, source, detValue, rnd);
		this.year = year;
		generatedValues[0] = SpanishCPIUpdate.updateCost(generatedValues[0], year, BasicConfigParams.STUDY_YEAR);
		this.behavior = behavior;
	}

	@Override
	public double getValue(int id) {
		if (Double.isNaN(generatedValues[id])) {
				generatedValues[id] = SpanishCPIUpdate.updateCost(rnd.generate(), year, BasicConfigParams.STUDY_YEAR);
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
	 * Returns the way the cost should be used: as an annual cost, i.e. multiplying the value by the years when it applies; 
	 * or as a one-time cost, i.e., applying the cost once
	 * @return the way the cost should be used
	 */
	public TemporalBehavior getTemporalBehavior() {
		return behavior;
	}
}