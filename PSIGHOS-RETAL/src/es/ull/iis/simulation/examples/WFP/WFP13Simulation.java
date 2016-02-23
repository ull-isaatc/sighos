/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.core.flow.SynchronizedMultipleInstanceFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 13. Multiple Instances with a priori design-time knowledge
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP13Simulation extends WFPTestSimulationFactory {
	final static int RES = 6;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP13Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP13: Multiple Instances with a priori design-time knowledge", detailed);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Director");
    	WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Sign Annual Report", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt0);

		SynchronizedMultipleInstanceFlow root = (SynchronizedMultipleInstanceFlow)factory.getFlowInstance("SynchronizedMultipleInstanceFlow", 6);
    	root.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", act0));
    	root.link(factory.getFlowInstance("SingleFlow", act1));

    	getDefGenerator(getDefElementType("ET0"), root);
	}

}
