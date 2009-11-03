package es.ull.isaatc.simulation.sequential.test;

import java.util.Stack;

import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.UserInfo;
import es.ull.isaatc.simulation.common.inforeceiver.Listener;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.info.ElementActionInfo;

public class ListenerExample extends Listener {

	Stack<Double> startTimes;
	
	public ListenerExample(Simulation simul) {
		super(simul, "");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addGenerated(ExecutionActivityInfo.class);
		startTimes = new Stack<Double>();
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo)  {
			ElementActionInfo actInfo = (ElementActionInfo) info;
			switch(actInfo.getType()) {
				case STAACT: {
					startTimes.push(actInfo.getTs());
					break;
				}
				case ENDACT: {
					double execTime = actInfo.getTs() - startTimes.pop().doubleValue();
					getSimul().getInfoHandler().notifyInfo(new ExecutionActivityInfo(actInfo.getSimul(),execTime, actInfo.getTs()));
					break;
				}
				default: break;
			}
		} else {
			if (info instanceof SimulationEndInfo) {
				SimulationEndInfo endInfo = (SimulationEndInfo) info;
				getSimul().getInfoHandler().notifyInfo(new ExecutionActivityInfo(endInfo.getSimul(), true, endInfo.getTs()));
			} else {
				Error err = new Error("Incompatible info recieved.");
				err.printStackTrace();
			}
		}
	}

	public class ExecutionActivityInfo extends UserInfo {
		
		private double executionTime;
		
		public ExecutionActivityInfo(Simulation simul, boolean finalInfo, double ts) {
			super(simul, ts);
			setFinalInfo(finalInfo);
		}
		
		public ExecutionActivityInfo(Simulation simul, double executionTime, double ts) {
			super(simul, ts);
			this.executionTime = executionTime;
		}

		public double getExecutionTime() {
			return executionTime;
		}
		
		public String toString() {
			return "EXECUTION ACTIVITY INFO\tDURATION: " + getExecutionTime() + "\tTS: " + getTs();
		}
	}

}
