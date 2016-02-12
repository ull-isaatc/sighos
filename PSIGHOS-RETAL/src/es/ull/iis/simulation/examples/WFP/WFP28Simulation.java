package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.DiscriminatorFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
        
        WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});
	   	
        TimeDrivenActivity act0 = getDefTimeDrivenActivity("Confirmar llegada delegacion", 2, wg0, false);
        TimeDrivenActivity act1 = getDefTimeDrivenActivity("Chequeo de seguridad", 3, wg1, false);
        TimeDrivenActivity act2 = getDefTimeDrivenActivity("Preparacion para nueva delegacion", 2, wg0, false);

        getDefResource("Asistente 1", rt0);        
        getDefResource("Asistente 2", rt0);
        getDefResource("Segurita 1", rt1);
        getDefResource("Segurita 2", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        DiscriminatorFlow dis1 = (DiscriminatorFlow)factory.getFlowInstance("DiscriminatorFlow");
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(dis1);
        sin2.link(dis1);
        dis1.link(sin3);
        
        getDefGenerator(getDefElementType("Asistente"), root);
	}
}
