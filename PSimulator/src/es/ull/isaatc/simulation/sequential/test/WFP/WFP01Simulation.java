package es.ull.isaatc.simulation.sequential.test.WFP;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;

class WFP01CheckView extends CheckElementActionsView {

	public WFP01CheckView(WFP01Simulation simul) {
		this(simul, true);
	}

	public WFP01CheckView(WFP01Simulation simul, boolean detailed) {
		super(simul, "Checking WFP1...", detailed);
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
//		}
//		// Start first activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.RESSTART), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(0);
//		}
//		// Start second activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(1);
//		}
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
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++) {
//			ref[i] = new ElementReferenceInfos();
//			ref[i].add(1);
//		}

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
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
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
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
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
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
	}

}

/**
 * WFP 1, Example 1: Tarjeta de Credito
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP01Simulation extends WFPTestSimulation {
	
	public WFP01Simulation(int id, boolean detailed) {
		super(id, "WFP1: Sequence. EjTarjetaCredito", detailed);
    }
    
    protected void createModel() {

        ResourceType rt = getDefResourceType("Cajero");
    	
        WorkGroup wg = new WorkGroup(rt, 1);
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Verificar cuenta", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Obtener detalles tarjeta", wg, false);
        
   
        getDefResource("Cajero1", rt);
        getDefResource("Cajero2", rt);
        getDefResource("Cajero3", rt);
        
        SingleFlow root = new SingleFlow(this, act0);
        SingleFlow sin1 = new SingleFlow(this, act1);
        root.link(sin1);
         
        getDefGenerator(getDefElementType("Cliente"), root);
//        addInfoReceiver(new WFP01CheckView(this, detailed));
        addInfoReceiver(new CheckFlowsView(this, root, new Time[] {DEFACTDURATION[0], DEFACTDURATION[0]}, detailed));
    }
	
}
