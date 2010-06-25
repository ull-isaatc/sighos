package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.PartialJoinFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

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

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("AprobarCuenta", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);       
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);

        PartialJoinFlow part1 = (PartialJoinFlow)factory.getFlowInstance("PartialJoinFlow", 2);
        
        root.link(sin1);
        root.link(sin2);
        root.link(sin3);
        sin1.link(part1);
        sin2.link(part1);
        sin3.link(part1);
        part1.link(sin4);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}

}
