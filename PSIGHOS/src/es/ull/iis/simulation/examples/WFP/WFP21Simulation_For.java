package es.ull.iis.simulation.examples.WFP;


import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ForLoopFlow;
import es.ull.iis.function.TimeFunctionFactory;

/**
 * WFP 21. Example 2: Revelado fotográfico (implemented with a for structure)
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP21Simulation_For extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_For(int id, boolean detailed) {
		super(id, "WFP21: Structured Loop. EjReveladoFotografico", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);        
		ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
    	
    	ActivityFlow act0 = getDefActivity("Revelar foto", wg, false);

        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        ForLoopFlow root = new ForLoopFlow(simul, act0, TimeFunctionFactory.getInstance("ConstantVariate", 2));

        ElementType et = getDefElementType("Cliente");
        getDefGenerator(et, root);
    	return simul;
	}
}
