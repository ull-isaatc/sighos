/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.StaticPartialJoinMultipleInstancesFlow;

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
    	WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt);

    	StaticPartialJoinMultipleInstancesFlow root = (StaticPartialJoinMultipleInstancesFlow)factory.getFlowInstance("StaticPartialJoinMultipleInstancesFlow", 6, 4);
    	root.addBranch(act0);
    	root.link(act1);
    	
        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
