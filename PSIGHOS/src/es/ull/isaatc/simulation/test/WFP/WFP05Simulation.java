package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.ParallelFlow;
import es.ull.isaatc.simulation.core.flow.SimpleMergeFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 5. Example 1: Excavaciones
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP05Simulation extends WFPTestSimulationFactory {
	
	public WFP05Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP5: Simple Merge. EjExcavaciones", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt0 = getDefResourceType("Excavadora bobcat");
        ResourceType rt1 = getDefResourceType("Excavadora D9");
        ResourceType rt2 = getDefResourceType("Conductor");
        ResourceType rt3 = getDefResourceType("Comercial");
        ResourceType rt4 = getDefResourceType("Excavadora H8");
        
        WorkGroup wgEBob = factory.getWorkGroupInstance(new ResourceType[] {rt0, rt2}, new int[] {1, 1});
        WorkGroup wgED9 = factory.getWorkGroupInstance(new ResourceType[] {rt1, rt2}, new int[] {1, 1});
        WorkGroup wgFacturacion = factory.getWorkGroupInstance(new ResourceType[] {rt3}, new int[] {1});
        WorkGroup wgEH8 = factory.getWorkGroupInstance(new ResourceType[] {rt4, rt2}, new int[] {1, 1});
        
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Excavacion bobcat", wgEBob, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Excavacion D9", wgED9, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Facturacion", 2, wgFacturacion, false);
        TimeDrivenActivity act3 = getDefTimeDrivenActivity("Excavacion H8", 1, wgEH8, false);
        
        getDefResource("Bobcat1", rt0);
        getDefResource("D91", rt1);
        getDefResource("D92", rt1);
        getDefResource("Conductor1", rt2);
        getDefResource("Conductor2", rt2);
        getDefResource("Conductor3", rt2);
        getDefResource("Comercial1", rt3);
        getDefResource("H81", rt4);

        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SimpleMergeFlow simme1 = (SimpleMergeFlow)factory.getFlowInstance("SimpleMergeFlow");        
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act3);
        
        root.link(sin1);
        root.link(sin2);     
        root.link(sin4);     
        sin1.link(simme1);
        sin2.link(simme1);
        sin4.link(simme1);
        simme1.link(sin3);
        
        getDefGenerator(getDefElementType("Excavacion"), root);
//        getSimulation().addInfoReceiver(new WFP05CheckView(getSimulation(), detailed));
    }
	
}
