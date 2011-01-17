/**
 * 
 */
package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.ParallelFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.core.flow.ThreadSplitFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 12. Multiple Instances without Synchronization
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP12Simulation extends WFPTestSimulationFactory {
	static final int RES = 5;

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP12Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP12: Multiple Instances without Synchronization", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Policeman");
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Receive Infringment", 1, wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Issue-Infringment-Notice", 5, wg, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Coffee", 1, wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt0);
    	
    	SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        ParallelFlow pf = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
    	root.link(pf);
    	ThreadSplitFlow tsf = (ThreadSplitFlow)factory.getFlowInstance("ThreadSplitFlow", 3);
    	tsf.link(factory.getFlowInstance("SingleFlow", act1));
    	pf.link(tsf);
    	SingleFlow finalSf = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
    	pf.link(finalSf);
    	finalSf.link(root);
    	
        getDefGenerator(getDefElementType("ET0"), root);    	
	}

}
