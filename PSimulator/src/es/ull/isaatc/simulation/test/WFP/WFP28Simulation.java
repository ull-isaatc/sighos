package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.DiscriminatorFlow;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * WFP 28. Comprobacion credenciales
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP28Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP28Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP28: Blocking Discriminator. EjComprobacionCredenciales", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Asistente");
        ResourceType rt1 = getDefResourceType("Personal Seguridad");
        
        WorkGroup wg0 = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = factory.getWorkGroupInstance(1, new ResourceType[] {rt1}, new int[] {1});
	   	
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Confirmar llegada delegacion", 2, wg0, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Chequeo de seguridad", 3, wg1, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Preparacion para nueva delegacion", 2, wg0, false);

        getDefResource("Asistente 1", rt0);        
        getDefResource("Asistente 2", rt0);
        getDefResource("Segurita 1", rt1);
        getDefResource("Segurita 2", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act2);
        DiscriminatorFlow dis1 = (DiscriminatorFlow)factory.getFlowInstance(11, "DiscriminatorFlow");
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(dis1);
        sin2.link(dis1);
        dis1.link(sin3);
        
        getDefGenerator(getDefElementType("Asistente"), root);
	}
}
