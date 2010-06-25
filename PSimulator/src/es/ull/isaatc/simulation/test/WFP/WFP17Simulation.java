/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.InterleavedParallelRoutingFlow;

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
	 * @param description
	 * @param detailed
	 */
	public WFP17Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP17: Interleaved Parallel Routing", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt = getDefResourceType("RT");
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});

    	ArrayList<Activity> acts = new ArrayList<Activity>();
    	acts.add(getDefTimeDrivenActivity("A", wg));
    	acts.add(getDefTimeDrivenActivity("B", wg));
    	acts.add(getDefTimeDrivenActivity("C", wg));
    	acts.add(getDefTimeDrivenActivity("D", wg));
    	acts.add(getDefTimeDrivenActivity("E", wg));
    	acts.add(getDefTimeDrivenActivity("F", wg));
    	TimeDrivenActivity finalAct = getDefTimeDrivenActivity("G", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt);
    	
    	// Dependencies
    	ArrayList<Activity[]> dep = new ArrayList<Activity[]>();
    	dep.add(new Activity[] {acts.get(0), acts.get(1)});
    	dep.add(new Activity[] {acts.get(0), acts.get(2)});
    	dep.add(new Activity[] {acts.get(2), acts.get(3), acts.get(4)});
    	dep.add(new Activity[] {acts.get(1), acts.get(4)});
    	
    	InterleavedParallelRoutingFlow root = (InterleavedParallelRoutingFlow)factory.getFlowInstance("InterleavedParallelRoutingFlow", acts, dep);
    	root.link(factory.getFlowInstance("SingleFlow", finalAct));

    	getDefGenerator(getDefElementType("ET0"), root);
	}
}
