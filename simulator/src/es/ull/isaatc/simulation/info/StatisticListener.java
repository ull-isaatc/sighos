/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import es.ull.isaatc.simulation.*;

/**
 * Stores the information related to a simulation. 
 * @author Iván Castilla Rodríguez
 */
public class StatisticListener implements SimulationListener {
	protected long iniT;
	protected long endT;
	protected double simStart;
	protected double simEnd;
	private int nStartedElem;
	private HashMap<Integer, Integer> nElemXType = new HashMap<Integer, Integer>();
	private HashMap<Integer, int[]> actQueues = new HashMap<Integer, int[]>();
	private HashMap<Integer, double[]> actUsage = new HashMap<Integer, double[]>();
	private double period = 1.0;
	private int currentPeriod = 0;
	private int nPeriods;
	protected int[] lpIds;
	protected int[][] amIds;
	protected int[][] actIds;
	protected int[][] rtIds;
	protected int firstElementId;
	protected int lastElementId;
	
	/**
	 * Creates a listener which generates several statistics.
	 * @param period 
	 */
	public StatisticListener(double period) {
		this.period = period;
	}
	
	public StatisticListener() {
	}
	
	/**
	 * @return Returns the nPeriods.
	 */
	public int getNPeriods() {
		return nPeriods;
	}

	public int getNStartedElem() {
		return nStartedElem;
	}
	
	/**
	 * @return Returns the nElemXType.
	 */
	public HashMap<Integer, Integer> getNElemXType() {
		return nElemXType;
	}

	/**
	 * @return Returns the actQueues.
	 */
	public HashMap<Integer, int[]> getActQueues() {
		return actQueues;
	}

	/**
	 * @return Returns the actUsage.
	 */
	public HashMap<Integer, double[]> getActUsage() {
		return actUsage;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(double period) {
		this.period = period;
	}

	/**
	 * @return Returns the actIds.
	 */
	public int[][] getActIds() {
		return actIds;
	}

	/**
	 * @return Returns the amIds.
	 */
	public int[][] getAmIds() {
		return amIds;
	}

	/**
	 * @return Returns the lpIds.
	 */
	public int[] getLpIds() {
		return lpIds;
	}

	/**
	 * @return Returns the rtIds.
	 */
	public int[][] getRtIds() {
		return rtIds;
	}

	/**
	 * @return Returns the iniT.
	 */
	public long getIniT() {
		return iniT;
	}

	/**
	 * @return Returns the endT.
	 */
	public long getEndT() {
		return endT;
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
	 * @return Returns the firstElementId.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	/**
	 * @return Returns the lastElementId.
	 */
	public int getLastElementId() {
		return lastElementId;
	}
	
	public void infoEmited(SimulationObjectInfo info) {
		// New period
		if (info.getTs() >= ((currentPeriod + 1) * period) + simStart) {				
			currentPeriod++;
			for (int[]queue : actQueues.values())
				queue[currentPeriod] = queue[currentPeriod - 1];
		}
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			switch (eInfo.getType()) {
				case START:
					nStartedElem++;
					if (!nElemXType.containsKey(eInfo.getValue()))
						nElemXType.put(eInfo.getValue(), 1);
					else
						nElemXType.put(eInfo.getValue(), nElemXType.get(eInfo.getValue()) + 1);
					break;
				case REQACT:
					actQueues.get(eInfo.getValue())[currentPeriod]++;
					break;
				case STAACT:
					actQueues.get(eInfo.getValue())[currentPeriod]--;
					// There's no control over unbalanced starts and ends 
					actUsage.get(eInfo.getValue())[currentPeriod] -= info.getTs();
					break;
				case ENDACT:
					// There's no control over unbalanced starts and ends 
					actUsage.get(eInfo.getValue())[currentPeriod] += info.getTs();
					break;
			}
		}
	}

	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
		firstElementId = info.getFirstElementId();
		Simulation simul = info.getSimulation();
		simStart = simul.getStartTs();
		simEnd = simul.getEndTs();
		lpIds = new int[simul.getLPSize()];
		for (int i = 0; i < lpIds.length; i++)
			lpIds[i] = simul.getLogicalProcess(i).getIdentifier();
		ArrayList amList = simul.getActivityManagerList();
		amIds = new int[amList.size()][2];
		for (int i = 0; i < amIds.length; i++) {
			ActivityManager am = (ActivityManager) amList.get(i);
			amIds[i][0] = am.getIdentifier(); 
			amIds[i][1] = am.getLp().getIdentifier();
		}
		// Fills the activity list
		TreeMap<Integer, Activity> actList = simul.getActivityList();
		actIds = new int[actList.size()][2];
		int cont = 0;
		for (Activity act : actList.values()) {
			actIds[cont][0] = act.getIdentifier(); 
			actIds[cont++][1] = act.getManager().getIdentifier();
		}
		// Creates the activity queues map
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		for (int []ids : actIds) {
			actQueues.put(ids[0], new int[nPeriods]);
			actUsage.put(ids[0], new double[nPeriods]);
			
		}
		// Fills the resource type list
		TreeMap<Integer, ResourceType> rtList = simul.getResourceTypeList();
		rtIds = new int[rtList.size()][2];
		for (int i = 0; i < rtIds.length; i++) {
			rtIds[i][0] = rtList.get(i).getIdentifier(); 
			rtIds[i][1] = rtList.get(i).getManager().getIdentifier();
		}		
	}

	public void infoEmited(SimulationEndInfo info) {
		endT = info.getEndT();
		lastElementId = info.getLastElementId();
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
		StringBuffer str = new StringBuffer("<<<STATISTICAL REVIEW>>>\r\n");
		str.append("Elements created: " + nStartedElem + "\r\n");
		str.append("Created (per type)\r\n");
		for (Integer et : nElemXType.keySet())
			str.append("[T" + et + "]\t" + nElemXType.get(et) + "\r\n");		
		str.append("Activity Queues(PERIOD: " + period + ")\r\n");
		for (Map.Entry<Integer,int[]> values : actQueues.entrySet()) {
			str.append("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				str.append("\t" + values.getValue()[j]);
			str.append("\r\n");
		}
		str.append("Activity Usage(PERIOD: " + period + ")\r\n");
		for (Map.Entry<Integer,double[]> values : actUsage.entrySet()) {
			str.append("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				str.append("\t" + values.getValue()[j]);
			str.append("\r\n");
		}
		return str.toString();
	}
}
