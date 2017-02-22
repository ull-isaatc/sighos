package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.SynchronizationFlow;

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
    
    protected Model createModel() {
		model = new Model(SIMUNIT);   	
        ResourceType rt = getDefResourceType("Comercial");

        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});
        ActivityFlow act0 = getDefActivity("Generacion de factura", wg, false);
        ActivityFlow act1 = getDefActivity("Comprobacion de factura", 1, wg, false);
        ActivityFlow act2 = getDefActivity("Envio de mercancias", wg, false);
        

        getDefResource("Comercial1", rt);
        getDefResource("Comercial2", rt);
        getDefResource("Comercial3", rt);
        
        ParallelFlow root = new ParallelFlow(model);
        SynchronizationFlow synchro1 = new SynchronizationFlow(model);
        
        root.link(act0);
        root.link(act1);
        act0.link(synchro1);
        act1.link(synchro1);
        synchro1.link(act2);
        
        getDefGenerator(getDefElementType("Cliente"), root);

//        addInfoReceiver(new WFP03CheckView(this, detailed));
//        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[1], DEFACTDURATION[0]}, detailed));
        return model;
    }
	
}
