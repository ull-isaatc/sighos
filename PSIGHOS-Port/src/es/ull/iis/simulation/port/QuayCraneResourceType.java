/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Model;

/**
 * @author Iván Castilla
 *
 */
public class QuayCraneResourceType extends ResourceType {
	private final int berth;
	
	/**
	 * @param id
	 * @param model
	 * @param description
	 */
	public QuayCraneResourceType(int id, Model model, int berth) {
		super(model, PortModel.QUAY_CRANE + " " + id);
		this.berth = berth;
	}

	/**
	 * @return the berth
	 */
	public int getBerth() {
		return berth;
	}

}
