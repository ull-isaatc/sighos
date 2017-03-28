/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.InterleavedRoutingFlow;

/**
 * WFP 40. 
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP40Simulation extends WFPTestSimulationFactory {
	static final int RES = 4;

	public WFP40Simulation(int id, boolean detailed) {
		super(id, "WFP40: Interleaved Routing", detailed);
	}

	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);    	
		ResourceType rt = getDefResourceType("Technician");
    	WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("check oil", wg);
    	ActivityFlow act1 = getDefActivity("examine main unit", wg);
    	ActivityFlow act2 = getDefActivity("review warranty", wg);
    	ActivityFlow act3 = getDefActivity("final check", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt);
    	
    	InterleavedRoutingFlow root = new InterleavedRoutingFlow(simul);
    	root.addBranch(act0);
    	root.addBranch(act1);
    	root.addBranch(act2);
    	root.link(act3);

        getDefGenerator(getDefElementType("ET0"), root);
    	return simul;
		
	}
}
