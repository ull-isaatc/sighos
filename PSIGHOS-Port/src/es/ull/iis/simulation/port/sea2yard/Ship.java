/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author Iván Castilla
 *
 */
public class Ship {
	/** Bays of the ship */
	final private ArrayList<Integer>[] bays;
	final private TreeMap<Integer, Integer> containerBay;
	private int maxDeep;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Ship(int nBays) {
		bays = (ArrayList<Integer>[]) new ArrayList<?>[nBays];
		containerBay = new TreeMap<Integer, Integer>();
		for (int i = 0; i < nBays; i++)
			bays[i] = new ArrayList<Integer>();
		maxDeep = 0;
	}

	/**
	 * Allocates a container to the specified bay
	 * @param bayId
	 * @param containerId
	 * @return The position in the bay of the allocated container 
	 */
	public int push(int bayId, int containerId) {
		containerBay.put(containerId, bayId);
		bays[bayId].add(containerId);
		maxDeep = Math.max(maxDeep, bays[bayId].size());
		return bays[bayId].size();
	}
	
	/**
	 * Returns the container on top of the specified bay
	 * @param bayId Specified bay
	 * @return the container identifier on top of the specified bay
	 */
	public int peek(int bayId) {
		return bays[bayId].get(bays[bayId].size()-1);
	}
	
	/**
	 * Returns and removes the container on top of the specified bay
	 * @param bayId Specified bay
	 * @return the container identifier on top of the specified bay
	 */	
	public int pop(int bayId) {
		return bays[bayId].remove(bays[bayId].size()-1);
	}
	
	/**
	 * Returns the number of bays of the ship
	 * @return the number of bays of the ship
	 */
	public int getNBays() {
		return bays.length;
	}
	
	/**
	 * Returns the containers at a specified bay 
	 * @param bayId Specified bay
	 * @return the containers at a specified bay
	 */
	public ArrayList<Integer> getBay(int bayId) {
		return bays[bayId];
	}
	
	/**
	 * Returns the bay where this container is stored
	 * @param containerId Specified container
	 * @return the bay where this container is stored
	 */
	public int getContainerBay(int containerId) {
		return containerBay.get(containerId);
	}
	private String printContainerId(int contId) {
		if (contId < 10)
			return "    " + contId + "   ";
		else if (contId < 100)
			return "   " + contId + "   ";
		else if (contId < 1000)
			return "  " + contId + "   ";
		else
			return "" + contId;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = maxDeep - 1; i >= 0; i--) {
			for (int bayId = 0; bayId < bays.length; bayId++) {
				if (bays[bayId].size() > i)
					str.append(printContainerId(bays[bayId].get(i)));
				else
					str.append("        ");
			}
			str.append("\n");
		}
		for (int bayId = 0; bayId < bays.length; bayId++) {
			str.append("--------");
		}
		str.append("\n");
		for (int bayId = 0; bayId < bays.length; bayId++) {
			str.append(printContainerId(bayId));
		}
		return str.toString();
	}
}
