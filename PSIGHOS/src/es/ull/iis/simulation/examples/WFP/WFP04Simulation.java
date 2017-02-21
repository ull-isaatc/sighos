package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;

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
    
    protected void createModel(Model model) {
   	
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});
        
        ActivityFlow act0 = getDefActivity("Celebrar elecciones", wg, false);
        ActivityFlow act1 = getDefActivity("Recuentos de votos", wg, false);
        ActivityFlow act2 = getDefActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 

        ExclusiveChoiceFlow excho1 = new ExclusiveChoiceFlow(model);
        
        act0.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(act1);
        excho1.link(act2, falseCond);

        getDefGenerator(getDefElementType("Votante"), act0);
//        getSimulation().addInfoReceiver(new WFP04CheckView(getSimulation(), detailed));
    }
	
}
