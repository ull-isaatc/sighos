/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

import es.ull.isaatc.simulation.inforeceiver.View;

/**
 * Estructura para almacenar un conjunto de vistas asociadas a una simulaci�n.
 * S�lo est� creada para facilitar el acceso conjunto a estas vistas.
 * @author Iv�n Castilla Rodr�guez
 */
public final class GSViewArray {
	private final GSElementTypeTimeView timeList;
	private final GSElementTypeWaitView waitList;
	private final GSResourceStdUsageView resList;
	private final int index;
	
	/**
	 * @param timeList
	 * @param waitList
	 * @param resList
	 */
	public GSViewArray(int index, GSElementTypeTimeView timeList,
			GSElementTypeWaitView waitList,
			GSResourceStdUsageView resList) {
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
	public GSElementTypeTimeView getTimeListener() {
		return timeList;
	}

	/**
	 * @return the waitList
	 */
	public GSElementTypeWaitView getWaitListener() {
		return waitList;
	}

	/**
	 * @return the resList
	 */
	public GSResourceStdUsageView getResListener() {
		return resList;
	}

	public View [] getListeners() {
		return new View[] {timeList, waitList, resList};
	}
}
