/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
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

	public WFP34Simulation(int id, boolean detailed) {
		super(id, "WFP34: Static Partial Join for Multiple Instances", detailed);
	}

	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);    	
		ResourceType rt = getDefResourceType("Director");
    	WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt);

    	StaticPartialJoinMultipleInstancesFlow root = new StaticPartialJoinMultipleInstancesFlow(simul, 6, 4);
    	root.addBranch(act0);
    	root.link(act1);
    	
        getDefGenerator(getDefElementType("ET0"), root);
    	return simul;
		
	}
}
