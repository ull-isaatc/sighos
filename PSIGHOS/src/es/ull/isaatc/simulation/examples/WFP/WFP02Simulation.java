package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.TimeStamp;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.ParallelFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

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
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Detección alarma", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Mandar patrulla", wg, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Generar informe", wg, false);
   
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        getDefResource("Operador3", rt);
        getDefResource("Operador4", rt);
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        ParallelFlow par1 = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        
        root.link(par1);
        par1.link(sin2);
        par1.link(sin3);
         
        getDefGenerator(getDefElementType("Activaciones de alarma"), root);
//        addInfoReceiver(new WFP02CheckView(this, detailed));
        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new TimeStamp[] {DEFACTDURATION[0], DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}