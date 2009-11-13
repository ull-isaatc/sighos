package es.ull.isaatc.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.MultiChoiceFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * WFP 10. Arbitrary Cycle
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP10Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP10Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP10: Arbitrary Cycle. Ej", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        
    	ResourceType rt0 = getDefResourceType("Operario");
    	ResourceType rt1 = getDefResourceType("Operario especial");
    	
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
	   	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Rellenar bidon", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Realizar envío de bidon", wg, false);
    	
    	act0.putVar("capacidadBidon", 20);
    	getSimulation().putVar("litrosIntroducidos", 0.0);
    	getSimulation().putVar("enviosRealizados", 0);
        

        getDefResource("Operario1", rt0);
        getDefResource("Operario1", rt0);
        getDefResource("Operario_especial1", rt1);
        
        Condition cond = new Condition(getSimulation()) {
        	@Override
        	public boolean check(Element e) {
        		return simul.getVar("litrosIntroducidos").getValue().doubleValue() < ((Simulation)simul).getActivity(0).getVar("capacidadBidon").getValue().doubleValue();
        	}
        };
        
        Condition notCond = new NotCondition(cond);

        SimulationUserCode code1 = new SimulationUserCode();
        code1.add(UserMethod.BEFORE_REQUEST, "System.out.println(\"Volumen actual es \" + <%GET(S.litrosIntroducidos)%> + \" litros\");" +	
        		"System.out.println(\"Capacidad bidon es \" + <%GET(A0.capacidadBidon)%> + \" litros\");" +
 				"return true;");
        MultiChoiceFlow mul1 = (MultiChoiceFlow)factory.getFlowInstance(10, "MultiChoiceFlow", code1);

        SimulationUserCode code2 = new SimulationUserCode();
        code2.add(UserMethod.AFTER_FINALIZE, "if (<%GET(S.litrosIntroducidos)%> < <%GET(A0.capacidadBidon)%>) {" +
				"double random = Math.random() * 50; " +
				"<%SET(S.litrosIntroducidos, <%GET(S.litrosIntroducidos)%> + random)%>;" +		
				"System.out.println(\"Introducimos \" + random + \" litros y nuestro volumen actual es \" + <%GET(S.litrosIntroducidos)%> + \" litros\");" +
	  	  "}");
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", code2, act0);
        
        SimulationUserCode code3 = new SimulationUserCode();
        code3.add(UserMethod.BEFORE_REQUEST, 	"<%SET(S.litrosIntroducidos, 0)%>;" +
 				"<%SET(S.enviosRealizados, <%GET(S.enviosRealizados)%> + 1)%>;" +
 				"System.out.println(\"Nuevo envio realizado\");" +
 				"return true;");

        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", code3, act1);
        
        root.link(mul1);
        ArrayList<Flow> succList = new ArrayList<Flow>();
        succList.add(root);
        succList.add(sin1);
        ArrayList<Condition> condList = new ArrayList<Condition>();
        condList.add(cond);
        condList.add(notCond);
        mul1.link(succList, condList);
        
        getDefGenerator(getDefElementType("Cliente"), root);
	}

}
