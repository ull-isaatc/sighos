package es.ull.isaatc.simulation.info;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Periodically stores the average waiting time of elements per activity
 * 
 * @author Roberto Muñoz
 */
public class ElementIndispTimeListener extends PeriodicListener {
	/** The next period timestamp */
	private double nextPeriodTs;

	/** Hashmap that contains the element info */
	private HashMap<Integer, ElementInfoValue> elemHashMap = new HashMap<Integer, ElementInfoValue>();

	private HashMap<Integer, ElementTypeTime> elementTypeTimes = new HashMap<Integer, ElementTypeTime>();

	
	/**
	 * Returns the element hashmap.
	 * 
	 * @return the element hashmap.
	 */
	public HashMap<Integer, ElementInfoValue> getElemHashMap() {
		return elemHashMap;
	}

	@Override
	protected void changeCurrentPeriod(double ts) {
		// perform the analisys of the values for the finished period
		Iterator<Entry<Integer, ElementInfoValue>> eInfoValueIt = elemHashMap.entrySet().iterator();
		while (eInfoValueIt.hasNext()) {
			ElementInfoValue eInfoValue = eInfoValueIt.next().getValue();
			double eStartTs;
			double eEndTs;
			// ignore the time before this period started
			if (eInfoValue.getStartPeriod() < currentPeriod - 1) 
				eStartTs = nextPeriodTs - period;
			else
				// the element started in the current period
				eStartTs = eInfoValue.getStartTs();
			if (eInfoValue.hasFinished()) {
				eEndTs = eInfoValue.getEndTs();
				eInfoValueIt.remove();
			} else
				eEndTs = nextPeriodTs;
			elementTypeTimes.get(eInfoValue.getTypeId()).addElement(currentPeriod - 1, eEndTs - eStartTs);
		}
		nextPeriodTs += period;
	}

	@Override
	protected void initializeStorages() {
		for (ElementType et : simul.getElementTypeList().values()) {
			elementTypeTimes.put(et.getIdentifier(), new ElementTypeTime(et.getIdentifier(), nPeriods));
		}
		nextPeriodTs = simStart + period;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		currentPeriod++;
		changeCurrentPeriod(getSimEnd());
		for (ElementTypeTime etTime : elementTypeTimes.values())
			etTime.finishSimulation();

		System.out.println(toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			if (eInfo.getType() == ElementInfo.Type.START) {
				ElementInfoValue eInfoValue = new ElementInfoValue(eInfo
						.getIdentifier(), currentPeriod, eInfo.getValue(),eInfo.getTs());
				elemHashMap.put(eInfo.getIdentifier(), eInfoValue);
				elementTypeTimes.get(eInfoValue.getTypeId()).startElement(eInfoValue);
			} else if (eInfo.getType() == ElementInfo.Type.FINISH) {
				if (eInfo.getValue() == 0) {
					ElementInfoValue eInfoValue = elemHashMap.get(eInfo.getIdentifier());
					eInfoValue.setEndTs(eInfo.getTs());
				}
			}
		}
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\nElement indisposed time (PERIOD: " + period + ")\n");
		for (ElementTypeTime etTime : elementTypeTimes.values())
			str.append(etTime.toString());
		return str.toString();
	}

	public class ElementInfoValue {
		int id;

		int startPeriod;

		double startTs;

		double endTs;

		int typeId;

		/**
		 * @param id
		 * @param startTs
		 */
		public ElementInfoValue(int id, int startPeriod, int typeId,
				double starTs) {
			super();
			this.id = id;
			this.startPeriod = startPeriod;
			this.typeId = typeId;
			this.startTs = starTs;
			this.endTs = Double.NaN;
		}

		/**
		 * @return the typeId
		 */
		public int getTypeId() {
			return typeId;
		}

		/**
		 * @return the startPeriod
		 */
		public int getStartPeriod() {
			return startPeriod;
		}

		/**
		 * @return the startTs
		 */
		public double getStartTs() {
			return startTs;
		}

		/**
		 * @param endTs
		 *            the endTs to set
		 */
		public void setEndTs(double endTs) {
			this.endTs = endTs;
		}

		/**
		 * @return the endTs
		 */
		public double getEndTs() {
			return endTs;
		}

		/**
		 * @return true if the element has finished, false elsewhere
		 */
		public boolean hasFinished() {
			if (!Double.isNaN(endTs))
				return true;
			return false;
		}

		public String toString() {
			return "E[" + id + "/" + typeId + "]\tS: " + startTs + "\tE: " + endTs;
		}
	}

	private class ElementTypeTime {

		/** the element type identifier */
		int typeId;

		/** the number of periods */
		int nPeriods;

		/** array that contains the indisposed time for the elements of this type */
		private double indispTime[];

		/** number of elements created by period */
		int createdElement[];

		/** number of active elements by period */
		int activedElement[];

		public ElementTypeTime(int typeId, int nPeriods) {
			this.typeId = typeId;
			this.nPeriods = nPeriods;
			createdElement = new int[nPeriods];
			activedElement = new int[nPeriods];
			indispTime = new double[nPeriods];
		}

		public void startElement(ElementInfoValue eInfoValue) {
			createdElement[eInfoValue.getStartPeriod()]++;
		}

		/**
		 * Adds an element to the list of a period
		 * 
		 * @param time
		 */
		public void addElement(int period, double time) {
			indispTime[period] += time;
			activedElement[period]++;
		}

		/**
		 * Calculate the average indisposed time by period
		 */
		public void finishSimulation() {
			for (int i = 0; i < nPeriods; i++)
				indispTime[i] = indispTime[i] / (double) activedElement[i];
		}

		public String toString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId + "\n");
			str.append("CREATED");
			for (int value : createdElement)
				str.append("\t" + value);
			str.append("\nINDISPOSED TIME");
			for (double value : indispTime)
				str.append("\t" + value);
			str.append("\n");
			return str.toString();
		}
	}
}
