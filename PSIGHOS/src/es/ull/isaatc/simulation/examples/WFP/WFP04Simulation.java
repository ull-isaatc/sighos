package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.NotCondition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 4 Example 1: Sistema Votación
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP04Simulation extends WFPTestSimulationFactory {
	int ndays;
	
	public WFP04Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP4: Exclusive Choice. EjSistemaVotacion", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
        
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Celebrar elecciones", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Recuentos de votos", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 
      
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        ExclusiveChoiceFlow excho1 = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        
        root.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(sin2);
        excho1.link(sin3, falseCond);

        getDefGenerator(getDefElementType("Votante"), root);
//        getSimulation().addInfoReceiver(new WFP04CheckView(getSimulation(), detailed));
    }
	
}
