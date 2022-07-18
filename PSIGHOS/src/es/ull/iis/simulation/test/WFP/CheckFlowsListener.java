/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.flow.ActionFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.SingleSuccessorFlow;

/**
 * Checks the elements created and finished during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckFlowsListener extends CheckerListener {
	private final NodeInfo[] checkStructure;
	/**
	 * 
	 * @param simul The simulation to view
	 * @param elements An array where each position is an element type, and each value is the amount of 
	 * elements which should be created per type.
	 */
	public CheckFlowsListener(int nElem) {
		super("Activity checker ");
		this.checkStructure = new NodeInfo[nElem];
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
	}

	private static final NodeInfo buildCheckStructure(InitializerFlow initFlow) {
		if (initFlow instanceof SingleSuccessorFlow) {
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static final NodeInfo createNode(Flow flow, boolean optional, int minIterations, int maxIterations) {
		if (flow instanceof ActionFlow) {
			return new NodeLeafInfo((ActionFlow)flow, optional, minIterations, maxIterations);
		}
		return new NodeBranchInfo(optional, minIterations, maxIterations);
	}
	private static interface NodeInfo {
		public boolean isOptional();
		public int getMaxIterations();
		public int getMinIterations();
	}
	
	
	private static class NodeLeafInfo implements NodeInfo {
		private final ActionFlow flow;
		private final boolean optional;
		private final int minIterations;
		private final int maxIterations;
		
		/**
		 * @param flow
		 * @param optional
		 * @param minIterations
		 * @param maxIterations
		 */
		public NodeLeafInfo(ActionFlow flow, boolean optional, int minIterations, int maxIterations) {
			super();
			this.flow = flow;
			this.optional = optional;
			this.minIterations = minIterations;
			this.maxIterations = maxIterations;
		}

		/**
		 * @return the flow
		 */
		@SuppressWarnings("unused")
		public ActionFlow getFlow() {
			return flow;
		}

		public boolean isOptional() {
			return optional;
		}

		public int getMinIterations() {
			return minIterations;
		}

		public int getMaxIterations() {
			return maxIterations;
		}
	}
	
	private static class NodeBranchInfo implements NodeInfo {
		private final boolean optional;
		private final int minIterations;
		private final int maxIterations;
		private final ArrayList<NodeInfo> next;
		private final TreeMap<NodeInfo, Integer> components;
		
		/**
		 * @param optional
		 * @param minIterations
		 * @param maxIterations
		 */
		public NodeBranchInfo(boolean optional, int minIterations, int maxIterations) {
			this.optional = optional;
			this.minIterations = minIterations;
			this.maxIterations = maxIterations;
			next = new ArrayList<NodeInfo>();
			components = new TreeMap<NodeInfo, Integer>();
		}
		
		@SuppressWarnings("unused")
		public void addComponent(final NodeInfo info, final int maxN) {
			components.put(info, maxN);
		}
		
		@SuppressWarnings("unused")
		public void link(final NodeInfo nextInfo) {
			next.add(nextInfo);
		}

		/**
		 * @return the next
		 */
		@SuppressWarnings("unused")
		public ArrayList<NodeInfo> getNext() {
			return next;
		}

		/**
		 * @return the components
		 */
		@SuppressWarnings("unused")
		public TreeMap<NodeInfo, Integer> getComponents() {
			return components;
		}

		public boolean isOptional() {
			return optional;
		}

		public int getMinIterations() {
			return minIterations;
		}

		public int getMaxIterations() {
			return maxIterations;
		}
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			final ElementInfo eInfo = (ElementInfo)info;
			final Element elem = eInfo.getElement();
			switch (eInfo.getType()) {
			case FINISH:
				break;
			case START:
				checkStructure[elem.getIdentifier()] = buildCheckStructure(elem.getFlow());
				break;
			default:
				break;
			}
		}
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			switch(eInfo.getType()) {
			case ACQ:
				break;
			case END:
				break;
			case REL:
				break;
			case REQ:
				break;
			case START:
				break;
			case RESACT:
			case INTACT:
			default:
				break;
			
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType()))  {
			}
		}
		
	}
}
