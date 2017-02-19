package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.model.TimeStamp;

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
    	ActivityFlow<?,?> act0 = getDefActivity("Verificar cuenta", wg, false);
    	ActivityFlow<?,?> act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
   
        getDefResource("Cajero1", rt);
        getDefResource("Cajero2", rt);
        getDefResource("Cajero3", rt);
        
        act0.link(act1);
         
        getDefGenerator(getDefElementType("Cliente"), act0);
//        addInfoReceiver(new WFP01CheckView(this, detailed));
        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), act0, new TimeStamp[] {DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}
