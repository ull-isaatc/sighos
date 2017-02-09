package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.PartialJoinFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 31. Banco
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP31Simulation extends WFPTestSimulationFactory {

	public WFP31Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP31: Blocking Partial Join. EjBanco", detailed);
	}

	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Director");
	   	
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow<?,?> act0_0 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow<?,?> act0_1 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow<?,?> act0_2 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow<?,?> act1 = getDefActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);       
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");

        PartialJoinFlow part1 = (PartialJoinFlow)factory.getFlowInstance("PartialJoinFlow", 2);
        
        root.link(act0_0);
        root.link(act0_1);
        root.link(act0_2);
        act0_0.link(part1);
        act0_1.link(part1);
        act0_2.link(part1);
        part1.link(act1);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}

}
