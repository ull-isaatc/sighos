/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.InterleavedRoutingFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
    	
    	Activity act0 = getDefActivity("check oil", wg);
    	Activity act1 = getDefActivity("examine main unit", wg);
    	Activity act2 = getDefActivity("review warranty", wg);
    	Activity act3 = getDefActivity("final check", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt);
    	
    	InterleavedRoutingFlow root = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act3);
    	root.addBranch(sin1);
    	root.addBranch(sin2);
    	root.addBranch(sin3);
    	root.link(sin4);

        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
