/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ThreadMergeFlow;
import es.ull.iis.simulation.model.flow.ThreadSplitFlow;

/**
 * WFP 41 - 42. 
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP41_42Simulation extends WFPTestSimulationFactory {

	public WFP41_42Simulation(int id, boolean detailed) {
		super(id, "WFP41_42: Thread Split-Merge", detailed);
	}

	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);		
		ResourceType rt0 = getDefResourceType("Program Chair"); 
		ResourceType rt1 = getDefResourceType("Peer Referee");
		
		WorkGroup wg0 = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
		WorkGroup wg1 = new WorkGroup(simul, new ResourceType[] {rt1}, new int[] {1});

    	getDefResource("PC1", rt0);
    	getDefResource("Ref0", rt1);
    	getDefResource("Ref1", rt1);
    	getDefResource("Ref2", rt1);
		
    	ActivityFlow act0 = getDefActivity("Confirm paper receival", 0, wg0, false);
    	ActivityFlow act1 = getDefActivity("Independent Peer review", 6, wg1, false);
    	ActivityFlow act2 = getDefActivity("Notify authors", 0, wg0, false);
		
        ThreadSplitFlow split = new ThreadSplitFlow(simul, 3);
		act0.link(split);
		split.link(act1);
        ThreadMergeFlow merge = new ThreadMergeFlow(simul, 3);
        act1.link(merge);
		merge.link(act2);

        getDefGenerator(getDefElementType("ET0"), act0);
    	return simul;
	}
}
