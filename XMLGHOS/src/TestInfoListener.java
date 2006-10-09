
import java.util.HashMap;

import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.*;

/**
 * A standard listener. It only shows the events on the standard output.
 * 
 * @author Iván Castilla Rodríguez
 */
public class TestInfoListener implements SimulationListener {
	
	/**
	 * The initial CPU time, used for showing the total time at the end of the
	 * simulation
	 */
	private long iniT;
	
	private String elemData[] = new String[100];
	
	
	
	
	/**
	 * Creates a simple listener that shows the information grouped by elements.
	 */
	public TestInfoListener() {

		for (int i = 0; i < 100; i++) {
			elemData[i] = new String();
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {

		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			String msg = "";
			switch (eInfo.getType()) {
				case START:
					msg = "STARTED";
					break;
				case FINISH:
					msg = "FINISHED";
					break;
				case STAACT:
					msg = "STARTS ACTIVITY";
					break;
				
				case REQACT:
					msg = "REQUESTS ACTIVITY";
					break;
				case ENDACT:
					msg = "ENDS ACTIVITY";
					break;
			}
			;
			// get(eInfo.getIdentifier()).;
			elemData[eInfo.getIdentifier()] = elemData[eInfo.getIdentifier()] + "\n["
					+ eInfo.getIdentifier() + "]\t" + eInfo.getTs() + "\t" + msg + "\t"
					+ eInfo.getValue();
			
		}
		// else if (info instanceof ResourceInfo) {
		// ResourceInfo rInfo = (ResourceInfo) info;
		// switch (rInfo.getType()) {
		// case START:
		// System.out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() +
		// "\tSTARTED");
		// break;
		// case ROLON:
		// System.out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() +
		// "\tROL ON\tRT" + rInfo.getValue());
		// break;
		// case ROLOFF:
		// System.out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() +
		// "\tROL OFF\tRT" + rInfo.getValue());
		// break;
		// };
		// }
		// else if (info instanceof ResourceUsageInfo) {
		// ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
		// String msg = "";
		// switch (rInfo.getType()) {
		// case CAUGHT:
		// msg = "CAUGHT"; break;
		// case RELEASED:
		// msg = "RELEASED"; break;
		// };
		// System.out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() +
		// "\t"
		// + msg + "\t[" + rInfo.getElemId() + "]\tRT" + rInfo.getRtId());
		// }
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {

		iniT = info.getIniT();
		Simulation simul = info.getSimulation();
		System.out.println("SIMULATION START TS:\t" + simul.getStartTs());
		System.out.println("EXPECTED SIMULATION END TS:\t" + simul.getEndTs());
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {

		System.out.println("SIMULATION CPU TIME (ms):\t" + (info.getEndT() - iniT));
		for (String aux : elemData) {
			if (aux.length() > 0)
				System.out.println(aux);
		}
	}
	
	
	public void infoEmited(TimeChangeInfo info) {

		// TODO Auto-generated method stub
		
	}
}
