/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.ElementActionInfo.Type;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * @author Iván Castilla Rodríguez
 * 
 */
public class CheckFlowsView extends WFPTestView {
	FlowNode flow;
	Time[] durations;
	ArrayList<TreeSet<EventToCheck>> futureFlow;
	private boolean ok = true;

	public CheckFlowsView(Simulation simul, Flow f,
			Time[] durations) {
		this(simul, f, durations, true);
	}

	public CheckFlowsView(Simulation simul, Flow f, Time[] durations, boolean detailed) {
		super(simul, "Checking flows...", detailed);
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(SimulationStartInfo.class);
		this.durations = durations;
		this.flow = createFlow(f);
		futureFlow = new ArrayList<TreeSet<EventToCheck>>(
				WFPTestSimulationFactory.DEFNELEMENTS);
		for (int i = 0; i < WFPTestSimulationFactory.DEFNELEMENTS; i++) 
			futureFlow.add(new TreeSet<EventToCheck>());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.ull.isaatc.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.
	 * isaatc.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		EventToCheck ev;
		if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			if (detailed)
				System.out.print(eInfo + "...\t");
			switch(eInfo.getType()) {
			case REQACT:
				ev = new EventToCheck(Type.REQACT, eInfo.getActivity().getIdentifier(), eInfo.getTs());
				printResult(futureFlow.get(eInfo.getElem().getIdentifier()).remove(ev), "unexpected event!"); 
				// debería chequear los recursos
				futureFlow.get(eInfo.getElem().getIdentifier()).add(new EventToCheck(Type.STAACT, eInfo.getActivity().getIdentifier(), eInfo.getTs()));
				break;
			case STAACT:
				ev = new EventToCheck(Type.STAACT, eInfo.getActivity().getIdentifier(), eInfo.getTs());
				printResult(futureFlow.get(eInfo.getElem().getIdentifier()).remove(ev), "unexpected event!"); 
				double nextTs = eInfo.getTs() + getSimul().simulationTime2Long(durations[eInfo.getActivity().getIdentifier()]);
				futureFlow.get(eInfo.getElem().getIdentifier()).add(new EventToCheck(Type.ENDACT, eInfo.getActivity().getIdentifier(), nextTs));
				break;
			case ENDACT:
				ev = new EventToCheck(Type.ENDACT, eInfo.getActivity().getIdentifier(), eInfo.getTs());
				printResult(futureFlow.get(eInfo.getElem().getIdentifier()).remove(ev), "unexpected event!"); 
				SingleFlowNode f = flow.search(eInfo.getSf().getFlow().getIdentifier());
				if (f.next != null)
					f.next.add2FutureFlow(eInfo.getElem().getIdentifier(), eInfo.getTs());
				break;
			}
		}
		else if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			switch(eInfo.getType()) {
			case START:
				flow.add2FutureFlow(eInfo.getElem().getIdentifier(), eInfo.getTs());
				break;
			case FINISH:
				if (detailed)
					System.out.print(eInfo + "...\t");
				printResult(futureFlow.get(eInfo.getElem().getIdentifier()).isEmpty(), "unexpected end: not all the events were executed!"); 
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println();
			notifyResult(ok);			
		}
		else if (info instanceof SimulationStartInfo) {
			System.out.println("--------------------------------------------------");
			System.out.println("Checking " + getSimul().getDescription());
			System.out.println();
		}
	}

	private void printResult(boolean result, String errMsg) {
		if (!detailed) {
			if (!result)
				ok = false;
		}
		else if (result)
			System.out.println("PASSED");
		else {
			System.out.println("ERROR " + errMsg);
			ok = false;
		}
	}
	
	private FlowNode createFlow(Flow f) {
		if (f instanceof SingleFlow) {
			SingleFlow sf = (SingleFlow) f;
			int actId = sf.getActivity().getIdentifier();
			if (sf.getSuccessor() == null)
				return new SingleFlowNode(sf.getIdentifier(), actId, getSimul()
						.simulationTime2Long(durations[actId]), null);
			return new SingleFlowNode(sf.getIdentifier(), actId, getSimul().simulationTime2Long(
					durations[actId]), createFlow(sf.getSuccessor()));
		}
		if (f instanceof ParallelFlow) {
			ParallelFlow pf = (ParallelFlow) f;
			GroupFlowNode gf = new GroupFlowNode();
			for (Flow aux : pf.getSuccessorList())
				gf.addNode(createFlow(aux));
			return gf;
		}
		
		return null;
	}

	interface FlowNode {
		void add2FutureFlow(int eId, double ts);
		SingleFlowNode search(int id);
	}

	class SingleFlowNode implements FlowNode {
		int actId;
		double duration;
		FlowNode next;
		int id;

		/**
		 * @param actId
		 * @param duration
		 * @param next
		 */
		public SingleFlowNode(int id, int actId, double duration, FlowNode next) {
			this.id = id;
			this.actId = actId;
			this.duration = duration;
			this.next = next;
		}

		@Override
		public void add2FutureFlow(int eId, double ts) {
			futureFlow.get(eId).add(
					new EventToCheck(ElementActionInfo.Type.REQACT, actId, ts));
		}

		@Override
		public SingleFlowNode search(int id) {
			if (this.id == id)
				return this;
			if (next != null)
				return next.search(id);
			return null;
		}
	}

	class GroupFlowNode implements FlowNode {
		ArrayDeque<FlowNode> list;

		public GroupFlowNode() {
			super();
			list = new ArrayDeque<FlowNode>();
		}

		public void addNode(FlowNode f) {
			list.add(f);
		}
		
		public void add2FutureFlow(int eId, double ts) {
			for (FlowNode f : list)
				f.add2FutureFlow(eId, ts);
		}

		@Override
		public SingleFlowNode search(int id) {
			for (FlowNode f : list) {
				SingleFlowNode sf = f.search(id);
				if (sf != null)
					return sf;
			}
			return null;
		}
	}

	class EventToCheck implements Comparable<EventToCheck> {
		final ElementActionInfo.Type type;
		final int actId;
		final double ts;

		/**
		 * @param type
		 * @param ts
		 */
		public EventToCheck(Type type, int actId, double ts) {
			this.type = type;
			this.actId = actId;
			this.ts = ts;
		}

		@Override
		public int compareTo(EventToCheck o) {
			if (type.ordinal() > o.type.ordinal())
				return 1;
			if (type.ordinal() < o.type.ordinal())
				return -1;
			if (actId > o.actId)
				return 1;
			if (actId < o.actId)
				return -1;
			return 0;
		}
	}
}
