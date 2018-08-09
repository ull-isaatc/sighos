package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.retal.EyeState;

/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public final class EyeStateAndValue {
	private final EyeState state;
	private final long value;

	public EyeStateAndValue(EyeState state, long value) {
		this.state = state;
		this.value = value;
	}

	/**
	 * @return the state
	 */
	public EyeState getState() {
		return state;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}
}