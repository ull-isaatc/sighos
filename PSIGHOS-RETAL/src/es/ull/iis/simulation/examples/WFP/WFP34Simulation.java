/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
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
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Sign Annual Report", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt);

    	StaticPartialJoinMultipleInstancesFlow root = (StaticPartialJoinMultipleInstancesFlow)factory.getFlowInstance("StaticPartialJoinMultipleInstancesFlow", 6, 4);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
    	root.addBranch(sin1);
    	root.link(sin2);
    	
        getDefGenerator(getDefElementType("ET0"), root);
		
	}
}
