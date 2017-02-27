package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.StructuredDiscriminatorFlow;

/**
 * WFP 9. Paro cardiaco
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP09Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP09Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP9: Structured Discriminator. EjParoCardiaco", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Model createModel() {
		model = new Model(id, description, SIMUNIT, SIMSTART, SIMEND);        
		ResourceType rt0 = getDefResourceType("Doctor");
	   	
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow act0 = getDefActivity("Comprobar respiracion", 0, wg, false);
        ActivityFlow act1 = getDefActivity("Comprobar pulso", 1, wg, false);
        ActivityFlow act2 = getDefActivity("Masaje cardiaco", 2, wg, false);
        
        getDefResource("Doctor 1", rt0);        
        getDefResource("Doctor 2", rt0);        
        
        StructuredDiscriminatorFlow root = new StructuredDiscriminatorFlow(model);
              
        root.addBranch(act0);
        root.addBranch(act1);
        root.link(act2);
        
        getDefGenerator(getDefElementType("Paciente"), root);
        return model;

	}

}
