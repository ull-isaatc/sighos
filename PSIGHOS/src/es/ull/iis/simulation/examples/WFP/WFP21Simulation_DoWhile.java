package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationUserCode;
import es.ull.iis.simulation.core.factory.UserMethod;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.DoWhileFlow;

/**
 * WFP 21. Example 2: Revelado fotogr�fico (implemented with a do-while structure)
 * @author Yeray Callero
 * @author Iv�n Castilla
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
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        Condition cond = factory.getCustomizedConditionInstance("", "<%GET(@E.fotosReveladas)%> < 10");
        
        SimulationUserCode code1 = new SimulationUserCode();
        code1.add(UserMethod.AFTER_FINALIZE, "<%SET(@E.fotosReveladas, <%GET(@E.fotosReveladas)%> + 1)%>;"
				+ "System.out.println(\"E\" + e.getIdentifier() + \": \" + <%GET(@E.fotosReveladas)%> + \" fotos reveladas.\");"
				);
    	ActivityFlow<?,?> act0 = getDefActivity(code1, "Revelar foto", wg, false);

        DoWhileFlow root = (DoWhileFlow)factory.getFlowInstance("DoWhileFlow", act0, cond);

        ElementType et = getDefElementType("Cliente");
        et.addElementVar("fotosReveladas", 0);
        getDefGenerator(et, root);
 	}

}
