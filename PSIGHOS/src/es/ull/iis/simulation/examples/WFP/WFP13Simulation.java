/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

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
public class WFP13Simulation extends WFPTestSimulationFactory {
	final static int RES = 6;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP13Simulation(int id, boolean detailed) {
		super(id, "WFP13: Multiple Instances with a priori design-time knowledge", detailed);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);    	
		ResourceType rt0 = getDefResourceType("Director");
    	WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Sign Annual Report", wg);
    	ActivityFlow act1 = getDefActivity("Check acceptance", wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("Director" + i, rt0);

		SynchronizedMultipleInstanceFlow root = new SynchronizedMultipleInstanceFlow(simul, 6);
    	root.addBranch(act0);
    	root.link(act1);

    	getDefGenerator(getDefElementType("ET0"), root);
    	return simul;
	}

}
