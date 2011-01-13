/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.core.flow.ThreadMergeFlow;
import es.ull.isaatc.simulation.core.flow.ThreadSplitFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

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
		
		WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
		WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});

    	getDefResource("PC1", rt0);
    	getDefResource("Ref0", rt1);
    	getDefResource("Ref1", rt1);
    	getDefResource("Ref2", rt1);
		
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Confirm paper receival", 0, wg0, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Independent Peer review", 6, wg1, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Notify authors", 0, wg0, false);
		
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);

        ThreadSplitFlow split = (ThreadSplitFlow)factory.getFlowInstance("ThreadSplitFlow", 3);
		root.link(split);
        SingleFlow peer = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
		split.link(peer);
        ThreadMergeFlow merge = (ThreadMergeFlow)factory.getFlowInstance("ThreadMergeFlow", 3);
		peer.link(merge);
		merge.link((SingleFlow)factory.getFlowInstance("SingleFlow", act2));

        getDefGenerator(getDefElementType("ET0"), root);
	}
}
