package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.condition.NotCondition;
import es.ull.isaatc.simulation.sequential.condition.TrueCondition;
import es.ull.isaatc.simulation.sequential.flow.MultiChoiceFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;

class WFP06CheckView extends CheckElementActionsView {
	public WFP06CheckView(WFP06Simulation simul) {
		this(simul, true);
	}

	public WFP06CheckView(WFP06Simulation simul, boolean detailed) {
		super(simul, "Checking WFP6...", detailed);
		
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
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref[1].add(1);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(3);
		ref = new ElementReferenceInfos[3];
		refRequests.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[0].add(3);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.37E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.396E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.42E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(1);
		ref[2].add(3);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.446E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref = new ElementReferenceInfos[3];
		refStartActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(3);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.396E-321, ref);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(0);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(0);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.42E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(0);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(3);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.446E-321, ref);
		ref[2] = new ElementReferenceInfos();
		ref[2].add(3);
		ref[2].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.47E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(1);
		ref[1] = new ElementReferenceInfos();
		ref[1].add(1);
		ref = new ElementReferenceInfos[3];
		refEndActs.put(2.495E-321, ref);
		ref[0] = new ElementReferenceInfos();
		ref[0].add(3);
	}
		
}

/**
 * WFP 6. Example 1: Llamadas emergencia
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP06Simulation extends WFPTestSimulation {
	
	public WFP06Simulation(int id, boolean detailed) {
		super(id, "WFP6: Multichoice. EjLamadasEmergencia", detailed);
    }
    
    protected void createModel() {
   	
        ResourceType rt = getDefResourceType("Operador");
        WorkGroup wg = new WorkGroup(rt, 1);

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Recepcion de llamada", wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Envio policias", wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Envio ambulancias", wg, false);
        TimeDrivenActivity act3 = getDefTimeDrivenActivity("Envio bomberos", wg, false);
        
        getDefResource("Operador1", rt);
        getDefResource("Operador2", rt);
        
        SingleFlow root = new SingleFlow(this, act0);
        MultiChoiceFlow mulcho1 = new MultiChoiceFlow(this);
        SingleFlow sin1 = new SingleFlow(this, act1);
        SingleFlow sin2 = new SingleFlow(this, act2);
        SingleFlow sin3 = new SingleFlow(this, act3);
        
        root.link(mulcho1);
        Condition falseCond = new NotCondition(new TrueCondition());
        mulcho1.link(sin1);
        mulcho1.link(sin2, falseCond);
        mulcho1.link(sin3);

        getDefGenerator(getDefElementType("Emergencia"), root);
        addInfoReceiver(new WFP06CheckView(this, detailed));
    }
	
}

