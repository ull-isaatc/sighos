package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.params.SpanishIPCUpdate;
import simkit.random.RandomVariate;

/**
 * A special kind of second order parameter that updates the values according to the Spanish IPC.
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderCostParam extends SecondOrderParam {
	/** Year of the cost */
	final private int year;

	public SecondOrderCostParam(String name, String description, String source, int year, double detValue) {
		super(name, description, source, detValue);
		this.year = year;
	}
	
	public SecondOrderCostParam(String name, String description, String source, int year, double detValue, RandomVariate rnd) {
		super(name, description, source, detValue, rnd);
		this.year = year;
	}

	@Override
	public double getValue(boolean baseCase) {
		lastGeneratedValue = SpanishIPCUpdate.updateCost(super.getValue(baseCase), year, BasicConfigParams.STUDY_YEAR);
		return lastGeneratedValue;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
}