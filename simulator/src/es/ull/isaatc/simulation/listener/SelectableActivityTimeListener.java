package es.ull.isaatc.simulation.listener;

import java.util.Map;
import java.util.TreeMap;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;

/**
 * Periodically stores the time each activity has been performed.
 * 
 * @author Yurena Garc’a-Hevia
 */
public class SelectableActivityTimeListener extends PeriodicListener {
	/** The activity usage by period. */
	private TreeMap<Integer, double[]> actUsage = new TreeMap<Integer, double[]>();

	/** The amount of elements performing activities by period. */
	private TreeMap<Integer, Integer> actCounter = new TreeMap<Integer, Integer>();

	public SelectableActivityTimeListener() {
		super();
	}

	public SelectableActivityTimeListener(double period) {
		super(period);
	}

	/**
	 * Returns the activity usage by period.
	 * 
	 * @return The activity usage by period.
	 */
	public TreeMap<Integer, double[]> getActUsage() {
		return actUsage;
	}

	@Override
	protected void changeCurrentPeriod(double ts) {
		for (int id : actUsage.keySet()) {
			// The activities are treated as if they were finishing at the end
			// of the previous period
			actUsage.get(id)[currentPeriod - 1] += currentPeriod * period
					* actCounter.get(id);
			// The activities are treated as if they were starting at the
			// beggining of the current period
			if (currentPeriod < nPeriods)
				actUsage.get(id)[currentPeriod] -= currentPeriod * period
						* actCounter.get(id);
		}
	}

	@Override
	protected void initializeStorages() {

		// if there is no selected activity all the activities are included to
		// be listened
		if (actUsage.keySet().isEmpty())
			for (int id : simul.getActivityList().keySet()) {
				actUsage.put(id, new double[nPeriods]);
				actCounter.put(id, 0);
			}
		// else only the selected one will be listened
		else
			for (int id : actUsage.keySet()) {
				actUsage.put(id, new double[nPeriods]);
				actCounter.put(id, 0);
			}
	}

	/*
	 * Include an activity to be listened
	 */
	public void listenAct(int act) {
		actUsage.put(act, null);
	}

	@Override
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);

		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			if (!eInfo.getType().equals(ElementInfo.Type.START)
					&& !eInfo.getType().equals(ElementInfo.Type.FINISH)
					&& (actUsage.keySet().contains(eInfo.getValue()))) {
				switch (eInfo.getType()) {
				case STAACT:
					// There's no control over unbalanced starts and ends
					actUsage.get(eInfo.getValue())[currentPeriod] -= eInfo
							.getTs();
					actCounter.put(eInfo.getValue(), actCounter.get(eInfo
							.getValue()) + 1);
					break;
				case INTACT:
					// There's no control over unbalanced starts and ends
					actUsage.get(eInfo.getValue())[currentPeriod] += eInfo
							.getTs();
					actCounter.put(eInfo.getValue(), actCounter.get(eInfo
							.getValue()) - 1);
					break;
				case RESACT:
					// There's no control over unbalanced starts and ends
					actUsage.get(eInfo.getValue())[currentPeriod] -= eInfo
							.getTs();
					actCounter.put(eInfo.getValue(), actCounter.get(eInfo
							.getValue()) + 1);
					break;
				case ENDACT:
					// There's no control over unbalanced starts and ends
					actUsage.get(eInfo.getValue())[currentPeriod] += eInfo
							.getTs();
					actCounter.put(eInfo.getValue(), actCounter.get(eInfo
							.getValue()) - 1);
					break;
				}
			}
		}
	}

	public void infoEmited(SimulationEndInfo info) {
		for (int id : actUsage.keySet())
			// The activities are treated as if they were finishing at the end of the previous period
			actUsage.get(id)[currentPeriod] += info.getSimulation().getEndTs() * actCounter.get(id);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("Activity Usage(PERIOD: "
				+ period + ")\n");
		for (Map.Entry<Integer, double[]> values : actUsage.entrySet()) {
			str.append("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				str.append("\t" + values.getValue()[j]);
			str.append("\r\n");
		}
		return str.toString();
	}
}
