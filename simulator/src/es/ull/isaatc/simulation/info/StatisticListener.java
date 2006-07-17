/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.OrderedList;


/**
 * Stores the information related to a simulation. 
 * @author Iván Castilla Rodríguez
 */
public class StatisticListener implements InfoListener {
	protected long iniT;
	protected long endT;
	protected double simStart;
	protected double simEnd;
	private int nStartedElem;
	private HashMap<Integer, Integer> nElemXType = new HashMap<Integer, Integer>();
	private HashMap<Integer, int[]> actQueues = new HashMap<Integer, int[]>();
	private double period = 1.0;
	private int currentPeriod = 0;
	private int nPeriods;
	protected int[] lpIds;
	protected int[][] amIds;
	protected int[][] actIds;
	protected int[][] rtIds;
	protected int firstElementId;
	protected int lastElementId;
	
	public StatisticListener(double period) {
		this.period = period;
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

	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			iniT = ((SimulationStartInfo) info).getIniT();
			firstElementId = ((SimulationStartInfo) info).getFirstElementId();
			saveSimulationStructure(((SimulationStartInfo) info).getSimulation());
		}
		else if (info instanceof SimulationEndInfo) {
			endT = ((SimulationEndInfo) info).getEndT();
			lastElementId = ((SimulationEndInfo) info).getLastElementId();
			for (int[]queue : actQueues.values()) {
				for (int cont = currentPeriod + 1; cont < queue.length; cont++)
					queue[cont] = queue[cont - 1];
			}
			showResults();
		}
		else if (info instanceof ElementInfo) {
			queueVariation((ElementInfo) info);
		}		
	}

	private void queueVariation(ElementInfo info) {
		// New period
		if (info.getTs() >= ((currentPeriod + 1) * period) + simStart) {				
			currentPeriod++;
			for (int[]queue : actQueues.values())
				queue[currentPeriod] = queue[currentPeriod - 1];
		}
		switch (info.getType()) {
			case START:
				nStartedElem++;
				if (!nElemXType.containsKey(info.getValue()))
					nElemXType.put(info.getValue(), 1);
				else
					nElemXType.put(info.getValue(), nElemXType.get(info.getValue()) + 1);
				break;
			case REQACT:
				actQueues.get(info.getValue())[currentPeriod]++;
				break;
			case STAACT:
				actQueues.get(info.getValue())[currentPeriod]--;
				break;
		}
	}
	
	private void saveSimulationStructure(Simulation simul) {
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
		OrderedList<Activity> actList = simul.getActivityList();
		actIds = new int[actList.size()][2];
		for (int i = 0; i < actIds.length; i++) {
			actIds[i][0] = actList.get(i).getIdentifier(); 
			actIds[i][1] = actList.get(i).getManager().getIdentifier();
		}
		// Creates the activity queues map
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		for (int i = 0; i < actIds.length; i++)			
			actQueues.put(new Integer(actIds[i][0]), new int[nPeriods]);
		// Fills the resource type list
		OrderedList<ResourceType> rtList = simul.getResourceTypeList();
		rtIds = new int[rtList.size()][2];
		for (int i = 0; i < rtIds.length; i++) {
			rtIds[i][0] = rtList.get(i).getIdentifier(); 
			rtIds[i][1] = rtList.get(i).getManager().getIdentifier();
		}		
	}
	
	// NOTA: TEMPORAL
	public void showResults() {
		System.out.println("<<<STATISTICAL REVIEW>>>");
		System.out.println("Elements created: " + nStartedElem);
		System.out.println("Created (per type)");
		for (Integer et : nElemXType.keySet())
			System.out.println("[T" + et + "]\t" + nElemXType.get(et));		
		System.out.println("Activity Queues(PERIOD: " + period + ")");
		for (Map.Entry<Integer,int[]> values : actQueues.entrySet()) {
			System.out.print("A" + values.getKey() + ":");
			for (int j = 0; j < values.getValue().length; j++)
				System.out.print("\t" + values.getValue()[j]);
			System.out.println("");
		}
	}
}
