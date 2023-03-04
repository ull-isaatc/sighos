/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.Locale;

/**
 * @author Iván Castilla
 *
 */
public class TransshipmentOrder {
	public enum OperationType {
		LOAD,
		UNLOAD
	}
	private final OperationType opType;
	private final WaresType wares;
	private final double tones;

	/**
	 * 
	 */
	public TransshipmentOrder(OperationType opType, WaresType wares, double tones) {
		this.opType = opType;
		this.wares = wares;
		this.tones = tones;
	}

	/**
	 * @return the type
	 */
	public OperationType getOpType() {
		return opType;
	}

	/**
	 * @return the wares
	 */
	public WaresType getWares() {
		return wares;
	}

	/**
	 * @return the tones
	 */
	public double getTones() {
		return tones;
	}

	@Override
	public String toString() {
		return opType + "(" + wares.getDescription() + ", " + String.format(Locale.US, "%.2f", tones) + ")";
	}
}
