package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SimpleMergeFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;


class WFP05CheckView extends CheckElementActionsView {
	public WFP05CheckView(Simulation simul) {
		this(simul, true);
	}

	public WFP05CheckView(Simulation simul, boolean detailed) {
		super(simul, "Checking WFP5...", detailed);
		
		ElementReferenceInfos [] ref;
		ref = new ElementReferenceInfos[3];
		refRequests.put(0.0, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(3);
		ref[0].add(1);
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref[1].add(1);
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(3);
		ref[2].add(1);
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.446E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.47E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.52E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(3);
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[0].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref[1].add(1);
		ref[1].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref[0].add(3);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.544E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.62E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.693E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.767E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[0].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(3);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[1].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.47E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.52E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(3);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.544E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.62E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.693E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.767E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.84E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
	}
	
}

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
        
        WorkGroup wgEBob = factory.getWorkGroupInstance(0, new ResourceType[] {rt0, rt2}, new int[] {1, 1});
        WorkGroup wgED9 = factory.getWorkGroupInstance(1, new ResourceType[] {rt1, rt2}, new int[] {1, 1});
        WorkGroup wgFacturacion = factory.getWorkGroupInstance(2, new ResourceType[] {rt3}, new int[] {1});
        WorkGroup wgEH8 = factory.getWorkGroupInstance(3, new ResourceType[] {rt4, rt2}, new int[] {1, 1});
        
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

        ParallelFlow root = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SimpleMergeFlow simme1 = (SimpleMergeFlow)factory.getFlowInstance(11, "SimpleMergeFlow");        
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance(3, "SingleFlow", act3);
        
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
