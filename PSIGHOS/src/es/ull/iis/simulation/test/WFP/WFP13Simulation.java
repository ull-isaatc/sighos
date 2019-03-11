/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.SynchronizedMultipleInstanceFlow;

/**
 * WFP 13. Multiple Instances with a priori design-time knowledge
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP13Simulation extends WFPTestSimulation {
	final static int RES = 6;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP13Simulation(int id) {
		super(id, "WFP13: Multiple Instances with a priori design-time knowledge");
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Director");
    	WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt0);

		SynchronizedMultipleInstanceFlow root = new SynchronizedMultipleInstanceFlow(this, 6);
    	root.addBranch(act0);
    	root.link(act1);

    	getDefGenerator(getDefElementType("ET0"), root);
	}

}
