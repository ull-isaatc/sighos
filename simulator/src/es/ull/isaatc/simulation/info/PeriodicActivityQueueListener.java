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
	private double period;
	private int nPeriods = 1;
	private int currentPeriod = 0;
	private double simStart;
	private double simEnd;
	private HashMap<Integer, int[]> actQueues = new HashMap<Integer, int[]>();
	
	public PeriodicActivityQueueListener (double period) {
		this.period = period;
	}

	/**
	 * @return Returns the period.
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * @return Returns the nPeriods.
	 */
	public int getNPeriods() {
		return nPeriods;
	}

	/**
	 * @return Returns the simStart.
	 */
	public double getSimStart() {
		return simStart;
	}

	/**
	 * @return Returns the simEnd.
	 */
	public double getSimEnd() {
		return simEnd;
	}

	/**
	 * @return Returns the actQueues.
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
			if (eInfo.getTs() >= ((currentPeriod + 1) * period) + simStart) {				
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

	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
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
