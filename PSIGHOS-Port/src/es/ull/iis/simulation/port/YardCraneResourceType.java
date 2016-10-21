/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.sequential.ResourceType;
import es.ull.iis.simulation.sequential.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class YardCraneResourceType extends ResourceType {
	private final int block;

	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public YardCraneResourceType(int id, Simulation simul, int block) {
		super(id, simul, PortSimulation.YARD_CRANE + " " + id);
		this.block = block;
	}

	/**
	 * @return the block
	 */
	public int getBlock() {
		return block;
	}

}
