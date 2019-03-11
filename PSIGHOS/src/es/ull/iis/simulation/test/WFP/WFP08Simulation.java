package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.MultiMergeFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;

/**
 * WFP 8. Control de calidad
 * @author Yeray Callero
 * @author Iván Castilla
 */
public class WFP08Simulation extends WFPTestSimulation {
	public WFP08Simulation(int id) {
		super(id, "WFP8: Multi-Merge. EjControlCalidad");
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Maquina productora");
    	ResourceType rt1 = getDefResourceType("Empleados");
        
        WorkGroup wgMa = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgEm = new WorkGroup(this, new ResourceType[] {rt1}, new int[] {1});
	   	
		ActivityFlow act0 = getDefActivity("Crear destornilladores", 2, wgMa, false);
		ActivityFlow act1 = getDefActivity("Crear llaves", 2, wgMa, false);
		ActivityFlow act2 = getDefActivity("Crear niveladores", 1, wgMa, false);
		ActivityFlow act3 = getDefActivity("Control de calidad", 2, wgEm, false);
        
		getDefResource("Maquina1", rt0);
		getDefResource("Maquina2", rt0);
		getDefResource("Empleado1", rt1);
        
        ParallelFlow root = new ParallelFlow(this);
        MultiMergeFlow mulmer1 = new MultiMergeFlow(this);
        
        root.link(act0);
        root.link(act1);
        root.link(act2);
        act0.link(mulmer1);
        act1.link(mulmer1);
        act2.link(mulmer1);
        mulmer1.link(act3);

        getDefGenerator(getDefElementType("Remesa productos"), root);

	}
	
}
