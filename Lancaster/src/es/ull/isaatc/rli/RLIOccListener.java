/**
 * 
 */
package es.ull.isaatc.rli;

import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.listener.PeriodicListener;
import es.ull.isaatc.util.Statistics;

/**
 * "Listens" to the daily ward occupancy.
 * @author Iván Castilla Rodríguez
 */
public class RLIOccListener extends PeriodicListener {
	private HashMap<Integer, int[]> dailyOcc = new HashMap<Integer, int[]>();

	public RLIOccListener(double period) {
		super(period);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.PeriodicListener#changeCurrentPeriod(double)
	 */
	@Override
	protected void changeCurrentPeriod(double ts) {
		for(int []occ : dailyOcc.values())
			occ[currentPeriod] = occ[currentPeriod - 1];
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.PeriodicListener#initializeStorages()
	 */
	@Override
	protected void initializeStorages() {
		for (int rt : simul.getResourceTypeList().keySet()) {
			dailyOcc.put(rt, new int[nPeriods]);
		}
	}

	@Override
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);
		if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			if (rInfo.getType() == ResourceUsageInfo.Type.CAUGHT) {
				dailyOcc.get(rInfo.getRtId())[currentPeriod]++;
			}
			else if (rInfo.getType() == ResourceUsageInfo.Type.RELEASED) {
				dailyOcc.get(rInfo.getRtId())[currentPeriod]--;				
			}		
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Map.Entry<Integer, int[]> entry : dailyOcc.entrySet()) {
			double av = Statistics.average(entry.getValue());
			double sd = Statistics.stdDev(entry.getValue(), av);
			str.append(simul.getResourceType(entry.getKey()).getDescription() + "\t" + av + "\t" + sd + "\n");
		}
		str.append("\n\n");
		for (Map.Entry<Integer, int[]> entry : dailyOcc.entrySet())
			str.append(simul.getResourceType(entry.getKey()).getDescription() + "\t");
		str.append("\n");
		for (int i = 0; i < nPeriods; i++) {
			for (Map.Entry<Integer, int[]> entry : dailyOcc.entrySet())
				str.append(entry.getValue()[i] + "\t");
			str.append("\n");
		}
		return str.toString();
	}
}
