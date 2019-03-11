package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.SynchronizationFlow;

/**
 * WFP 33. Envío Mercancías
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP33Simulation extends WFPTestSimulation {

	public WFP33Simulation(int id) {
		super(id, "WFP33: Generalized AND-Join. EjEnvioMercacias");
	}

	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Comercial");
        
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
	   	
        ActivityFlow act0 = getDefActivity("Generacion de factura", wg, false);
        ActivityFlow act1 = getDefActivity("Comprobacion de factura", wg, false);
        ActivityFlow act2 = getDefActivity("Envio de mercancias", wg, false);
        
        getDefResource("Comercial 1", rt0);        
        getDefResource("Comercial 2", rt0);        
        getDefResource("Comercial 3", rt0);        
        
        ParallelFlow root = new ParallelFlow(this);

        SynchronizationFlow synchro1 = new SynchronizationFlow(this, false);
        
        root.link(act0);
        root.link(act1);
        act0.link(synchro1);
        act1.link(synchro1);
        synchro1.link(act2);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}
}
