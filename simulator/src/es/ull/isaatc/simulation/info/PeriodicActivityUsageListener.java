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
 * @author Iván Castilla Rodríguez
 *
 */
public class PeriodicActivityUsageListener implements SimulationListener {
	private double period;
	private int nPeriods = 1;
	private int currentPeriod = 0;
	private double simStart;
	private double simEnd;
	private HashMap<Integer, double[]> actUsage = new HashMap<Integer, double[]>();

	public PeriodicActivityUsageListener (double period) {
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
	 * @return Returns the actUsage.
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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
	}
	
	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
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
