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
public class ElementTypeTimeListener extends PeriodicListener {
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

		System.out.println(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);
		if (info.getTs() - (currentPeriod * period) >= referenceTs) {
			return;
		}
			
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			ElementInfoValue eInfoValue;
			switch (eInfo.getType()) {
				case START:
					eInfoValue = new ElementInfoValue(eInfo.getIdentifier(),
							currentPeriod, eInfo.getValue(),eInfo.getTs());
					elemHashMap.put(eInfo.getIdentifier(), eInfoValue);
					elementTypeTimes.get(eInfoValue.getTypeId()).startElement(eInfoValue);
					break;
				case FINISH:
					if (eInfo.getValue() == 0) {
						eInfoValue = elemHashMap.get(eInfo.getIdentifier());
						eInfoValue.setEndTs(currentPeriod, eInfo.getTs());
						elementTypeTimes.get(eInfoValue.getTypeId()).finishElement(eInfoValue);
					}
					break;
				case STAACT:
					eInfoValue = elemHashMap.get(eInfo.getIdentifier());
					eInfoValue.startActivity(eInfo.getTs());
					break;
				case ENDACT:
					eInfoValue = elemHashMap.get(eInfo.getIdentifier());
					eInfoValue.endActivity(eInfo.getTs());
					elementTypeTimes.get(eInfoValue.getTypeId()).finishActivity(eInfoValue);
					break;
				case RESACT:
					eInfoValue = elemHashMap.get(eInfo.getIdentifier());
					eInfoValue.startActivity(eInfo.getTs());
					break;
				case INTACT:
					eInfoValue = elemHashMap.get(eInfo.getIdentifier());
					eInfoValue.endActivity(eInfo.getTs());
					elementTypeTimes.get(eInfoValue.getTypeId()).finishActivity(eInfoValue);
					break;
			}
		}
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\nElement created (PERIOD: " + period + ")\n");
		for (ElementTypeTime etTime : elementTypeTimes.values())
			str.append(etTime.getCreatedString());
		str.append("Element finished (PERIOD: " + period + ")\n");
		for (ElementTypeTime etTime : elementTypeTimes.values())
			str.append(etTime.getFinishedString());
		str.append("Element work time (PERIOD: " + period + ")\n");
		for (ElementTypeTime etTime : elementTypeTimes.values())
			str.append(etTime.getWorkingString());
		return str.toString();
	}

	public class ElementInfoValue {
		int id;

		int startPeriod;
		
		int finishPeriod;

		double startTs;

		double endTs;
		
		double currActStartTs;

		double workTime = 0;

		int typeId;

		/**
		 * @param id
		 * @param startTs
		 */
		public ElementInfoValue(int id, int startPeriod, int typeId, double startTs) {
			super();
			this.id = id;
			this.startPeriod = startPeriod;
			this.typeId = typeId;
			this.startTs = startTs;
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
		 * @return the finishPeriod
		 */
		public int getFinishPeriod() {
			return finishPeriod;
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
		public void setEndTs(int period, double endTs) {
			this.finishPeriod = period;
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
		
		public void startActivity(double ts) {
			currActStartTs = ts;
		}
		
		public void endActivity(double ts) {
			workTime = ts - currActStartTs;
		}

		/**
		 * @return the workTime
		 */
		public double getWorkTime() {
			return workTime;
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

		/** array that contains the working time for the elements of this type */
		private double workTime[];

		/** number of elements created by period */
		int createdElement[];

		/** number of elements finished by period */
		int finishedElement[];

		/** number of active elements by period */
		int activedElement[];

		public ElementTypeTime(int typeId, int nPeriods) {
			this.typeId = typeId;
			this.nPeriods = nPeriods;
			createdElement = new int[nPeriods];
			finishedElement = new int[nPeriods];
			activedElement = new int[nPeriods];
			workTime = new double[nPeriods];
		}

		public void startElement(ElementInfoValue eInfoValue) {
			createdElement[eInfoValue.getStartPeriod()]++;
		}
		
		public void finishElement(ElementInfoValue eInfoValue) {
			finishedElement[eInfoValue.getFinishPeriod()]++;
		}

		public void finishActivity(ElementInfoValue eInfoValue) {
			workTime[eInfoValue.getStartPeriod()] += eInfoValue.getWorkTime();
		}

		/**
		 * Adds an element to the list of a period
		 * 
		 * @param time
		 */
		public void addElement(int period, double time) {
			activedElement[period]++;
		}

		/**
		 * Calculate the average indisposed time by period
		 */
		public void finishSimulation() {
//			for (int i = 0; i < nPeriods; i++)
//				workTime[i] = workTime[i] / (double) createdElement[i];
		}


		public String getCreatedString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			for (int value : createdElement)
				str.append("\t" + value);
			str.append("\n");			
			return str.toString();
		}

		public String getFinishedString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			for (int value : finishedElement)
				str.append("\t" + value);
			str.append("\n");			
			return str.toString();
		}
		
		public String getWorkingString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId);
			for (double value : workTime)
				str.append("\t" + value);
			str.append("\n");
			return str.toString();
		}
	}
}
