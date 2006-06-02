/**
 * 
 */
package es.ull.cyc.simulation.results;

import java.util.ArrayList;
import java.util.HashMap;
import es.ull.cyc.simulation.*;
import es.ull.cyc.util.OrderedList;


/**
 * Stores the information related to a simulation. 
 * @author Iván Castilla Rodríguez
 */
public class SimulationResults {
	protected ArrayList<ElementStatistics> elementStatistics;
	protected ArrayList<ActivityStatistics> activityStatistics;
	protected ArrayList<PendingFlowStatistics> pendingFlowStatistics;
	protected long iniT;
	protected long endT;
	protected double simStart;
	protected double simEnd;
	protected double simRealEnd;
	protected int[] lpIds;
	protected int[][] amIds;
	protected int[][] actIds;
	protected int[][] rtIds;
	protected int firstElementId;
	protected int lastElementId;
	
	public SimulationResults() {
		elementStatistics = new ArrayList<ElementStatistics>();
		activityStatistics = new ArrayList<ActivityStatistics>();
		pendingFlowStatistics = new ArrayList<PendingFlowStatistics>();
	}
	
	public synchronized void add(StatisticData data) {
		if (data instanceof ElementStatistics)
			elementStatistics.add((ElementStatistics)data);
		else if (data instanceof ActivityStatistics)
			activityStatistics.add((ActivityStatistics)data);
		else if (data instanceof PendingFlowStatistics)
			pendingFlowStatistics.add((PendingFlowStatistics)data);
	}


	public int createdElements() {
		int count = 0;
		for (int i = 0; i < elementStatistics.size(); i++) {
			ElementStatistics es = elementStatistics.get(i);
			if (es.getType() == ElementStatistics.START)
				count++;
		}		
		return count;		
	}
	
	public int[] computeQueueSizes() {
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
		int queues[] = new int[actIds.length];
		for (int i = 0; i < actIds.length; i++)			
			hash.put(new Integer(actIds[i][0]), new Integer(i));
		for (int i = 0; i < activityStatistics.size(); i++) {
			ActivityStatistics as = activityStatistics.get(i);
			Integer pos = hash.get(new Integer(as.getActId()));
			queues[pos.intValue()]++;
		}		
		return queues;
	}
	
	public int[][] computeQueueSizes(double period) {
		int nPeriods;
		int count = 0;
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
		for (int i = 0; i < actIds.length; i++)			
			hash.put(new Integer(actIds[i][0]), new Integer(i));
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		int [][]queues = new int[actIds.length][nPeriods];
		for (int i = 0; i < elementStatistics.size(); i++) {
			ElementStatistics es = elementStatistics.get(i);
			// New period
			if (es.getTs() >= ((count + 1) * period) + simStart) {				
				count++;
				for (int j = 0; j < actIds.length; j++) {
					queues[j][count] = queues[j][count - 1];
				}				
			}
			Integer pos = hash.get(new Integer(es.getValue()));
			if (es.getType() == ElementStatistics.REQACT)
				queues[pos.intValue()][count]++;
			else if (es.getType() == ElementStatistics.STAACT)
				queues[pos.intValue()][count]--;
		}
		while (count++ < nPeriods - 1)
			for (int j = 0; j < actIds.length; j++)
				queues[j][count] = queues[j][count - 1];
			
		return queues;
	}
	
