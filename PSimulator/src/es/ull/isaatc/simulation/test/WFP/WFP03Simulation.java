package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.SynchronizationFlow;

/**
 * WFP 3, example 1: Env�o de mercancias
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP03Simulation extends WFPTestSimulationFactory {
	
	public WFP03Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP3: Synchronization. EjEnvioMercacias", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Comercial");

        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Generacion de factura", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Comprobacion de factura", 1, wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio de mercancias", wg, false);
        

        getDefResource("Comercial1", rt);
        getDefResource("Comercial2", rt);
        getDefResource("Comercial3", rt);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
        SynchronizationFlow synchro1 = (SynchronizationFlow)factory.getFlowInstance(11, "SynchronizationFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(synchro1);
        sin2.link(synchro1);
        synchro1.link(sin3);
        
        getDefGenerator(getDefElementType("Cliente"), root);

//        addInfoReceiver(new WFP03CheckView(this, detailed));
//        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[1], DEFACTDURATION[0]}, detailed));
    }
	
}
