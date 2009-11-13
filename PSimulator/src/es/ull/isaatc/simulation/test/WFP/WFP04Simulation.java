package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

class WFP04CheckView extends CheckElementActionsView {
	public WFP04CheckView(Simulation simul) {
		this(simul, true);
	}

	public WFP04CheckView(Simulation simul, boolean detailed) {
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
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP04Simulation extends WFPTestSimulationFactory {
	int ndays;
	
	public WFP04Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP4: Exclusive Choice. EjSistemaVotacion", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Encargado");
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
        
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Celebrar elecciones", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Recuentos de votos", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Declarar resultados", wg, false);
        
        getDefResource("Encargado 1", rt); 
      
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        ExclusiveChoiceFlow excho1 = (ExclusiveChoiceFlow)factory.getFlowInstance(10, "ExclusiveChoiceFlow");
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        
        root.link(excho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        excho1.link(sin2);
        excho1.link(sin3, falseCond);

        getDefGenerator(getDefElementType("Votante"), root);
//        getSimulation().addInfoReceiver(new WFP04CheckView(getSimulation(), detailed));
    }
	
}
