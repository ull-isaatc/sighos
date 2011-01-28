/**
 * 
 */
package es.ull.isaatc.rli;

import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.Statistics;

/**
 * "Listens" to the daily ward occupancy.
 * @author Iván Castilla Rodríguez
 */
public class RLIOccView extends View {
	private HashMap<Integer, int[]> dailyOcc = new HashMap<Integer, int[]>();
	/** The number of periods contained in the simulation time. */
	protected int nPeriods = 1;
	/** The current period to store information. */
	protected int currentPeriod = 0;
	private double start;
	private double end;
	private double period;

	public RLIOccView(Simulation simul, SimulationTime period) {
		super(simul, "RLI occupancy viewer");
		addEntrance(TimeChangeInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(SimulationEndInfo.class);
		this.start = simul.getStartTs().convert(simul.getUnit()).getValue();
		this.end = simul.getEndTs().convert(simul.getUnit()).getValue();
		this.period = period.convert(simul.getUnit()).getValue();
		double nPeriods = (end - start) / this.period;
		if (nPeriods > (int)nPeriods)
			this.nPeriods = (int)nPeriods + 1;
		else
			this.nPeriods = (int)nPeriods;
		for (int rt : simul.getResourceTypeList().keySet())
			dailyOcc.put(rt, new int[this.nPeriods]);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof TimeChangeInfo) {
			TimeChangeInfo tInfo = (TimeChangeInfo)info;
			// increase the current period until the timestamp of the info is reached
			while (tInfo.getTs().getValue() >= ((currentPeriod + 1) * period) + start) {
				currentPeriod++;
				// performs the required operations when the period changes
				for(int []occ : dailyOcc.values())
					occ[currentPeriod] = occ[currentPeriod - 1];
			}
		} 
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			if (rInfo.getType() == ResourceUsageInfo.Type.CAUGHT) {
				dailyOcc.get(rInfo.getRt().getIdentifier())[currentPeriod]++;
			}
			else if (rInfo.getType() == ResourceUsageInfo.Type.RELEASED) {
				dailyOcc.get(rInfo.getRt().getIdentifier())[currentPeriod]--;				
			}		
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println(this);
		}
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
			str.append(getSimul().getResourceType(entry.getKey()).getDescription() + "\t" + av + "\t" + sd + "\n");
		}
		str.append("\n\n");
		for (Map.Entry<Integer, int[]> entry : dailyOcc.entrySet())
			str.append(getSimul().getResourceType(entry.getKey()).getDescription() + "\t");
		str.append("\n");
		for (int i = 0; i < nPeriods; i++) {
			for (Map.Entry<Integer, int[]> entry : dailyOcc.entrySet())
				str.append(entry.getValue()[i] + "\t");
			str.append("\n");
		}
		return str.toString();
	}
}
