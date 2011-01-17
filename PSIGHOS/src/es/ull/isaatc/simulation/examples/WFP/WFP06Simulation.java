package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.NotCondition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.MultiChoiceFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 6. Example 1: Llamadas emergencia
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP06Simulation extends WFPTestSimulationFactory {
	
	public WFP06Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP6: Multichoice. EjLamadasEmergencia", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Operador");
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Recepcion de llamada", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Envio policias", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio ambulancias", wg, false);
        TimeDrivenActivity act3 = getDefTimeDrivenActivity("Envio bomberos", wg, false);
        
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        MultiChoiceFlow mulcho1 = (MultiChoiceFlow)factory.getFlowInstance("MultiChoiceFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act3);
        
        root.link(mulcho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        mulcho1.link(sin1);
        mulcho1.link(sin2, falseCond);
        mulcho1.link(sin3);

        getDefGenerator(getDefElementType("Emergencia"), root);
//        getSimulation().addInfoReceiver(new WFP06CheckView(getSimulation(), detailed));
    }
	
}

