package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SimpleMergeFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;


class WFP05CheckView extends CheckElementActionsView {
	public WFP05CheckView(WFP05Simulation simul) {
		this(simul, true);
	}

	public WFP05CheckView(WFP05Simulation simul, boolean detailed) {
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
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP05Simulation extends WFPTestSimulation {
	
	public WFP05Simulation(int id, boolean detailed) {
		super(id, "WFP5: Simple Merge. EjExcavaciones", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt0 = getDefResourceType("Excavadora bobcat");
        ResourceType rt1 = getDefResourceType("Excavadora D9");
        ResourceType rt2 = getDefResourceType("Conductor");
        ResourceType rt3 = getDefResourceType("Comercial");
        ResourceType rt4 = getDefResourceType("Excavadora H8");
        
        WorkGroup wgEBob = new WorkGroup();
        wgEBob.add(rt0, 1);
        wgEBob.add(rt2, 1);
        WorkGroup wgED9 = new WorkGroup();
        wgED9.add(rt1, 1);
        wgED9.add(rt2, 1);
        WorkGroup wgFacturacion = new WorkGroup(rt3, 1);
        WorkGroup wgEH8 = new WorkGroup();
        wgEH8.add(rt4, 1);
        wgEH8.add(rt2, 1);
        
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

        ParallelFlow root = new ParallelFlow(this);
        SingleFlow sin1 = new SingleFlow(this, act0);
        SingleFlow sin2 = new SingleFlow(this, act1);
        SimpleMergeFlow simme1 = new SimpleMergeFlow(this);        
        SingleFlow sin3 = new SingleFlow(this, act2);
        SingleFlow sin4 = new SingleFlow(this, act3);
        
        root.link(sin1);
        root.link(sin2);     
        root.link(sin4);     
        sin1.link(simme1);
        sin2.link(simme1);
        sin4.link(simme1);
        simme1.link(sin3);
        
        getDefGenerator(getDefElementType("Excavacion"), root);
        addInfoReceiver(new WFP05CheckView(this, detailed));
    }
	
}
