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
	private final int lastTask;

	/**
	 * @param model
	 * @param elementType
	 * @param initialFlow
	 */
	public QuayCrane(Simulation model, ElementType elementType, InitializerFlow initialFlow, int initPosition, int lastTask) {
		super(model, elementType, initialFlow);
		this.position = initPosition;
		this.lastTask = lastTask;
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

	/**
	 * @return the lastTask
	 */
	public int getLastTask() {
		return lastTask;
	}

}
