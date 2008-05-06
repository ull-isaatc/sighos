/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import es.ull.isaatc.simulation.listener.EventListener;

/**
 * @author Iván
 *
 */
public final class GSListenerArray {
	private final GSElementTypeTimeListener timeList;
	private final GSElementTypeWaitListener waitList;
	private final GSResourceStdUsageListener resList;
	private final int index;
	
	/**
	 * @param timeList
	 * @param waitList
	 * @param resList
	 */
	public GSListenerArray(int index, GSElementTypeTimeListener timeList,
			GSElementTypeWaitListener waitList,
			GSResourceStdUsageListener resList) {
		this.index = index;
		this.timeList = timeList;
		this.waitList = waitList;
		this.resList = resList;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the timeList
	 */
	public GSElementTypeTimeListener getTimeListener() {
		return timeList;
	}

	/**
	 * @return the waitList
	 */
	public GSElementTypeWaitListener getWaitListener() {
		return waitList;
	}

	/**
	 * @return the resList
	 */
	public GSResourceStdUsageListener getResListener() {
		return resList;
	}

	public EventListener [] getListeners() {
		return new EventListener[] {timeList, waitList, resList};
	}
}
