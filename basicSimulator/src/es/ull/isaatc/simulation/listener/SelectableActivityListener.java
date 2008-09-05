package es.ull.isaatc.simulation.listener;

import java.util.Set;
import java.util.TreeMap;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;

/**
 * Periodically stores info about activity queues and activities performed.
 * 
 * @author Yurena Garc’a-Hevia
 */
public class SelectableActivityListener extends PeriodicListener {

	/** The size of the activity queues by period. */
	private TreeMap<Integer, int[]> actQueues = new TreeMap<Integer, int[]>();

	/** The number of activities performed by period. */
	private TreeMap<Integer, int[]> actPerformed = new TreeMap<Integer, int[]>();

	public SelectableActivityListener() {
		super();
	}

	public SelectableActivityListener(double period) {
		super(period);
	}

	/**
	 * Returns the activities performed by period.
	 * 
	 * @return the actPerformed
	 */
	public TreeMap<Integer, int[]> getActPerformed() {
		return actPerformed;
	}

	/**
	 * Returns the activity queues by period.
	 * 
	 * @return the actQueues
	 */
	public TreeMap<Integer, int[]> getActQueues() {
		return actQueues;
	}

	@Override
	protected void changeCurrentPeriod(double ts) {
		for (int[] queue : actQueues.values())
			queue[currentPeriod] = queue[currentPeriod - 1];
	}

	@Override
	protected void initializeStorages() {

		// if there is no selected activity all the activities are included to
		// be listened
		if (actQueues.keySet().isEmpty())
			for (int id : simul.getActivityList().keySet()) {
				actQueues.put(id, new int[nPeriods]);
				actPerformed.put(id, new int[nPeriods]);
			}
		// else only the selected one will be listened
		else
			for (int id : actQueues.keySet()) {
				actQueues.put(id, new int[nPeriods]);
				actPerformed.put(id, new int[nPeriods]);
			}

	}

	/*
	 * Include an activity to be listened
	 */
	public void listenAct(int act) {
		actQueues.put(act, null);
	}

	@Override
	public void infoEmited(SimulationObjectInfo info) {

		super.infoEmited(info);

		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			// if the activity should be listened
			if (!eInfo.getType().equals(ElementInfo.Type.START)
					&& !eInfo.getType().equals(ElementInfo.Type.FINISH)
					&& (actQueues.keySet().contains(eInfo.getValue()))) {
				switch (eInfo.getType()) {
				case REQACT:
					actQueues.get(eInfo.getValue())[currentPeriod]++;
					break;
				case STAACT:
					actQueues.get(eInfo.getValue())[currentPeriod]--;
					break;
				case INTACT:
					actQueues.get(eInfo.getValue())[currentPeriod]++;
					break;
				case RESACT:
					actQueues.get(eInfo.getValue())[currentPeriod]--;
					break;
				case ENDACT:
					actPerformed.get(eInfo.getValue())[currentPeriod]++;
					break;
				}
			}
		}
	}

	public void infoEmited(SimulationEndInfo info) {
		for (int[] queue : actQueues.values()) {
			for (int cont = currentPeriod + 1; cont < queue.length; cont++)
				queue[cont] = queue[cont - 1];
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		Set<Integer> actList = actQueues.keySet();

		str.append("\nActivity Queues (PERIOD: " + period + ")\n");
		for (int i : actList) {
			str.append("A" + i + ":");
			for (int value : actQueues.get(Integer.valueOf(i)))
				str.append("\t" + value);
			str.append("\n");
		}

		str.append("Activities Performed (PERIOD: " + period + ")\n");
		for (int i : actList) {
			str.append("A" + i + ":");
			for (int value : actPerformed.get(Integer.valueOf(i)))
				str.append("\t" + value);
			str.append("\n");
		}
		return str.toString();
	}
}
