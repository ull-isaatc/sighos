package es.ull.isaatc.simulation.listener;

import java.util.TreeMap;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;

/**
 * Periodically stores info about activity queues and activities performed.
 * 
 * @author Ivan Castilla
 * @author Roberto Mu�oz
 */
public class ActivityListener extends PeriodicListener {

	/** The size of the activity queues by period. */
	private TreeMap<Integer, int[]> actQueues = new TreeMap<Integer, int[]>();

	/** The number of activities performed by period. */
	private TreeMap<Integer, int[]> actPerformed = new TreeMap<Integer, int[]>();


	public ActivityListener() {
		super();
	}

	public ActivityListener(double period) {
		super(period);
	}

	/**
	 * Returns the activities performed by period.
	 * @return the actPerformed
	 */
	public TreeMap<Integer, int[]> getActPerformed() {
		return actPerformed;
	}

	/**
	 * Returns the activity queues by period.
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
		for (int i : simul.getActivityList().keySet()) {
			actQueues.put(i, new int[nPeriods]);
			actPerformed.put(i, new int[nPeriods]);
		}
	}	

	@Override
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);

		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
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

	public void infoEmited(SimulationEndInfo info) {
		for (int[] queue : actQueues.values()) {
			for (int cont = currentPeriod + 1; cont < queue.length; cont++)
				queue[cont] = queue[cont - 1];
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		TreeMap<Integer, Activity> actList = simul.getActivityList();
		
		str.append("\nActivity Queues (PERIOD: " + period + ")\n");
		for (int i : actList.keySet()) {
			str.append("A" + actList.get(i).getIdentifier() + ":");
			for (int value : actQueues.get(Integer.valueOf(actList.get(i).getIdentifier())))
				str.append("\t" + value);
			str.append("\n");
		}
		
		str.append("Activities Performed (PERIOD: " + period + ")\n");
		for (int i : actList.keySet()) {
			str.append("A" + actList.get(i).getIdentifier() + ":");
			for (int value : actPerformed.get(Integer.valueOf(actList.get(i)
					.getIdentifier())))
				str.append("\t" + value);
			str.append("\n");
		}
		return str.toString();
	}
}