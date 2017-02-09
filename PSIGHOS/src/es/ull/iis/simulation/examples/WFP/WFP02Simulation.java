package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 2, example 2: Alarma
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP02Simulation extends WFPTestSimulationFactory {
	
	public WFP02Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP2: Parallel Split. EjAlarma", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Operador");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
    	ActivityFlow<?,?> act0 = getDefActivity("Detección alarma", wg);
    	ActivityFlow<?,?> act1 = getDefActivity("Mandar patrulla", wg, false);
    	ActivityFlow<?,?> act2 = getDefActivity("Generar informe", wg, false);
   
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        getDefResource("Operador3", rt);
        getDefResource("Operador4", rt);
        
        ParallelFlow par1 = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        
        act0.link(par1);
        par1.link(act1);
        par1.link(act2);
         
        getDefGenerator(getDefElementType("Activaciones de alarma"), act0);
//        addInfoReceiver(new WFP02CheckView(this, detailed));
        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), act0, new TimeStamp[] {DEFACTDURATION[0], DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}