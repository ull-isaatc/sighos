package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.NotCondition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.core.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

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
        
        WorkGroup wgOp = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgMe = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Envio policias", wgOp, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Envio ambulancias", wgOp, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio bomberos", wgOp, false);
        TimeDrivenActivity act3 = getDefTimeDrivenActivity("Transferencia pacientes", wgMe, false);

        getDefResource("Operador 1", rt0);
        getDefResource("Operador 2", rt0);
        getDefResource("Operador 3", rt0);
        getDefResource("Medico 1", rt1);
        
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act3);
        StructuredSynchroMergeFlow root = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
        
        Condition falseCond = new NotCondition(new TrueCondition());
        
        // Create leafs

        root.addBranch(sin1, falseCond);
        root.addBranch(sin2);
        root.addBranch(sin3, falseCond);
        root.link(sin4);

        getDefGenerator(getDefElementType("Emergencia"), root);

//        getSimulation().addInfoReceiver(new WFP07CheckView(getSimulation(), detailed));
    }
	
}
