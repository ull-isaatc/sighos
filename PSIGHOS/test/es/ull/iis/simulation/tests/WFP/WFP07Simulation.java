package es.ull.iis.simulation.tests.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.StructuredSynchroMergeFlow;

/**
 * WFP 7. Example 1: Transferencia Pacientes
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP07Simulation extends WFPTestSimulation {
	
	public WFP07Simulation(int id, TestWFP.CommonArguments args) {
		super(id, "WFP7: Structured Synchronizing Merge. EjTransferenciaPacientes", args);
    }
    
    protected void createModel() {
        ResourceType rt0 = getDefResourceType("Operador");
        ResourceType rt1 = getDefResourceType("Medico");
        
        WorkGroup wgOp = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgMe = new WorkGroup(this, new ResourceType[] {rt1}, new int[] {1});

        ActivityFlow act0 = getDefActivity("Envio policias", wgOp, false);
        ActivityFlow act1 = getDefActivity("Envio ambulancias", wgOp, false);
        ActivityFlow act2 = getDefActivity("Envio bomberos", wgOp, false);
        ActivityFlow act3 = getDefActivity("Transferencia pacientes", wgMe, false);

        getDefResource("Operador 1", rt0);
        getDefResource("Operador 2", rt0);
        getDefResource("Operador 3", rt0);
        getDefResource("Medico 1", rt1);
        
        StructuredSynchroMergeFlow root = new StructuredSynchroMergeFlow(this);
        
        Condition<ElementInstance> falseCond = new NotCondition<ElementInstance>(new TrueCondition<ElementInstance>());
        
        // Create leafs

        root.addBranch(act0, falseCond);
        root.addBranch(act1);
        root.addBranch(act2, falseCond);
        root.link(act3);

        getDefGenerator(getDefElementType("Emergencia"), root);

//        getSimulation().addInfoReceiver(new WFP07CheckView(getSimulation(), detailed));
    }
	
}
