/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class QuayCrane extends Element {
	/** Position of the crane, respecting to the bays of the ship */
	private int position;

	/**
	 * @param model
	 * @param elementType
	 * @param initialFlow
	 */
	public QuayCrane(Simulation model, ElementType elementType, InitializerFlow initialFlow, int initPosition) {
		super(model, elementType, initialFlow);
		this.position = initPosition;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

}
