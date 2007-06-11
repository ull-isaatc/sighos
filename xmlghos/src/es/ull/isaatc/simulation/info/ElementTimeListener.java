package es.ull.isaatc.simulation.info;

import java.util.HashMap;

import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Periodically stores the average waiting time of elements per activity
 * 
 * @author Roberto Muñoz
 */
public class ElementTimeListener extends PeriodicListener {
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

	}

	@Override
	protected void initializeStorages() {
		for (ElementType et : simul.getElementTypeList().values()) {
			elementTypeTimes.put(et.getIdentifier(), new ElementTypeTime(et.getIdentifier(), nPeriods));
		}
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
				ElementInfoValue eInfoValue = elemHashMap.get(eInfo.getIdentifier());
				eInfoValue.setEndTs(eInfo.getTs());
				if (eInfo.getValue() == 0) {
					eInfoValue.setPeriodBack(currentPeriod);
					elementTypeTimes.get(eInfoValue.getTypeId()).finishElement(eInfoValue);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		for (ElementTypeTime etTime : elementTypeTimes.values())
			etTime.finishSimulation();
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("PERIOD: " + period);
		for (int i = 1; i <= nPeriods; i++)
			str.append("\t" + i);
		str.append("\nElement time information per type\n");
		for (ElementTypeTime etTime : elementTypeTimes.values())
			str.append(etTime.toString());
		return str.toString();
	}

	public class ElementInfoValue {
		int id;

		int periodFwd;

		int periodBack;

		double starTs;

		double endTs;

		double resTime;

		int typeId;

		/**
		 * @param id
		 * @param startTs
		 */
		public ElementInfoValue(int id, int period, int typeId, double starTs) {
			super();
			this.id = id;
			this.periodFwd = period;
			this.typeId = typeId;
			this.starTs = starTs;
			this.endTs = -1;
		}

		/**
		 * @return the typeId
		 */
		public int getTypeId() {
			return typeId;
		}

		/**
		 * @param endTs
		 *            the endTs to set
		 */
		public void setEndTs(double endTs) {
			this.endTs = endTs;
			resTime = endTs - starTs;
		}

		/**
		 * @return the periodFwd
		 */
		public int getPeriodFwd() {
			return periodFwd;
		}

		/**
		 * @param periodBack
		 *            the periodBack to set
		 */
		public void setPeriodBack(int periodBack) {
			this.periodBack = periodBack;
		}

		/**
		 * @return the periodBack
		 */
		public int getPeriodBack() {
			return periodBack;
		}

		/**
		 * @return the resTime
		 */
		public double getResTime() {
			return resTime;
		}

		public String toString() {
			return "E[" + id + "/" + typeId + "]\tS: " + starTs + "\tE: "
					+ endTs + "\tRT: " + resTime + "\tFP: " + periodFwd
					+ "\tBP: " + periodBack;
		}
	}

	private class ElementTypeTime {

		/** element type identifier */
		int typeId;

		int nPeriods;

		/** Array that contains the response time for the elements of this type */
		private double resTimeFwd[];

		/** Array that contains the response time for the elements of this type */
		private double resTimeBack[];

		/** Number of elements created by period */
		int createdElement[];

		/** Number of elements finished that were created in the period */
		int finishedElementFwd[];

		/** Number of elements finished in each period */
		int finishedElementBack[];

		public ElementTypeTime(int typeId, int nPeriods) {
			this.typeId = typeId;
			this.nPeriods = nPeriods;
			createdElement = new int[nPeriods];
			finishedElementFwd = new int[nPeriods];
			finishedElementBack = new int[nPeriods];
			resTimeFwd = new double[nPeriods];
			resTimeBack = new double[nPeriods];
		}

		public void startElement(ElementInfoValue eInfoValue) {
			createdElement[eInfoValue.getPeriodFwd()]++;
		}

		public void finishElement(ElementInfoValue eInfoValue) {
			finishedElementFwd[eInfoValue.getPeriodFwd()]++;
			finishedElementBack[eInfoValue.getPeriodBack()]++;
			resTimeFwd[eInfoValue.getPeriodFwd()] += eInfoValue.getResTime();
			resTimeBack[eInfoValue.getPeriodBack()] += eInfoValue.getResTime();
		}

		public void finishSimulation() {
			for (int i = 0; i < nPeriods; i++) {
				resTimeFwd[i] = resTimeFwd[i] / (double) finishedElementFwd[i];
				resTimeBack[i] = resTimeBack[i]	/ (double) finishedElementBack[i];
			}
		}

		public String toString() {
			StringBuffer str = new StringBuffer();
			str.append("ET : " + typeId + "\n");
			str.append("CREATED");
			for (int value : createdElement)
				str.append("\t" + value);
			str.append("\nFINISHED FWD");
			for (int value : finishedElementFwd)
				str.append("\t" + value);
			str.append("\nFINISHED BACK");
			for (int value : finishedElementBack)
				str.append("\t" + value);
			str.append("\nRESPONSE TIME FWD");
			for (double value : resTimeFwd)
				str.append("\t" + value);
			str.append("\nRESPONSE TIME BACK");
			for (double value : resTimeBack)
				str.append("\t" + value);
			str.append("\n\n");
			return str.toString();
		}
	}
}
