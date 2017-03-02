package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.MultiChoiceFlow;

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
    
    protected Simulation createModel() {
		model = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);   	
        ResourceType rt = getDefResourceType("Operador");
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});

        ActivityFlow act0 = getDefActivity("Recepcion de llamada", wg, false);
        ActivityFlow act1 = getDefActivity("Envio policias", wg, false);
        ActivityFlow act2 = getDefActivity("Envio ambulancias", wg, false);
        ActivityFlow act3 = getDefActivity("Envio bomberos", wg, false);
        
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        
        MultiChoiceFlow mulcho1 = new MultiChoiceFlow(model);
        
        act0.link(mulcho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        mulcho1.link(act1);
        mulcho1.link(act2, falseCond);
        mulcho1.link(act3);

        getDefGenerator(getDefElementType("Emergencia"), act0);
//        getSimulation().addInfoReceiver(new WFP06CheckView(getSimulation(), detailed));
        return model;
    }
	
}

