package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * WFP 4 Example 1: Sistema Votaci�n
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP04Simulation extends WFPTestSimulationFactory {
	int ndays;
	
	public WFP04Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP4: Exclusive Choice. EjSistemaVotacion", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
        
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Celebrar elecciones", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Recuentos de votos", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 
      
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        ExclusiveChoiceFlow excho1 = (ExclusiveChoiceFlow)factory.getFlowInstance(10, "ExclusiveChoiceFlow");
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        
        root.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(sin2);
        excho1.link(sin3, falseCond);

        getDefGenerator(getDefElementType("Votante"), root);
//        getSimulation().addInfoReceiver(new WFP04CheckView(getSimulation(), detailed));
    }
	
}