	public double[]periodicUse(double period) {
		int nPeriods;
		int count = 0;
		double auxPeriods = ((simEnd - simStart) / period);
		if (auxPeriods > (int)auxPeriods)
			nPeriods = (int)auxPeriods + 1;
		else
			nPeriods = (int)auxPeriods;
		double[]result = new double[nPeriods];
		for (int i = 0; i < elementStatistics.size(); i++) {
			ElementStatistics es = elementStatistics.get(i);
			// New period
			if (es.getTs() >= ((count + 1) * period) + simStart) {				
				count++;
			}
			// There's no control over unbalanced starts and ends 
			if (es.getType() == ElementStatistics.STAACT)
				result[count] -= es.getTs();
			else if (es.getType() == ElementStatistics.ENDACT)
				result[count] += es.getTs();
		}
		return result;
	}

//	public double []computeAvgWaitTime() {
//		double []avg = new double[actIds.length];
//		int [][]nElem = new int[2][actIds.length];
//		HashMap hash = new HashMap();
//		for (int i = 0; i < actIds.length; i++)			
//			hash.put(new Integer(actIds[i][0]), new Integer(i));
//		for (int i = 0; i < elementStatistics.size(); i++) {
//			ElementStatistics es = (ElementStatistics) elementStatistics.get(i);
//			Integer pos = (Integer)hash.get(new Integer(es.getValue()));
//			if (es.getType() == ElementStatistics.REQACT) {
//				avg[pos.intValue()] -= es.getTs();
//				nElem[0][pos.intValue()]++;
//			}
//			else if (es.getType() == ElementStatistics.STAACT) {
//				avg[pos.intValue()] += es.getTs();
//				nElem[1][pos.intValue()]++;
//			}
//		}
//		// Se añaden las esperas de actividades que no han llegado a realizarse
//		for (int i = 0; i < actIds.length; i++) {
//			avg[i] += (nElem[0][i] - nElem[1][i]) * simEnd;
//			avg[i] = avg[i] / nElem[0][i];
//		}
//		return avg;
//	}
	
	public void saveSimulationStructure(Simulation simul) {
		simStart = simul.getStartTs();
		simEnd = simul.getEndTs();
		firstElementId = Generator.getElemCounter();
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
		OrderedList<Activity> actList = simul.getActivityList();
		actIds = new int[actList.size()][2];
		for (int i = 0; i < actIds.length; i++) {
			actIds[i][0] = actList.get(i).getIdentifier(); 
			actIds[i][1] = actList.get(i).getManager().getIdentifier();
		}
		OrderedList<ResourceType> rtList = simul.getResourceTypeList();
		rtIds = new int[rtList.size()][2];
		for (int i = 0; i < rtIds.length; i++) {
			rtIds[i][0] = rtList.get(i).getIdentifier(); 
			rtIds[i][1] = rtList.get(i).getManager().getIdentifier();
		}		
	}
	
	/**
	 * @param endT The endT to set.
	 */
	public void setEndT(long endT) {
		this.endT = endT;
	}

	/**
	 * @param iniT The iniT to set.
	 */
	public void setIniT(long iniT) {
		this.iniT = iniT;
	}

	/**
	 * @param simRealEnd The simRealEnd to set.
	 */
	public void setSimRealEnd(double realEndTs) {
		this.simRealEnd = realEndTs;
	}

	/**
	 * @return Returns the actIds.
	 */
	public int[][] getActIds() {
		return actIds;
	}

	/**
	 * @return Returns the activityStatistics.
	 */
	public ArrayList<ActivityStatistics> getActivityStatistics() {
		return activityStatistics;
	}

	/**
	 * @return Returns the amIds.
	 */
	public int[][] getAmIds() {
		return amIds;
	}

	/**
	 * @return Returns the elementStatistics.
	 */
	public ArrayList<ElementStatistics> getElementStatistics() {
		return elementStatistics;
	}

	/**
	 * @return Returns the endT.
	 */
	public long getEndT() {
		return endT;
	}

	/**
	 * @return Returns the simEnd.
	 */
	public double getSimEnd() {
		return simEnd;
	}

	/**
	 * @return Returns the iniT.
	 */
	public long getIniT() {
		return iniT;
	}

	/**
	 * @return Returns the lpIds.
	 */
	public int[] getLpIds() {
		return lpIds;
	}

	/**
	 * @return Returns the pendingFlowStatistics.
	 */
	public ArrayList<PendingFlowStatistics> getPendingFlowStatistics() {
		return pendingFlowStatistics;
	}

	/**
	 * @return Returns the simRealEnd.
	 */
	public double getSimRealEnd() {
		return simRealEnd;
	}

	/**
	 * @return Returns the rtIds.
	 */
	public int[][] getRtIds() {
		return rtIds;
	}

	/**
	 * @return Returns the simStart.
	 */
	public double getSimStart() {
		return simStart;
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

	/**
	 * @param firstElementId The firstElementId to set.
	 */
	public void setFirstElementId(int firstElementId) {
		this.firstElementId = firstElementId;
	}

	/**
	 * @param lastElementId The lastElementId to set.
	 */
	public void setLastElementId(int lastElementId) {
		this.lastElementId = lastElementId;
	}

}
