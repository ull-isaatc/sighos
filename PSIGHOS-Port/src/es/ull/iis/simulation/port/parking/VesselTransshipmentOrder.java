/**
 * 
 */
package es.ull.iis.simulation.port.parking;

/**
 * @author Iván Castilla
 *
 */
public class VesselTransshipmentOrder extends TransshipmentOrder {
	private double pendingTones;
	private double notAssignedTones;

	/**
	 * 
	 */
	public VesselTransshipmentOrder(OperationType opType, WaresType wares, double tones) {
		super(opType, wares, tones);
		this.pendingTones = tones;
		this.notAssignedTones = tones;
	}

	public VesselTransshipmentOrder getTransshipmentOrderForTruck(Truck truck) {
		final double actual = (truck.getMaxLoad() > notAssignedTones) ? notAssignedTones : truck.getMaxLoad();
		notAssignedTones -= actual;
		return (actual > PortParkingModel.MIN_LOAD) ? new VesselTransshipmentOrder(getOpType(), getWares(), actual) : null;
	}
	
	public boolean performTransshipmentOrder(TransshipmentOrder order) {
		if (order.getOpType().equals(getOpType()) && order.getWares().equals(getWares())) {
			pendingTones -= order.getTones();
			return (pendingTones >= 0);
		}
		return false;
	}
}
