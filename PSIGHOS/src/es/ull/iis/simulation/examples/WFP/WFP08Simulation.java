package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.MultiMergeFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 8. Control de calidad
 * @author Yeray Callero
 * @author Iván Castilla
 */
public class WFP08Simulation extends WFPTestSimulationFactory {
	public WFP08Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP8: Multi-Merge. EjControlCalidad", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Maquina productora");
    	ResourceType rt1 = getDefResourceType("Empleados");
        
        WorkGroup wgMa = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgEm = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});
	   	
		ActivityFlow<?,?> act0 = getDefActivity("Crear destornilladores", 2, wgMa, false);
		ActivityFlow<?,?> act1 = getDefActivity("Crear llaves", 2, wgMa, false);
		ActivityFlow<?,?> act2 = getDefActivity("Crear niveladores", 1, wgMa, false);
		ActivityFlow<?,?> act3 = getDefActivity("Control de calidad", 2, wgEm, false);
        
		getDefResource("Maquina1", rt0);
		getDefResource("Maquina2", rt0);
		getDefResource("Empleado1", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        MultiMergeFlow mulmer1 = (MultiMergeFlow)factory.getFlowInstance("MultiMergeFlow");
        
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
