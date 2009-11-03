package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.flow.SynchronizationFlow;

class WFP03CheckView extends CheckElementActionsView {
	public WFP03CheckView(WFP03Simulation simul) {
		this(simul, true);
	}

	public WFP03CheckView(WFP03Simulation simul, boolean detailed) {
		super(simul, "Checking WFP3...", detailed);
//		// First requests
//		ElementReferenceInfos [] ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS];
//		refRequests.put(getSimul().simulationTime2Double(SimulationTime.getZero()), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(0);
//			ref[i].add(1);
//		}
//		// Request of third activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(2);
//		ref[1] = new ElementReferenceInfos(2);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART))), ref);
//		ref[0] = new ElementReferenceInfos(2);
//
//		// Start activities
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.RESSTART), ref);
//		ref[2] = new ElementReferenceInfos(0, 1);
//		ref[1] = new ElementReferenceInfos(1);
//
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART)), ref);
//		ref[0] = new ElementReferenceInfos(0, 1);
//		ref[1] = new ElementReferenceInfos(2);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART))), ref);
//		ref[2] = new ElementReferenceInfos(2);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART))), ref);
//		ref[0] = new ElementReferenceInfos(2);
//		
//		// End activities
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(1);
//		ref[1] = new ElementReferenceInfos(0, 1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART))), ref);
//		ref[1] = new ElementReferenceInfos(2);
//		ref[0] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.RESSTART))), ref);
//		ref[2] = new ElementReferenceInfos(2);
//		ref[0] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[1].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)))), ref);
//		ref[0] = new ElementReferenceInfos(2);
		
		ElementReferenceInfos [] ref;
		ref = new ElementReferenceInfos[3];
		refRequests.put(0.0, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.446E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(2);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.495E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(2);	}

}

/**
 * WFP 3, example 1: Envío de mercancias
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP03Simulation extends WFPTestSimulation {
	
	public WFP03Simulation(int id, boolean detailed) {
		super(id, "WFP3: Synchronization. EjEnvioMercacias", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Comercial");

        WorkGroup wg = new WorkGroup(rt, 1);
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Generacion de factura", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Comprobacion de factura", 1, wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio de mercancias", wg, false);
        

        getDefResource("Comercial1", rt);
        getDefResource("Comercial2", rt);
        getDefResource("Comercial3", rt);
        
        ParallelFlow root = new ParallelFlow(this);
        SynchronizationFlow synchro1 = new SynchronizationFlow(this);
        SingleFlow sin1 = new SingleFlow(this, act0);
        SingleFlow sin2 = new SingleFlow(this, act1);
        SingleFlow sin3 = new SingleFlow(this, act2);
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(synchro1);
        sin2.link(synchro1);
        synchro1.link(sin3);
        
        getDefGenerator(getDefElementType("Cliente"), root);

//        addInfoReceiver(new WFP03CheckView(this, detailed));
        addInfoReceiver(new CheckFlowsView(this, root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[1], DEFACTDURATION[0]}, detailed));
    }
	
}
