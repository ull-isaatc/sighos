package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.factory.SimulationType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * WFP 1, Example 1: Tarjeta de Credito
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP01Simulation extends WFPTestSimulationFactory {
	
	public WFP01Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP1: Sequence. EjTarjetaCredito", detailed);
    }
    
    protected Simulation createModel() {
		model = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);    	
        ResourceType rt = getDefResourceType("Cajero");
    	
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt}, new int[] {1});
    	ActivityFlow act0 = getDefActivity("Verificar cuenta", wg, false);
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
   
        getDefResource("Cajero1", rt);
        getDefResource("Cajero2", rt);
        getDefResource("Cajero3", rt);
        
        act0.link(act1);
         
        getDefGenerator(getDefElementType("Cliente"), act0);
//        addInfoReceiver(new WFP01CheckView(this, detailed));
//        model.addInfoReceiver(new CheckFlowsView(model, act0, new long[] {DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
        return model;
    }
	
}
