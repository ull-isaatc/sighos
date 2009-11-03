/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import java.io.PrintStream;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * A standard listener. It only shows the events on the standard output.
 * @author Iv�n Castilla Rodr�guez
 */
public class StdInfoListener implements SimulationListener, SimulationObjectListener, TimeChangeListener {
	/** The initial CPU time, used for showing the total time at the end of the simulation */
	private long iniT;
	
	/** Stream where the information will be sent */
	private PrintStream out;
	
	private Simulation simul = null;
	/**
	 * Creates a simple listener that shows the information on the standard output. 
	 */
	public StdInfoListener() {
		this.out = System.out;
	}

	/**
	 * Creates a simple listener that shows the information on the standard output. 
	 */
	public StdInfoListener(PrintStream out) {
		this.out = out;
	}

	public void infoEmited(SimulationObjectInfo info) {
		SimulationTime ts = simul.double2SimulationTime(info.getTs());
		if (info instanceof ElementInfo) {
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
				case RESACT:
					msg = "RESUMES ACTIVITY"; break;
				case INTACT:
					msg = "INTERRUPTS ACTIVITY"; break;
			};
			out.println("[" + eInfo.getIdentifier() + "]\t" + ts + "\t" 
					+ msg + "\t" + eInfo.getValue());			
		}
		else if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			switch (rInfo.getType()) {
				case START:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + ts + "\tSTARTED");			
					break;
				case ROLON:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + ts + "\tROL ON\tRT" + rInfo.getValue());			
					break;
				case ROLOFF:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + ts + "\tROL OFF\tRT" + rInfo.getValue());			
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
			out.println("[R" + rInfo.getIdentifier() + "]\t" + ts + "\t" 
					+ msg + "\t[" + rInfo.getElemId() + "]\tRT" + rInfo.getRtId());			
		}
	}

	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
		simul = info.getSimulation();
		out.println("SIMULATION START TS:\t" + simul.getStartTs());
		out.println("EXPECTED SIMULATION END TS:\t" + simul.getEndTs());			
	}

	public void infoEmited(SimulationEndInfo info) {
		out.println("SIMULATION CPU TIME (ms):\t" + (info.getEndT() - iniT));					
	}

	public void infoEmited(TimeChangeInfo info) {
		out.println("<<< LP clock advanced <<<" + simul.double2SimulationTime(info.getTs()));		
	}
}