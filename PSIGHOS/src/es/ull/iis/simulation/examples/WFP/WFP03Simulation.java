package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
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
	
	public WFP03Simulation(int id, boolean detailed) {
		super(id, "WFP3: Synchronization. EjEnvioMercacias", detailed);
    }
    
    protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);   	
        ResourceType rt = getDefResourceType("Comercial");

        WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt}, new int[] {1});
        ActivityFlow act0 = getDefActivity("Generacion de factura", wg, false);
        ActivityFlow act1 = getDefActivity("Comprobacion de factura", 1, wg, false);
        ActivityFlow act2 = getDefActivity("Envio de mercancias", wg, false);
        

        getDefResource("Comercial1", rt);
        getDefResource("Comercial2", rt);
        getDefResource("Comercial3", rt);
        
        ParallelFlow root = new ParallelFlow(simul);
        SynchronizationFlow synchro1 = new SynchronizationFlow(simul);
        
        root.link(act0);
        root.link(act1);
        act0.link(synchro1);
        act1.link(synchro1);
        synchro1.link(act2);
        
        getDefGenerator(getDefElementType("Cliente"), root);

//        addInfoReceiver(new WFP03CheckView(this, detailed));
//        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[1], DEFACTDURATION[0]}, detailed));
        return simul;
    }
	
}
