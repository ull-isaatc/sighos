package es.ull.iis.simulation.examples.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.MultiChoiceFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationUserCode;
import es.ull.iis.simulation.factory.UserMethod;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
	 * @param detailed
	 */
	public WFP10Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP10: Arbitrary Cycle. Ej", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        
    	ResourceType rt0 = getDefResourceType("Operario");
    	ResourceType rt1 = getDefResourceType("Operario especial");
    	
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
	   	
    	Activity<?, ?, ?> act0 = getDefActivity("Rellenar bidon", wg, false);
    	Activity<?, ?, ?> act1 = getDefActivity("Realizar envío de bidon", wg, false);
    	
    	getSimulation().putVar("capacidadBidon", 20);
    	getSimulation().putVar("litrosIntroducidos", 0.0);
    	getSimulation().putVar("enviosRealizados", 0);
        

        getDefResource("Operario1", rt0);
        getDefResource("Operario1", rt0);
        getDefResource("Operario_especial1", rt1);
        
        Condition cond = new Condition(getSimulation()) {
        	@Override
        	public boolean check(Element e) {
        		return simul.getVar("litrosIntroducidos").getValue().doubleValue() < simul.getVar("capacidadBidon").getValue().doubleValue();
        	}
        };
        
        Condition notCond = new NotCondition(cond);

        SimulationUserCode code1 = new SimulationUserCode();
        code1.add(UserMethod.BEFORE_REQUEST, "System.out.println(\"Volumen actual es \" + <%GET(S.litrosIntroducidos)%> + \" litros\");" +	
        		"System.out.println(\"Capacidad bidon es \" + <%GET(A0.capacidadBidon)%> + \" litros\");" +
 				"return true;");
        MultiChoiceFlow mul1 = (MultiChoiceFlow)factory.getFlowInstance("MultiChoiceFlow", code1);

        SimulationUserCode code2 = new SimulationUserCode();
        code2.add(UserMethod.AFTER_FINALIZE, "if (<%GET(S.litrosIntroducidos)%> < <%GET(A0.capacidadBidon)%>) {" +
				"double random = Math.random() * 50; " +
				"<%SET(S.litrosIntroducidos, <%GET(S.litrosIntroducidos)%> + random)%>;" +		
				"System.out.println(\"Introducimos \" + random + \" litros y nuestro volumen actual es \" + <%GET(S.litrosIntroducidos)%> + \" litros\");" +
	  	  "}");
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", code2, act0);
        
        SimulationUserCode code3 = new SimulationUserCode();
        code3.add(UserMethod.BEFORE_REQUEST, 	"<%SET(S.litrosIntroducidos, 0)%>;" +
 				"<%SET(S.enviosRealizados, <%GET(S.enviosRealizados)%> + 1)%>;" +
 				"System.out.println(\"Nuevo envio realizado\");" +
 				"return true;");

        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", code3, act1);
        
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
