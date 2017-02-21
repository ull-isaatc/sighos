/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.InterleavedParallelRoutingFlow;

/**
 * WFP 17. Interleaved Parallel Routing
 * @author Yeray Callero
 * @author Iván Castilla
 * Creates an interleaved paralell routing example with the following activities: A, B, C, D, E, F;
 * and the following dependencies: A -> B, A -> C, C -> D -> E, B -> E. F has no dependencies 
 *
 */
public class WFP17Simulation extends WFPTestSimulationFactory {
	final static int RES = 6;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP17Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP17: Interleaved Parallel Routing", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt = getDefResourceType("RT");
    	WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});

    	ArrayList<ActivityFlow> acts = new ArrayList<ActivityFlow>();
    	acts.add(getDefActivity("A", wg));
    	acts.add(getDefActivity("B", wg));
    	acts.add(getDefActivity("C", wg));
    	acts.add(getDefActivity("D", wg));
    	acts.add(getDefActivity("E", wg));
    	acts.add(getDefActivity("F", wg));
    	ActivityFlow finalAct = getDefActivity("G", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt);
    	
    	// Dependencies
    	ArrayList<ActivityFlow[]> dep = new ArrayList<ActivityFlow[]>();
    	dep.add(new ActivityFlow[] {acts.get(0), acts.get(1)});
    	dep.add(new ActivityFlow[] {acts.get(0), acts.get(2)});
    	dep.add(new ActivityFlow[] {acts.get(2), acts.get(3), acts.get(4)});
    	dep.add(new ActivityFlow[] {acts.get(1), acts.get(4)});
    	
    	InterleavedParallelRoutingFlow root = (InterleavedParallelRoutingFlow)factory.getFlowInstance("InterleavedParallelRoutingFlow", acts, dep);
    	root.link(factory.getFlowInstance("SingleFlow", finalAct));

    	getDefGenerator(getDefElementType("ET0"), root);
	}
}
