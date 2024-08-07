package es.ull.iis.simulation.tests.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;

/**
 * WFP 2, example 2: Alarma
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP02Simulation extends WFPTestSimulation {
	
	public WFP02Simulation(int id, TestWFP.CommonArguments args) {
		super(id, "WFP2: Parallel Split. EjAlarma", args);
    }
    
    protected void createModel() {
        ResourceType rt = getDefResourceType("Operador");
        
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt}, new int[] {1});
    	ActivityFlow act0 = getDefActivity("Detecci�n alarma", wg);
    	ActivityFlow act1 = getDefActivity("Mandar patrulla", wg, false);
    	ActivityFlow act2 = getDefActivity("Generar informe", wg, false);
   
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        getDefResource("Operador3", rt);
        getDefResource("Operador4", rt);
        
        ParallelFlow par1 = new ParallelFlow(this);
        
        act0.link(par1);
        par1.link(act1);
        par1.link(act2);
         
        getDefGenerator(getDefElementType("Activaciones de alarma"), act0);
//        addInfoReceiver(new WFP02CheckView(this, detailed));
//        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), act0, new long[] {DEFACTDURATION[0], DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}