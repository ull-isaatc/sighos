/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * A vessel that carries containers to be unloaded at the port. The vessel is divided into bays, and
 * each bay contains several containers piled in a specified order. The bottom containers in a bay cannot be 
 * unloaded until the corresponding top containers have been unloaded.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class Vessel implements Serializable {
	/** Constant for serializing this class */
	private static final long serialVersionUID = 76987236887987432L;
	/** Content of the bays of the ship */
	final private ArrayList<Integer>[] bays;
	/** The bay where a specified container is */
	final private TreeMap<Integer, Integer> bayPosition;
	/** A precomputed time that represents how long does it take to unload each container */
	final private TreeMap<Integer, Long> processingTime;
	/** The time unit for processing times */
	final private TimeUnit unit;
	/** The maximum number of containers in a bay */
	private int maxDeep;
	
	/**
	 * Creates a vessel
	 * @param nBays Number of bays that divide the vessel
	 * @param unit Time unit to express the duration of processes in the vessel
	 */
	@SuppressWarnings("unchecked")
	public Vessel(int nBays, TimeUnit unit) {
		bays = (ArrayList<Integer>[]) new ArrayList<?>[nBays];
		bayPosition = new TreeMap<Integer, Integer>();
		processingTime = new TreeMap<Integer, Long>();
		for (int i = 0; i < nBays; i++)
			bays[i] = new ArrayList<Integer>();
		maxDeep = 0;
		this.unit = unit;
	}

	/**
	 * Allocates a container to the specified bay
	 * @param containerId Container identifier
	 * @param bayId Bay identifier
	 * @param procTime Precomputed duration of the container's unloading process  
	 * @return The position in the bay of the allocated container 
	 */
	public int add(int containerId, int bayId, long procTime) {
		bayPosition.put(containerId, bayId);
		processingTime.put(containerId, procTime);
		bays[bayId].add(0, containerId);
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
		return bayPosition.get(containerId);
	}
	
	/**
	 * Returns the time spent in unloading a specified container
	 * @param containerId Specified container
	 * @return the time spent in unloading a specified container
	 */
	public long getContainerProcessingTime(int containerId) {
		return processingTime.get(containerId);
	}

	/**
	 * Returns the total amount of containers in this vessel
	 * @return the total amount of containers in this vessel
	 */
	public int getNContainers() {
		return bayPosition.size();
	}
	
	/**
	 * Returns the time unit used to express the duration of processes in the vessel
	 * @return the time unit used to express the duration of processes in the vessel
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * A convenient method to print a container. Creates a string that adds blanks before and after the container identifier
	 * to properly align the container. Works fine up to 9999 containers. 
	 * @param contId Container identifier
	 * @return An aligned string representing the container.
	 */
	private String printContainerId(int contId) {
		if (contId < 10)
			return "    " + contId + "   ";
		else if (contId < 100)
			return "   " + contId + "   ";
		else if (contId < 1000)
			return "  " + contId + "   ";
		else if (contId < 10000)
			return "  " + contId + "  ";
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
