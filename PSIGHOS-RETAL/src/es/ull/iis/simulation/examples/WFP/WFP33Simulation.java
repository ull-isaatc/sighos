package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
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
	   	
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Generacion de factura", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Comprobacion de factura", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio de mercancias", wg, false);
        
        getDefResource("Comercial 1", rt0);        
        getDefResource("Comercial 2", rt0);        
        getDefResource("Comercial 3", rt0);        
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);

        SynchronizationFlow synchro1 = (SynchronizationFlow)factory.getFlowInstance("SynchronizationFlow", false);
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(synchro1);
        sin2.link(synchro1);
        synchro1.link(sin3);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}
}
