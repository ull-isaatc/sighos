package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SynchronizationFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 33. Envío Mercancías
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP33Simulation extends WFPTestSimulationFactory {

	public WFP33Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP33: Generalized AND-Join. EjEnvioMercacias", detailed);
	}

	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Comercial");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
	   	
        ActivityFlow<?,?> act0 = getDefActivity("Generacion de factura", wg, false);
        ActivityFlow<?,?> act1 = getDefActivity("Comprobacion de factura", wg, false);
        ActivityFlow<?,?> act2 = getDefActivity("Envio de mercancias", wg, false);
        
        getDefResource("Comercial 1", rt0);        
        getDefResource("Comercial 2", rt0);        
        getDefResource("Comercial 3", rt0);        
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");

        SynchronizationFlow synchro1 = (SynchronizationFlow)factory.getFlowInstance("SynchronizationFlow", false);
        
        root.link(act0);
        root.link(act1);
        act0.link(synchro1);
        act1.link(synchro1);
        synchro1.link(act2);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}
}
