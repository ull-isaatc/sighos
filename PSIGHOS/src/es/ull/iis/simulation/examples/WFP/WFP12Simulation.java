/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.core.flow.ThreadSplitFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
	 * @param detailed
	 */
	public WFP12Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP12: Multiple Instances without Synchronization", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Policeman");
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
    	Activity act0 = getDefActivity("Receive Infringment", 1, wg);
    	Activity act1 = getDefActivity("Issue-Infringment-Notice", 5, wg, false);
    	Activity act2 = getDefActivity("Coffee", 1, wg);
    	
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
