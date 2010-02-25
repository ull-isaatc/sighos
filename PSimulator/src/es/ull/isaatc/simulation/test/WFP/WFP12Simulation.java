/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.ThreadSplitFlow;

/**
 * WFP 12. Multiple Instances without Synchronization
 * @author Yeray Callero
 * @author Iv�n Castilla
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
    	WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Receive Infringment", 1, wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Issue-Infringment-Notice", 5, wg, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Coffee", 1, wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt0);
    	
    	SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        ParallelFlow pf = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
    	root.link(pf);
    	ThreadSplitFlow tsf = (ThreadSplitFlow)factory.getFlowInstance(11, "ThreadSplitFlow", 3);
    	tsf.link(factory.getFlowInstance(1, "SingleFlow", act1));
    	pf.link(tsf);
    	SingleFlow finalSf = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
    	pf.link(finalSf);
    	finalSf.link(root);
    	
        getDefGenerator(getDefElementType("ET0"), root);    	
	}

}