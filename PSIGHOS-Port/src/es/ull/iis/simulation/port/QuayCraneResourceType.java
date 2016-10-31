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
public class QuayCraneResourceType extends ResourceType {
	private final int berth;
	
	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public QuayCraneResourceType(int id, Simulation simul, int berth) {
		super(simul, PortSimulation.QUAY_CRANE + " " + id);
		this.berth = berth;
	}

	/**
	 * @return the berth
	 */
	public int getBerth() {
		return berth;
	}

}
