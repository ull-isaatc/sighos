/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.*;
import es.ull.isaatc.simulation.listener.SimulationListener;

class FlowSkeletonFactory {
	private int finished = 0;
	
	static FlowSkeleton getFlowSkeleton(Flow f) {
		FlowSkeleton fs = null;
		if (f instanceof SingleFlow)
			fs = new SingleFlowSkeleton((SingleFlow)f);
		else if (f instanceof GroupFlow)
			fs = new GroupFlowSkeleton((GroupFlow)f);
		return fs;
	}
	
	public int getFinished() {
		return finished;
	}
	
	public void incFinished() {
		finished++;
	}
}

interface FlowSkeleton {}

class SingleFlowSkeleton implements FlowSkeleton {	
	int act;
	
	SingleFlowSkeleton(SingleFlow f) {
		act = f.getActivity().getIdentifier();
	}
	
}

class GroupFlowSkeleton implements FlowSkeleton {
	enum Type {SEQ, SIM}
	Type type;
	FlowSkeleton[] list;
	
	GroupFlowSkeleton(GroupFlow f) {
		list = new FlowSkeleton[f.getDescendants().size()];
		if (f instanceof SequenceFlow)
			type = Type.SEQ;		
		if (f instanceof SimultaneousFlow)
			type = Type.SIM;		
	}
	
	public int getNFlow() {
		return list.length;
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementGeneralTestListener implements SimulationListener {
	HashMap<Integer, TestInfo> table;
	ArrayList<Integer> errorStart = new ArrayList<Integer>();
	TreeSet<Resource> resUsage;
	ArrayList<Integer> errorResUsage = new ArrayList<Integer>();

	class TestInfo {
		int nActToDo;
	}
	
	/**
	 * 
	 */
	public ElementGeneralTestListener() {
		table = new HashMap<Integer, TestInfo>();
//		resUsage = new HashMap<Integer, Boolean>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationObjectInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			// Verifies if the start event is the first event 
			if (!table.containsKey(eInfo.getIdentifier())) {
				table.put(eInfo.getIdentifier(), new TestInfo());
				if (eInfo.getType() != ElementInfo.Type.START)
					errorStart.add(eInfo.getIdentifier());
			}
			Element elem = (Element)eInfo.getSource();
			switch (eInfo.getType()) {
				case START:
					TestInfo t = table.get(eInfo.getIdentifier());
					t.nActToDo = elem.getFlow().countActivities()[0] + elem.getFlow().countActivities()[1]; 
					break;
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			switch(rInfo.getType()) {
			case CAUGHT:
				if (resUsage.contains(rInfo.getSource()))					
					errorResUsage.add(rInfo.getIdentifier());
				else
					resUsage.add((Resource)rInfo.getSource());
				break;
			case RELEASED:
				if (resUsage.remove((Resource)rInfo.getSource()))
					;//ERROR... ¿de q tipo?
			}
		}

	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {

	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {

	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.TimeChangeInfo)
	 */
	public void infoEmited(TimeChangeInfo info) {

	}

}
