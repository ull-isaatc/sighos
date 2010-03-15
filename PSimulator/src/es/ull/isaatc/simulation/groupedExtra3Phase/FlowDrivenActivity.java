/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtra3Phase;

import es.ull.isaatc.simulation.common.FlowDrivenActivityWorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.StructuredFlow;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.groupedExtra3Phase.flow.BasicFlow;
import es.ull.isaatc.simulation.groupedExtra3Phase.flow.FinalizerFlow;
import es.ull.isaatc.simulation.groupedExtra3Phase.flow.InitializerFlow;

// FIXME: No funciona porque no cuadra con las dos etapas de este esquema. Se puede resolver la parte de coger los
// recursos del FlowDrivenActivity, pero no los de la actividad que haya en su flujo interno
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class FlowDrivenActivity extends Activity implements es.ull.isaatc.simulation.common.FlowDrivenActivity {
	/** 
	 * An artificially created final node. This flow informs the flow-driven
	 * activity that it has being finalized.
	 */
	private final BasicFlow virtualFinalFlow = new BasicFlow(simul) {

		public void request(WorkThread wThread) {
			Element elem = wThread.getElement();
			elem.addEvent(elem.new FinishFlowEvent(elem.getTs(), wThread.getParent().getWorkItem().getFlow(), wThread.getParent()));
		}

		@Override
		public void addPredecessor(Flow newFlow) {}

		@Override
		public void link(Flow successor) {}

		@Override
		public void setRecursiveStructureLink(StructuredFlow parent) {}		
	};


	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public FlowDrivenActivity(int id, Simulation simul, String description) {
		super(id, simul, description);
	}

	/**
	 * @param id
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public FlowDrivenActivity(int id, Simulation simul, String description, int priority) {
		super(id, simul, description, priority);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.groupedExtraThreaded.Activity#carryOut(es.ull.isaatc.simulation.groupedExtraThreaded.WorkItem)
	 */
	@Override
	public void carryOut(WorkItem wItem) {
		Element elem = wItem.getElement();
		wItem.getFlow().afterStart(elem);
		wItem.catchResources();

		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
		elem.debug("Starts\t" + this + "\t" + description);
		InitializerFlow initialFlow = ((FlowDrivenActivity.ActivityWorkGroup)wItem.getExecutionWG()).getInitialFlow();
		initialFlow.request(wItem.getWorkThread().getInstanceDescendantWorkThread(initialFlow));
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.groupedExtraThreaded.Activity#finish(es.ull.isaatc.simulation.groupedExtraThreaded.WorkItem)
	 */
	@Override
	public boolean finish(WorkItem wItem) {
		Element elem = wItem.getElement();

		wItem.releaseCaughtResources();

//		int[] order = RandomPermutation.nextPermutation(amList.size());
//		for (int ind : order) {
//			ActivityManager am = amList.get(ind);
//			am.waitSemaphore();
//			am.availableResource();
//			am.signalSemaphore();
//		}

		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + description);
        return true;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.groupedExtraThreaded.Activity#request(es.ull.isaatc.simulation.groupedExtraThreaded.WorkItem)
	 */
	@Override
	public void request(WorkItem wItem) {
		Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);
		queueAdd(wItem); // The element is introduced in the queue
		manager.notifyElement(wItem);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "FACT";
	}

	
	/**
	 * All elements are valid to perform a flow-driven activity.
	 * @param wItem Work item requesting this activity 
	 */
	@Override
	public boolean validElement(WorkItem wItem) {
		return true;
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow,
			es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.common.WorkGroup wg) {
		ActivityWorkGroup aWg = new ActivityWorkGroup(id, initFlow, finalFlow, priority, (WorkGroup)wg);
		workGroupTable.add(aWg);
		return aWg;
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow,
			es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.common.WorkGroup wg, Condition cond) {
		ActivityWorkGroup aWg = new ActivityWorkGroup(id, initFlow, finalFlow, priority, (WorkGroup)wg, cond);
		workGroupTable.add(aWg);
		return aWg;
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow, 
			es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, es.ull.isaatc.simulation.common.WorkGroup wg) {
		return addWorkGroup(initFlow, finalFlow, 0, wg);
	}

	@Override
	public FlowDrivenActivityWorkGroup addWorkGroup(es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow, 
			es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, es.ull.isaatc.simulation.common.WorkGroup wg, Condition cond) {
		return addWorkGroup(initFlow, finalFlow, 0, wg, cond);
	}

	public class ActivityWorkGroup extends Activity.ActivityWorkGroup implements FlowDrivenActivityWorkGroup {
		final protected InitializerFlow initFlow;
		final protected FinalizerFlow finalFlow;
		
		protected ActivityWorkGroup(int id, es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow, 
				es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, int priority, WorkGroup wg) {
			super(id, priority, wg);
			this.initFlow = (InitializerFlow)initFlow;
			this.finalFlow = (FinalizerFlow)finalFlow;
			this.finalFlow.link(virtualFinalFlow);
		}

		protected ActivityWorkGroup(int id, es.ull.isaatc.simulation.common.flow.InitializerFlow initFlow, 
				es.ull.isaatc.simulation.common.flow.FinalizerFlow finalFlow, int priority, WorkGroup wg, Condition cond) {
			super(id, priority, wg, cond);
			this.initFlow = (InitializerFlow)initFlow;
			this.finalFlow = (FinalizerFlow)finalFlow;
			this.finalFlow.link(virtualFinalFlow);
		}

		@Override
		public FinalizerFlow getFinalFlow() {
			return finalFlow;
		}
		@Override
		public InitializerFlow getInitialFlow() {
			return initFlow;
		}

	    @Override
	    public String toString() {
	    	return new String(super.toString());
	    }
	}
}
