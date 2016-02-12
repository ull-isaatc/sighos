package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 1, Example 1: Tarjeta de Credito
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP01Simulation extends WFPTestSimulationFactory {
	
	public WFP01Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP1: Sequence. EjTarjetaCredito", detailed);
    }
    
    protected void createModel() {

        ResourceType rt = getDefResourceType("Cajero");
    	
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Verificar cuenta", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Obtener detalles tarjeta", wg, false);
        
   
        getDefResource("Cajero1", rt);
        getDefResource("Cajero2", rt);
        getDefResource("Cajero3", rt);
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        root.link(sin1);
         
        getDefGenerator(getDefElementType("Cliente"), root);
//        addInfoReceiver(new WFP01CheckView(this, detailed));
        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new TimeStamp[] {DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}
