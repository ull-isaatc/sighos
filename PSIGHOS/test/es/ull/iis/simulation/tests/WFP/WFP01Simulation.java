package es.ull.iis.simulation.tests.WFP;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * WFP 1, Example 1: Tarjeta de Credito
 * @author Yeray Callero
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP01Simulation extends WFPTestSimulation {
	
	public WFP01Simulation(int id, TestWFP.CommonArguments args) {
		super(id, "WFP1: Sequence. EjTarjetaCredito", args);
    }
    
    protected void createModel() {
        ResourceType rt = getDefResourceType("Cajero");
    	
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt}, new int[] {1});
    	ActivityFlow act0 = getDefActivity("Verificar cuenta", wg, false);
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
   
        getDefResource("Cajero1", rt);
        getDefResource("Cajero2", rt);
        getDefResource("Cajero3", rt);
        
        act0.link(act1);
         
        getDefGenerator(getDefElementType("Cliente"), act0);
//        addInfoReceiver(new WFP01CheckView(this, detailed));
//        model.addInfoReceiver(new CheckFlowsView(model, act0, new long[] {DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}
