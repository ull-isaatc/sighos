package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

class WFP02CheckView extends CheckElementActionsView {
	public WFP02CheckView(Simulation simul) {
		this(simul, true);
	}

	public WFP02CheckView(Simulation simul, boolean detailed) {
		super(simul, "Checking WFP2...", detailed);
//		// First requests
//		ElementReferenceInfos [] ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS];
//		refRequests.put(getSimul().simulationTime2Double(SimulationTime.getZero()), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(0);
//		}
//		// Request of second activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(1);
//			ref[i].add(2);
//		}
//		// Start first activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.RESSTART), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(0);
//		}
//		// Start second activities
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos();
//		ref[1].add(1);
//		ref[1].add(2);
//		ref[2] = new ElementReferenceInfos();
//		ref[2].add(1);
//		ref[2].add(2);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART))), ref);
//		ref[0] = new ElementReferenceInfos();
//		ref[0].add(1);
//		ref[0].add(2);
//		
//		// End first activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(0);
//		}
//		// Start second activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART))), ref);
//		ref[1] = new ElementReferenceInfos();
//		ref[1].add(1);
//		ref[1].add(2);
//		ref[2] = new ElementReferenceInfos();
//		ref[2].add(1);
//		ref[2].add(2);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)))), ref);
//		ref[0] = new ElementReferenceInfos();
//		ref[0].add(1);
//		ref[0].add(2);
		
		ElementReferenceInfos [] ref;
		ref = new ElementReferenceInfos[3];
		refRequests.put(0.0, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.396E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[0].add(2);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[1].add(2);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[1].add(2);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref[0].add(1);
		
	}

}

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
        
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Detección alarma", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Mandar patrulla", wg, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Generar informe", wg, false);
   
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        getDefResource("Operador3", rt);
        getDefResource("Operador4", rt);
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        ParallelFlow par1 = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        
        root.link(par1);
        par1.link(sin2);
        par1.link(sin3);
         
        getDefGenerator(getDefElementType("Activaciones de alarma"), root);
//        addInfoReceiver(new WFP02CheckView(this, detailed));
        getSimulation().addInfoReceiver(new CheckFlowsView(getSimulation(), root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}