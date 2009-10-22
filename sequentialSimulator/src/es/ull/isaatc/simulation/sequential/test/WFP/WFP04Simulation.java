package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.condition.NotCondition;
import es.ull.isaatc.simulation.sequential.condition.TrueCondition;
import es.ull.isaatc.simulation.sequential.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;

class WFP04CheckView extends CheckElementActionsView {
	public WFP04CheckView(WFP04Simulation simul) {
		this(simul, true);
	}

	public WFP04CheckView(WFP04Simulation simul, boolean detailed) {
		super(simul, "Checking WFP4...", detailed);
//		// First requests
//		ElementReferenceInfos [] ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS];
//		refRequests.put(getSimul().simulationTime2Double(SimulationTime.getZero()), ref);
//		for (int i = 0; i < WFPTestSimulation.DEFNELEMENTS; i++)
//			ref[i] = new ElementReferenceInfos(0);
//		// Request of second activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART))), ref);
//		ref[0] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refRequests.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(3).add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(1);
//
//		// Start first activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.RESSTART), ref);
//		ref[2] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[0] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(2).add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(0);
//		// Start second activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(3).add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(4).add(WFPTestSimulation.RESSTART)), ref);
//		ref[0] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refStartActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(5).add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(1);
//
//		// End first activity
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(2).add(WFPTestSimulation.RESSTART)), ref);
//		ref[0] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(3).add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(0);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(4).add(WFPTestSimulation.RESSTART)), ref);
//		ref[2] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(5).add(WFPTestSimulation.RESSTART)), ref);
//		ref[0] = new ElementReferenceInfos(1);
//		ref = new ElementReferenceInfos[WFPTestSimulation.DEFNELEMENTS]; 
//		refEndActs.put(getSimul().simulationTime2Double(WFPTestSimulation.DEFACTDURATION[0].multiply(6).add(WFPTestSimulation.RESSTART)), ref);
//		ref[1] = new ElementReferenceInfos(1);

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
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.446E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.446E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.495E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.47E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.495E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.52E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		
	}
	
}
/**
 * WFP 4 Example 1: Sistema Votación
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP04Simulation extends WFPTestSimulation {
	int ndays;
	
	public WFP04Simulation(int id, boolean detailed) {
		super(id, "WFP4: Exclusive Choice. EjSistemaVotacion", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = new WorkGroup(rt, 1);
        
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Celebrar elecciones", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Recuentos de votos", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 
      
        SingleFlow root = new SingleFlow(this, act0);
        ExclusiveChoiceFlow excho1 = new ExclusiveChoiceFlow(this);
        SingleFlow sin2 = new SingleFlow(this, act1);
        SingleFlow sin3 = new SingleFlow(this, act2);
        
        root.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(sin2);
        excho1.link(sin3, falseCond);

        getDefGenerator(getDefElementType("Votante"), root);
        addInfoReceiver(new WFP04CheckView(this, detailed));
    }
	
}
