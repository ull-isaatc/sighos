package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.WhileDoFlow;

/**
 * WFP 21. Example 2: Revelado fotográfico (implemented with a while-do structure)
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP21Simulation_WhileDo extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_WhileDo(SimulationType type, int id, boolean detailed) {
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
        WhileDoFlow root = (WhileDoFlow)factory.getFlowInstance("WhileDoFlow", sin1, cond);

        ElementType et = getDefElementType("Cliente");
        et.addElementVar("fotosReveladas", 0);
        getDefGenerator(et, root);
	}
}
