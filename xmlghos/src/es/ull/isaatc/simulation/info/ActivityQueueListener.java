package es.ull.isaatc.simulation.info;

import java.util.TreeMap;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Periodically stores the size of the activity queues.
 * 
 * @author Ivan Castilla
 * @author Roberto Muñoz
 */
public class ActivityQueueListener extends PeriodicListener {

	/** The size of the activity queues by periodFwd. */
	private TreeMap<Integer, int[]> actQueues = new TreeMap<Integer, int[]>();

	/** The number of activities performed by periodFwd. */
	private TreeMap<Integer, int[]> actPerformed = new TreeMap<Integer, int[]>();

	/** The number of elements started by periodFwd. */
	private int elemStarted[];

	/** The number of elements finished by periodFwd. */
	private int elemFinish[];

	
	public ActivityQueueListener() {
		super();
	}

	public ActivityQueueListener(double period) {
		super(period);
	}

	/**
	 * Returns the size of the activity queues by periodFwd.
	 * 
	 * @return The size of the activity queues by periodFwd.
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
		elemStarted = new int[nPeriods];
		elemFinish = new int[nPeriods];
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
			switch (eInfo.getType()) {
				case START:
					elemStarted[currentPeriod]++;
					break;
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
				case FINISH:
					elemFinish[currentPeriod]++;
					break;
			}				
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		for (int[] queue : actQueues.values()) {
			for (int cont = currentPeriod + 1; cont < queue.length; cont++)
				queue[cont] = queue[cont - 1];
		}
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
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
		
		str.append("\nElements Started (PERIOD: " + period + ")\n");
		for (int i = 0; i < elemStarted.length; i++)
			str.append("\t" + elemStarted[i]);
		
		str.append("\nElements Finished (PERIOD: " + period + ")\n");
		for (int i = 0; i < elemFinish.length; i++)
			str.append("\t" + elemFinish[i]);
		str.append("\n");
		
		return str.toString();
	}
	
	public int getActivityQueue(int id) {
		return actQueues.get(id)[currentPeriod];
	}
}
