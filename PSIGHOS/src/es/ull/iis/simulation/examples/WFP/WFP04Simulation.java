package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
        
        ActivityFlow<?,?> act0 = getDefActivity("Celebrar elecciones", wg, false);
        ActivityFlow<?,?> act1 = getDefActivity("Recuentos de votos", wg, false);
        ActivityFlow<?,?> act2 = getDefActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 

        ExclusiveChoiceFlow excho1 = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
        
        act0.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(act1);
        excho1.link(act2, falseCond);

        getDefGenerator(getDefElementType("Votante"), act0);
//        getSimulation().addInfoReceiver(new WFP04CheckView(getSimulation(), detailed));
    }
	
}
