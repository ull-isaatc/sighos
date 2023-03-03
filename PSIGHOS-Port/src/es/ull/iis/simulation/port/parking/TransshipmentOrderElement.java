/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;

/**
 * @author Iván Castilla
 *
 */
public class TransshipmentOrderElement extends Element {
	private final TransshipmentOrder order;
	private double pendingTones;
	private double notAssignedTones;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 * @param type
	 * @param wares
	 * @param tones
	 * @param pendingTones
	 * @param notAssignedTones
	 */
	public TransshipmentOrderElement(PortParkingModel simul, ElementType elementType, InitializerFlow initialFlow, TransshipmentOrder order) {
		super(simul, elementType, initialFlow);
		this.order = order;
		this.pendingTones = order.getTones();
		this.notAssignedTones = order.getTones();
	}

	/**
	 * @return the type
	 */
	public OperationType getOpType() {
		return order.getOpType();
	}

	/**
	 * @return the wares
	 */
	public WaresType getWares() {
		return order.getWares();
	}

	/**
	 * @return the tones
	 */
	public double getTones() {
		return order.getTones();
	}

	/**
	 * @return the pendingTones
	 */
	public double getPendingTones() {
		return pendingTones;
	}

	/**
	 * @return the notAssignedTones
	 */
	public double getNotAssignedTones() {
		return notAssignedTones;
	}

}
