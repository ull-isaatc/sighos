/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import java.io.PrintStream;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * A standard listener. It only shows the events on the standard output.
 * @author Iván Castilla Rodríguez
 */
public class StdInfoListener implements SimulationListener {
	/** The initial CPU time, used for showing the total time at the end of the simulation */
	private long iniT;
	
	/** Stream where the information will be sent */
	private PrintStream out;
	
	/**
	 * Creates a simple listener that shows the information on the standard output. 
	 */
	public StdInfoListener(PrintStream out) {
		this.out = out;
	}

	@Override
	public void infoEmited(SimulationObjectInfo info) {
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
			out.println("[" + eInfo.getIdentifier() + "]\t" + eInfo.getTs() + "\t" 
					+ msg + "\t" + eInfo.getValue());			
		}
		else if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			switch (rInfo.getType()) {
				case START:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() + "\tSTARTED");			
					break;
				case ROLON:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() + "\tROL ON\tRT" + rInfo.getValue());			
					break;
				case ROLOFF:
					out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() + "\tROL OFF\tRT" + rInfo.getValue());			
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
			out.println("[R" + rInfo.getIdentifier() + "]\t" + rInfo.getTs() + "\t" 
					+ msg + "\t[" + rInfo.getElemId() + "]\tRT" + rInfo.getRtId());			
		}
	}

	@Override
	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
		Simulation simul = info.getSimulation();
		out.println("SIMULATION START TS:\t" + simul.getStartTs());
		out.println("EXPECTED SIMULATION END TS:\t" + simul.getEndTs());			
	}

	@Override
	public void infoEmited(SimulationEndInfo info) {
		out.println("SIMULATION CPU TIME (ms):\t" + (info.getEndT() - iniT));					
	}

	@Override
	public void infoEmited(TimeChangeInfo info) {
		out.println("<<< LP clock advanced <<<" + info.getTs());		
	}
}
