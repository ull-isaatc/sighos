/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * WFP 40. 
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP40Simulation extends WFPTestSimulationFactory {
	static final int RES = 4;

	public WFP40Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP40: Interleaved Routing", detailed);
	}

	@Override
	protected void createModel() {
    	ResourceType rt = getDefResourceType("Technician");
    	WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("check oil", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("examine main unit", wg);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("review warranty", wg);
    	TimeDrivenActivity act3 = getDefTimeDrivenActivity("final check", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt);
    	
    	InterleavedRoutingFlow root = (InterleavedRoutingFlow)factory.getFlowInstance(10, "InterleavedRoutingFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance(3, "SingleFlow", act3);
    	root.addBranch(sin1);
    	root.addBranch(sin2);
    	root.addBranch(sin3);
    	root.link(sin4);

        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
