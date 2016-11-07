/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.core.flow.ThreadMergeFlow;
import es.ull.iis.simulation.core.flow.ThreadSplitFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
		
    	Activity act0 = getDefActivity("Confirm paper receival", 0, wg0, false);
    	Activity act1 = getDefActivity("Independent Peer review", 6, wg1, false);
    	Activity act2 = getDefActivity("Notify authors", 0, wg0, false);
		
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
