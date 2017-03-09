/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;

/**
 * @author Iv�n Castilla
 *
 */
public class StowagePlan {
	private final ArrayList<Integer>[] plan;
	private final int[] initPosition;
	private final Ship ship;
	private int nContainers;
	private final int nCranes;
	private final int safetyDistance;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public StowagePlan(Ship ship, int nCranes, int safetyDistance) {
		plan = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
		initPosition = new int[nCranes];
		this.ship = ship;
		this.nCranes = nCranes;
		this.safetyDistance = safetyDistance;
		nContainers = 0;
	}

	public void addAll(int craneId, int[] containers) {
		plan[craneId] = new ArrayList<Integer>();
		nContainers += containers.length;
		for (int c : containers)			
			plan[craneId].add(c);
	}
	
	public ArrayList<Integer> get(int craneId) {
		return plan[craneId];
	}
	
	public void setInitialPosition(int craneId, int initPos) {
		initPosition[craneId] = initPos;
	}
	
	public int getInitialPosition(int craneId) {
		return initPosition[craneId];
	}
	
	/**
	 * @return the nContainers
	 */
	public int getNContainers() {
		return nContainers;
	}

	/**
	 * @return the nCranes
	 */
	public int getNCranes() {
		return nCranes;
	}

	/**
	 * @return the safetyDistance
	 */
	public int getSafetyDistance() {
		return safetyDistance;
	}

	/**
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < plan.length; i++) {
			str.append("Crane " + i + " (INIT:" + initPosition[i] + "):");
			for (int containerId : plan[i]) {
				str.append("\t" + containerId);
			}
			str.append("\n");
		}
		return str.toString();
	}
}
