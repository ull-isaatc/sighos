package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ForLoopFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 21. Example 2: Revelado fotogr�fico (implemented with a for structure)
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP21Simulation_For extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_For(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP21: Structured Loop. EjReveladoFotografico", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
    	
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Revelar foto", wg, false);

        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        ForLoopFlow root = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sin1, TimeFunctionFactory.getInstance("ConstantVariate", 2));

        ElementType et = getDefElementType("Cliente");
        getDefGenerator(et, root);
	}
}
