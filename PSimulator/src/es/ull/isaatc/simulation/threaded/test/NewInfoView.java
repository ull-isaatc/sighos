package es.ull.isaatc.simulation.threaded.test;

import java.io.PrintStream;

import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;
import es.ull.isaatc.simulation.threaded.Simulation;

public class NewInfoView extends View {

	private final PrintStream out = System.out;
	private int activitiesExecuted = 0;
	private double executionTime = 0.0;
	
	public NewInfoView(Simulation simul) {
		super(simul, "");
		addEntrance(ListenerExample.ExecutionActivityInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ListenerExample.ExecutionActivityInfo) {
			ListenerExample.ExecutionActivityInfo execInfo = (ListenerExample.ExecutionActivityInfo) info;
			if (!execInfo.isFinalInfo()) {
				activitiesExecuted ++;
				executionTime += execInfo.getExecutionTime();
			}else
				out.println("ACTIVITY EXECUTION\tEXECUTED ACTIVITIES: " + activitiesExecuted + "\tEXECUTION TIME: " + executionTime);
		} else {
			Error err = new Error("Incompatible info recieved.");
			err.printStackTrace();
		}
	}

	public int getActivitiesExecuted() {
		return activitiesExecuted;
	}

	public double getExecutionTime() {
		return executionTime;
	}

}
