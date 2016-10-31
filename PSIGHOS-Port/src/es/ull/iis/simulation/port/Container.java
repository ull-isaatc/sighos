/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.sequential.Element;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class Container extends Element {
	private final int berth;
	private final int block;

	public Container(Simulation simul, ElementType et, InitializerFlow flow, int berth, int block) {
		super(simul, et, flow);
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

	@Override
	protected void addEvent(DiscreteEvent e) {
		super.addEvent(e);
	}
}
