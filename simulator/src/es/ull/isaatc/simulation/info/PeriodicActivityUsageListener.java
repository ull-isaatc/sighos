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
 * Periodically stores the usage of an activity, that is, the total amount of time
 * that this activity has been used.
 * @author Iván Castilla Rodríguez
 */
public class PeriodicActivityUsageListener implements SimulationListener {
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
	/** The activity usage by period. */
	private HashMap<Integer, double[]> actUsage = new HashMap<Integer, double[]>();

	/**
	 * Creates a listener with period <code>period</code>.
	 * @param period The interval of time between two consecutive storages.
	 */
	public PeriodicActivityUsageListener (double period) {
		this.period = period;
	}

	/**
	 * Creates a default listener.
	 */
	public PeriodicActivityUsageListener () {
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
	 * Returns the activity usage by period.
	 * @return The activity usage by period.
	 */
	public HashMap<Integer, double[]> getActUsage() {
		return actUsage;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			// New period
			if (eInfo.getTs() >= ((currentPeriod + 1) * period) + simStart)				
				currentPeriod++;
			if (eInfo.getType() == ElementInfo.Type.STAACT)
				// There's no control over unbalanced starts and ends 
				actUsage.get(eInfo.getValue())[currentPeriod] -= eInfo.getTs();
			else if (eInfo.getType() == ElementInfo.Type.ENDACT)
				// There's no control over unbalanced starts and ends 
				actUsage.get(eInfo.getValue())[currentPeriod] += eInfo.getTs();
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
			actUsage.put(new Integer(actList.get(i).getIdentifier()), new double[nPeriods]);			
	}

	// Nothing to do
	public void infoEmited(SimulationEndInfo info) {
	}
	
	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Activity Usage(PERIOD: " + period + ")\r\n");
		for (Map.Entry<Integer,double[]> values : actUsage.entrySet()) {
			str.append("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				str.append("\t" + values.getValue()[j]);
			str.append("\r\n");
		}		
		return str.toString();
	}

}
