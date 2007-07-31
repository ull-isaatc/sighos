/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;

/**
 * A listener to compute the amount of started and finished elements.
 * 
 * @author Iván Castilla Rodríguez
 * @author Roberto Muñoz
 */
public class ElementStartFinishListener extends PeriodicListener {
	/** The number of elements started by period. */
	private int elemStarted[];
	/** The number of elements finished by period. */
	private int elemFinish[];
	/** The identifier of the first element created. */
	protected int firstElementId;
	/** The identifier of the last element created. */
	protected int lastElementId;

	public ElementStartFinishListener() {
		super();
	}

	public ElementStartFinishListener(double period) {
		super(period);
	}

	/**
	 * Returns the identifier of the first element created.
	 * @return The identifier of the first element created.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	/**
	 * Returns the identifier of the last element created.
	 * @return The identifier of the last element created.
	 */
	public int getLastElementId() {
		return lastElementId;
	}

	/**
	 * @return the elemFinish
	 */
	public int[] getElemFinish() {
		return elemFinish;
	}

	/**
	 * @return the elemStarted
	 */
	public int[] getElemStarted() {
		return elemStarted;
	}

	@Override
	protected void changeCurrentPeriod(double ts) {
	}

	@Override
	protected void initializeStorages() {
		elemStarted = new int[nPeriods];
		elemFinish = new int[nPeriods];
	}	
	
	@Override
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);

		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			switch (eInfo.getType()) {
				case START:
					elemStarted[currentPeriod]++;
					break;
				case FINISH:
					elemFinish[currentPeriod]++;
					break;
			}				
		}
	}

	@Override
	public void infoEmited(SimulationStartInfo info) {
		super.infoEmited(info);
		firstElementId = info.getFirstElementId();
	}
	
	public void infoEmited(SimulationEndInfo info) {
		lastElementId = info.getLastElementId();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("First Element Id:\t" + firstElementId + "\nLast Element Id:\t" + lastElementId);
		str.append("\nElements Started (PERIOD: " + period + ")\n");
		for (int i = 0; i < elemStarted.length; i++)
			str.append("\t" + elemStarted[i]);
		
		str.append("\nElements Finished (PERIOD: " + period + ")\n");
		for (int i = 0; i < elemFinish.length; i++)
			str.append("\t" + elemFinish[i]);
		str.append("\n");
		
		return str.toString();
	}
	
}
