package es.ull.iis.simulation.parallel;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.parallel.flow.FinalizerFlow;
import es.ull.iis.simulation.parallel.flow.InitializerFlow;

public class FlowDrivenActivityWorkGroup extends ActivityWorkGroup implements es.ull.iis.simulation.core.FlowDrivenActivityWorkGroup {
	final protected InitializerFlow initFlow;
	final protected FinalizerFlow finalFlow;
	
	protected FlowDrivenActivityWorkGroup(Activity flowDrivenActivity, int id, es.ull.iis.simulation.core.flow.InitializerFlow initFlow, 
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, int priority, WorkGroup wg) {
		super(flowDrivenActivity, id, priority, wg);
		this.initFlow = (InitializerFlow)initFlow;
		this.finalFlow = (FinalizerFlow)finalFlow;
		this.finalFlow.link(flowDrivenActivity.getVirtualFinalFlow());
	}

	protected FlowDrivenActivityWorkGroup(Activity flowDrivenActivity, int id, es.ull.iis.simulation.core.flow.InitializerFlow initFlow, 
			es.ull.iis.simulation.core.flow.FinalizerFlow finalFlow, int priority, WorkGroup wg, Condition cond) {
		super(flowDrivenActivity, id, priority, wg, cond);
		this.initFlow = (InitializerFlow)initFlow;
		this.finalFlow = (FinalizerFlow)finalFlow;
		this.finalFlow.link(flowDrivenActivity.getVirtualFinalFlow());
	}

	@Override
	public FinalizerFlow getFinalFlow() {
		return finalFlow;
	}
	@Override
	public InitializerFlow getInitialFlow() {
		return initFlow;
	}
}