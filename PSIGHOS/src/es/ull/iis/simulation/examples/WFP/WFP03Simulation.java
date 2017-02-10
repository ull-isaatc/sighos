package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SynchronizationFlow;

/**
 * WFP 3, example 1: Envío de mercancias
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP03Simulation extends WFPTestSimulationFactory {
	
	public WFP03Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP3: Synchronization. EjEnvioMercacias", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Comercial");

        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
        ActivityFlow<?,?> act0 = getDefActivity("Generacion de factura", wg, false);
        ActivityFlow<?,?> act1 = getDefActivity("Comprobacion de factura", 1, wg, false);
        ActivityFlow<?,?> act2 = getDefActivity("Envio de mercancias", wg, false);
        

        getDefResource("Comercial1", rt);
        getDefResource("Comercial2", rt);
        getDefResource("Comercial3", rt);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SynchronizationFlow synchro1 = (SynchronizationFlow)factory.getFlowInstance("SynchronizationFlow");
        
        root.link(act0);
        root.link(act1);
        act0.link(synchro1);
        act1.link(synchro1);
        synchro1.link(act2);
        
        getDefGenerator(getDefElementType("Cliente"), root);

//        addInfoReceiver(new WFP03CheckView(this, detailed));
//        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[1], DEFACTDURATION[0]}, detailed));
    }
	
}
