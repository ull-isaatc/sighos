package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.DiscriminatorFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;

/**
 * WFP 28. Comprobacion credenciales
 * @author Yeray Callero
 * @author Iv�n Castilla
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
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Asistente");
        ResourceType rt1 = getDefResourceType("Personal Seguridad");
        
        WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});
	   	
        ActivityFlow<?,?> act0 = getDefActivity("Confirmar llegada delegacion", 2, wg0, false);
        ActivityFlow<?,?> act1 = getDefActivity("Chequeo de seguridad", 3, wg1, false);
        ActivityFlow<?,?> act2 = getDefActivity("Preparacion para nueva delegacion", 2, wg0, false);

        getDefResource("Asistente 1", rt0);        
        getDefResource("Asistente 2", rt0);
        getDefResource("Segurita 1", rt1);
        getDefResource("Segurita 2", rt1);
        
        ParallelFlow root = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
        DiscriminatorFlow dis1 = (DiscriminatorFlow)factory.getFlowInstance("DiscriminatorFlow");
        
        root.link(act0);
        root.link(act1);
        act0.link(dis1);
        act1.link(dis1);
        dis1.link(act2);
        
        getDefGenerator(getDefElementType("Asistente"), root);
	}
}
