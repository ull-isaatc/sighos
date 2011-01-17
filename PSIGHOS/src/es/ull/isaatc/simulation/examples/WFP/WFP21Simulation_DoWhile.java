package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.core.ElementType;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.DoWhileFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationUserCode;
import es.ull.isaatc.simulation.factory.UserMethod;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 21. Example 2: Revelado fotográfico (implemented with a do-while structure)
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP21Simulation_DoWhile extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_DoWhile(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP21: Structured Loop. EjReveladoFotografico", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Revelar foto", wg, false);

        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        Condition cond = factory.getCustomizedConditionInstance("", "<%GET(@E.fotosReveladas)%> < 10");
        
        SimulationUserCode code1 = new SimulationUserCode();
        code1.add(UserMethod.AFTER_FINALIZE, "<%SET(@E.fotosReveladas, <%GET(@E.fotosReveladas)%> + 1)%>;"
				+ "System.out.println(\"E\" + e.getIdentifier() + \": \" + <%GET(@E.fotosReveladas)%> + \" fotos reveladas.\");"
				);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", code1, act0);
        DoWhileFlow root = (DoWhileFlow)factory.getFlowInstance("DoWhileFlow", sin1, cond);

        ElementType et = getDefElementType("Cliente");
        et.addElementVar("fotosReveladas", 0);
        getDefGenerator(et, root);
 	}

}
