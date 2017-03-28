/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.ThreadSplitFlow;

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
	public WFP12Simulation(int id, boolean detailed) {
		super(id, "WFP12: Multiple Instances without Synchronization", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);    	
		ResourceType rt0 = getDefResourceType("Policeman");
    	WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Receive Infringment", 1, wg);
    	ActivityFlow act1 = getDefActivity("Issue-Infringment-Notice", 5, wg, false);
    	ActivityFlow act2 = getDefActivity("Coffee", 1, wg);
    	
    	for (int i = 0; i < RES; i++)
    		getDefResource("RES" + i, rt0);
    	
        ParallelFlow pf = new ParallelFlow(simul);
    	act0.link(pf);
    	ThreadSplitFlow tsf = new ThreadSplitFlow(simul, 3);
    	tsf.link(act1);
    	pf.link(tsf);
    	pf.link(act2).link(act0);
    	
        getDefGenerator(getDefElementType("ET0"), act0);    	
        return simul;
	}

}
