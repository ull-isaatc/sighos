package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;

class WFP07CheckView extends CheckElementActionsView {
	public WFP07CheckView(Simulation simul) {
		this(simul, true);
	}

	public WFP07CheckView(Simulation simul, boolean detailed) {
		super(simul, "Checking WFP07...", detailed);

		ElementReferenceInfos [] ref;
		ref = new ElementReferenceInfos[3];
		refRequests.put(0.0, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
	}
}

/**
 * WFP 7. Example 1: Transferencia Pacientes
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP07Simulation extends WFPTestSimulationFactory {
	
	public WFP07Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP7: Structured Synchronizing Merge. EjTransferenciaPacientes", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt0 = getDefResourceType("Operador");
        ResourceType rt1 = getDefResourceType("Medico");
        
        WorkGroup wgOp = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgMe = factory.getWorkGroupInstance(1, new ResourceType[] {rt1}, new int[] {1});

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Envio policias", wgOp, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Envio ambulancias", wgOp, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Transferencia pacientes", wgMe, false);

        getDefResource("Operador 1", rt0);
        getDefResource("Medico 1", rt1);
        
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        StructuredSynchroMergeFlow root = (StructuredSynchroMergeFlow)factory.getFlowInstance(10, "StructuredSynchroMergeFlow");
        
        Condition falseCond = new NotCondition(new TrueCondition());
        
        // Create leafs

        root.addBranch(sin1, falseCond);
        root.addBranch(sin2);
        root.link(sin3);

        getDefGenerator(getDefElementType("Emergencia"), root);

//        getSimulation().addInfoReceiver(new WFP07CheckView(getSimulation(), detailed));
    }
	
}
