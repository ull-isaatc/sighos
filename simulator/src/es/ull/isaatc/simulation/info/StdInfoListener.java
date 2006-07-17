/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdInfoListener implements InfoListener {
	private long iniT;
	private long endT;
	/**
	 * 
	 */
	public StdInfoListener() {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationInfo)
	 */
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			iniT = ((SimulationStartInfo) info).getIniT();
			Simulation simul = ((SimulationStartInfo) info).getSimulation();
			System.out.println("SIMULATION START TS:\t" + simul.getStartTs());
			System.out.println("EXPECTED SIMULATION END TS:\t" + simul.getEndTs());
			
		}
		else if (info instanceof SimulationEndInfo) {
			endT = ((SimulationEndInfo) info).getEndT();
			System.out.println("SIMULATION CPU TIME (ms):\t" + (endT - iniT));		
		}
		else if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			String msg = "";
			switch (eInfo.getType()) {
				case START:
					msg = "STARTED"; break;
				case FINISH:
					msg = "FINISHED"; break;
				case STAACT:
					msg = "STARTS ACTIVITY"; break;
					
				case REQACT:
					msg = "REQUESTS ACTIVITY"; break;
				case ENDACT:
					msg = "ENDS ACTIVITY"; break;
			};
			System.out.println("[" + eInfo.getElemId() + "]\t" + eInfo.getTs() + "\t" 
					+ msg + "\t" + eInfo.getValue());			
		}
		else if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			switch (rInfo.getType()) {
				case START:
					System.out.println("[R" + rInfo.getResId() + "]\t" + rInfo.getTs() + "\tSTARTED");			
					break;
				case ROLON:
					System.out.println("[R" + rInfo.getResId() + "]\t" + rInfo.getTs() + "\tROL ON\tRT" + rInfo.getRtId());			
					break;
				case ROLOFF:
					System.out.println("[R" + rInfo.getResId() + "]\t" + rInfo.getTs() + "\tROL OFF\tRT" + rInfo.getRtId());			
					break;
			};
		}
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			String msg = "";
			switch (rInfo.getType()) {
				case CAUGHT:
					msg = "CAUGHT"; break;
				case RELEASED:
					msg = "RELEASED"; break;
			};
			System.out.println("[R" + rInfo.getResId() + "]\t" + rInfo.getTs() + "\t" 
					+ msg + "\t[" + rInfo.getElemId() + "]\tRT" + rInfo.getRtId());			
		}
	}
}
