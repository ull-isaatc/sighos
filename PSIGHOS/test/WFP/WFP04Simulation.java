package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;

/**
 * WFP 4 Example 1: Sistema Votación
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP04Simulation extends WFPTestSimulation {
	int ndays;
	
	public WFP04Simulation(int id) {
		super(id, "WFP4: Exclusive Choice. EjSistemaVotacion");
    }
    
    protected void createModel() {
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt}, new int[] {1});
        
        ActivityFlow act0 = getDefActivity("Celebrar elecciones", wg, false);
        ActivityFlow act1 = getDefActivity("Recuentos de votos", wg, false);
        ActivityFlow act2 = getDefActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 

        ExclusiveChoiceFlow excho1 = new ExclusiveChoiceFlow(this);
        
        act0.link(excho1);
        Condition<ElementInstance> falseCond = new NotCondition<ElementInstance>(new TrueCondition<ElementInstance>());
        excho1.link(act1);
        excho1.link(act2, falseCond);

        getDefGenerator(getDefElementType("Votante"), act0);

    }
	
}
