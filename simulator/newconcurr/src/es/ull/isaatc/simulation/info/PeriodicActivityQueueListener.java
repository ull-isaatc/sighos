/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.util.OrderedList;

/**
 * Periodically stores the size of the activity queues. 
 * @author Iván Castilla Rodríguez
 *
 */
public class PeriodicActivityQueueListener implements SimulationListener {
	/**	The interval of time between two consecutive storages. */
	private double period;
	/** The number of periods contained in the simulation time. */
	private int nPeriods = 1;
	/** The current period to store information. */
	private int currentPeriod = 0;
	/** The simulation start timestamp. */
	private double simStart;
	/** The simulation end timestamp. */
	private double simEnd;
	/** The size of the activity queues by period. */
	private HashMap<Integer, int[]> actQueues = new HashMap<Integer, int[]>();
	
	/**
	 * Creates a listener with period <code>period</code>.
	 * @param period The interval of time between two consecutive storages.
	 */
	public PeriodicActivityQueueListener (double period) {
		this.period = period;
	}

	/**
	 * Creates a default listener.
	 */
	public PeriodicActivityQueueListener () {
	}

	/**
	 * Returns the interval of time between two consecutive storages.
	 * @return The interval of time between two consecutive storages.
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * Sets the period of the listener
	 * @param period The period to set.
	 */
	public void setPeriod(double period) {
		this.period = period;
	}

	/**
	 * Returns the number of periods contained in the simulation time.
	 * @return The number of periods contained in the simulation time.
	 */
	public int getNPeriods() {
		return nPeriods;
	}

	/**
	 * Returns the simulation start timestamp.
	 * @return The simulation start timestamp.
	 */
	public double getSimStart() {
		return simStart;
	}

	/**
	 * Returns the simulation end timestamp.
	 * @return The simulation end timestamp.
	 */
	public double getSimEnd() {
		return simEnd;
	}

	/**
	 * Returns the size of the activity queues by period.
	 * @return The size of the activity queues by period.
	 */
	public HashMap<Integer, int[]> getActQueues() {
		return actQueues;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			// New period
			while (eInfo.getTs() >= ((currentPeriod + 1) * period) + simStart) {
				currentPeriod++;
				for (int[]queue : actQueues.values())
					queue[currentPeriod] = queue[currentPeriod - 1];
			}
			if (eInfo.getType() == ElementInfo.Type.REQACT)
				actQueues.get(eInfo.getValue())[currentPeriod]++;
			else if (eInfo.getType() == ElementInfo.Type.STAACT)
				actQueues.get(eInfo.getValue())[currentPeriod]--;
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		Simulation simul = info.getSimulation();
		simStart = simul.getStartTs();
		simEnd = simul.getEndTs();
		// Creates the activity queues map
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		OrderedList<Activity> actList = simul.getActivityList();
		for (int i = 0; i < actList.size(); i++)			
			actQueues.put(new Integer(actList.get(i).getIdentifier()), new int[nPeriods]);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		for (int[]queue : actQueues.values()) {
			for (int cont = currentPeriod + 1; cont < queue.length; cont++)
				queue[cont] = queue[cont - 1];
		}
	}

	// Nothing to do 
	public void infoEmited(TimeChangeInfo info) {
		
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Activity Queues(PERIOD: " + period + ")\r\n");
		for (Map.Entry<Integer,int[]> values : actQueues.entrySet()) {
			str.append("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				str.append("\t" + values.getValue()[j]);
			str.append("\r\n");
		}		
		return str.toString();
	}

}
