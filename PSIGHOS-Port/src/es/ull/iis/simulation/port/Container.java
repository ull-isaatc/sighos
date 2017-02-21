/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class Container extends Element {
	private final int berth;
	private final int block;

	public Container(Model model, ElementType et, InitializerFlow flow, int berth, int block) {
		super(model, et, flow);
		this.berth = berth;
		this.block = block;
	}

	/**
	 * @return the berth
	 */
	public int getBerth() {
		return berth;
	}

	/**
	 * @return the block
	 */
	public int getBlock() {
		return block;
	}
}
