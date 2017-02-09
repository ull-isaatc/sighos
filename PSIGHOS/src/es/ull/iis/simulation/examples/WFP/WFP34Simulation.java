/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.StaticPartialJoinMultipleInstancesFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 34. 
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP34Simulation extends WFPTestSimulationFactory {
	final static int RES = 6;

	public WFP34Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP34: Static Partial Join for Multiple Instances", detailed);
	}

	@Override
	protected void createModel() {
    	ResourceType rt = getDefResourceType("Director");
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
    	
    	ActivityFlow<?,?> act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow<?,?> act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt);

    	StaticPartialJoinMultipleInstancesFlow root = (StaticPartialJoinMultipleInstancesFlow)factory.getFlowInstance("StaticPartialJoinMultipleInstancesFlow", 6, 4);
    	root.addBranch(act0);
    	root.link(act1);
    	
        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
