/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;

/**
 * @author Iván Castilla
 *
 */
public class StowagePlan {
	private final ArrayList<Integer>[] plan;
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public StowagePlan(int nCranes) {
		plan = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
	}

	public void addAll(int craneId, int[] containers) {
		plan[craneId] = new ArrayList<Integer>();
		for (int c : containers)			
			plan[craneId].add(c);
	}
	
	public ArrayList<Integer> get(int craneId) {
		return plan[craneId];
	}
}
