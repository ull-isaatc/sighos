/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class YardCraneResourceType extends ResourceType {
	private final int block;

	/**
	 * @param id
	 * @param model
	 * @param description
	 */
	public YardCraneResourceType(int id, Simulation model, int block) {
		super(model, PortModel.YARD_CRANE + " " + id);
		this.block = block;
	}

	/**
	 * @return the block
	 */
	public int getBlock() {
		return block;
	}

}
