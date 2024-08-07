/**
 * 
 */
package es.ull.iis.simulation.tests.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.StaticPartialJoinMultipleInstancesFlow;

/**
 * WFP 34. 
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP34Simulation extends WFPTestSimulation {
	final static int RES = 6;

	public WFP34Simulation(int id, TestWFP.CommonArguments args) {
		super(id, "WFP34: Static Partial Join for Multiple Instances", args);
	}

	@Override
	protected void createModel() {
		ResourceType rt = getDefResourceType("Director");
    	WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt);

    	StaticPartialJoinMultipleInstancesFlow root = new StaticPartialJoinMultipleInstancesFlow(this, 6, 4);
    	root.addBranch(act0);
    	root.link(act1);
    	
        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
