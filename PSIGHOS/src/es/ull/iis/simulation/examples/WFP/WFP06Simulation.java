package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.MultiChoiceFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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

        Activity act0 = getDefActivity("Recepcion de llamada", wg, false);
        Activity act1 = getDefActivity("Envio policias", wg, false);
        Activity act2 = getDefActivity("Envio ambulancias", wg, false);
        Activity act3 = getDefActivity("Envio bomberos", wg, false);
        
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

