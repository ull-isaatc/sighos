package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.MultiMergeFlow;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

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
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Maquina productora");
    	ResourceType rt1 = getDefResourceType("Empleados");
        
        WorkGroup wgMa = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wgEm = factory.getWorkGroupInstance(1, new ResourceType[] {rt1}, new int[] {1});
	   	
		TimeDrivenActivity act0 = getDefTimeDrivenActivity("Crear destornilladores", 2, wgMa, false);
		TimeDrivenActivity act1 = getDefTimeDrivenActivity("Crear llaves", 2, wgMa, false);
		TimeDrivenActivity act2 = getDefTimeDrivenActivity("Crear niveladores", 1, wgMa, false);
		TimeDrivenActivity act3 = getDefTimeDrivenActivity("Control de calidad", 2, wgEm, false);
        
		getDefResource("Maquina1", rt0);
		getDefResource("Maquina2", rt0);
		getDefResource("Empleado1", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        MultiMergeFlow mulmer1 = (MultiMergeFlow)factory.getFlowInstance(11, "MultiMergeFlow");
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance(3, "SingleFlow", act3);
        
        
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
