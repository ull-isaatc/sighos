package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;

class WFP02CheckView extends CheckElementActionsView {
	public WFP02CheckView(WFP02Simulation simul) {
		this(simul, true);
	}

	public WFP02CheckView(WFP02Simulation simul, boolean detailed) {
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
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP02Simulation extends WFPTestSimulation {
	
	public WFP02Simulation(int id, boolean detailed) {
		super(id, "WFP2: Parallel Split. EjAlarma", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Operador");
        
        WorkGroup wg = new WorkGroup(rt, 1);
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Detección alarma", wg);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Mandar patrulla", wg, false);
    	TimeDrivenActivity act2 = getDefTimeDrivenActivity("Generar informe", wg, false);
   
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        getDefResource("Operador3", rt);
        getDefResource("Operador4", rt);
        
        
        SingleFlow root = new SingleFlow(this, act0);
        ParallelFlow par1 = new ParallelFlow(this);
        SingleFlow sin2 = new SingleFlow(this, act1);
        SingleFlow sin3 = new SingleFlow(this, act2);
        
        root.link(par1);
        par1.link(sin2);
        par1.link(sin3);
         
        getDefGenerator(getDefElementType("Activaciones de alarma"), root);
//        addInfoReceiver(new WFP02CheckView(this, detailed));
        addInfoReceiver(new CheckFlowsView(this, root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}