package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.MultiMergeFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
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
	   	
		Activity act0 = getDefActivity("Crear destornilladores", 2, wgMa, false);
		Activity act1 = getDefActivity("Crear llaves", 2, wgMa, false);
		Activity act2 = getDefActivity("Crear niveladores", 1, wgMa, false);
		Activity act3 = getDefActivity("Control de calidad", 2, wgEm, false);
        
		getDefResource("Maquina1", rt0);
		getDefResource("Maquina2", rt0);
		getDefResource("Empleado1", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        MultiMergeFlow mulmer1 = (MultiMergeFlow)factory.getFlowInstance("MultiMergeFlow");
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act3);
        
        
        root.link(sin1);
        root.link(sin2);
        root.link(sin3);
        sin1.link(mulmer1);
        sin2.link(mulmer1);
        sin3.link(mulmer1);
        mulmer1.link(sin4);

        getDefGenerator(getDefElementType("Remesa productos"), root);
        
	}
	
}
