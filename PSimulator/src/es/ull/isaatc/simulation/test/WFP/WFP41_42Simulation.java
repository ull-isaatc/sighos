/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.ThreadMergeFlow;
import es.ull.isaatc.simulation.common.flow.ThreadSplitFlow;

/**
 * WFP 41 - 42. 
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP41_42Simulation extends WFPTestSimulationFactory {

	public WFP41_42Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP41_42: Thread Split-Merge", detailed);
	}

	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Program Chair"); 
		ResourceType rt1 = getDefResourceType("Peer Referee");
		
		WorkGroup wg0 = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
		WorkGroup wg1 = factory.getWorkGroupInstance(1, new ResourceType[] {rt1}, new int[] {1});

    	getDefResource("PC1", rt0);
    	getDefResource("Ref0", rt1);
    	getDefResource("Ref1", rt1);
    	getDefResource("Ref2", rt1);
		
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Confirm paper receival", 0, wg0, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Independent Peer review", 6, wg1, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Notify authors", 0, wg0, false);
		
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);

        ThreadSplitFlow split = (ThreadSplitFlow)factory.getFlowInstance(10, "ThreadSplitFlow", 3);
		root.link(split);
        SingleFlow peer = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
		split.link(peer);
        ThreadMergeFlow merge = (ThreadMergeFlow)factory.getFlowInstance(10, "ThreadMergeFlow", 3);
		peer.link(merge);
		merge.link((SingleFlow)factory.getFlowInstance(1, "SingleFlow", act2));

        getDefGenerator(getDefElementType("ET0"), root);
	}
}
