package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredDiscriminatorFlow;

/**
 * WFP 9. Paro cardiaco
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP09Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP09Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP9: Structured Discriminator. EjParoCardiaco", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Doctor");
	   	
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Comprobar respiracion", 0, wg, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Comprobar pulso", 1, wg, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Masaje cardiaco", 2, wg, false);
        
        getDefResource("Doctor 1", rt0);        
        getDefResource("Doctor 2", rt0);        
        
        StructuredDiscriminatorFlow root = (StructuredDiscriminatorFlow)factory.getFlowInstance(10, "StructuredDiscriminatorFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
              
        root.addBranch(sin1);
        root.addBranch(sin2);
        root.link(sin3);
        
        getDefGenerator(getDefElementType("Paciente"), root);
		
	}

}
