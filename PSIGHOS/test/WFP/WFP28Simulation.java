package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.DiscriminatorFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;

/**
 * WFP 28. Comprobacion credenciales
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP28Simulation extends WFPTestSimulation {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP28Simulation(int id) {
		super(id, "WFP28: Blocking Discriminator. EjComprobacionCredenciales");
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Asistente");
        ResourceType rt1 = getDefResourceType("Personal Seguridad");
        
        WorkGroup wg0 = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = new WorkGroup(this, new ResourceType[] {rt1}, new int[] {1});
	   	
        ActivityFlow act0 = getDefActivity("Confirmar llegada delegacion", 2, wg0, false);
        ActivityFlow act1 = getDefActivity("Chequeo de seguridad", 3, wg1, false);
        ActivityFlow act2 = getDefActivity("Preparacion para nueva delegacion", 2, wg0, false);

        getDefResource("Asistente 1", rt0);        
        getDefResource("Asistente 2", rt0);
        getDefResource("Segurita 1", rt1);
        getDefResource("Segurita 2", rt1);
        
        ParallelFlow root = new ParallelFlow(this);
        DiscriminatorFlow dis1 = new DiscriminatorFlow(this);
        
        root.link(act0);
        root.link(act1);
        act0.link(dis1);
        act1.link(dis1);
        dis1.link(act2);
        
        getDefGenerator(getDefElementType("Asistente"), root);
	}
}
