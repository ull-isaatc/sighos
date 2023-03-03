/**
 * 
 */
package es.ull.iis.simulation.port.parking;

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

}
