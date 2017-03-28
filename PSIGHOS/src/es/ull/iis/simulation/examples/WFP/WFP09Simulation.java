package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Simulation;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.StructuredDiscriminatorFlow;

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
	 * @param detailed
	 */
	public WFP09Simulation(int id, boolean detailed) {
		super(id, "WFP9: Structured Discriminator. EjParoCardiaco", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new Simulation(id, description, SIMUNIT, SIMSTART, SIMEND);        
		ResourceType rt0 = getDefResourceType("Doctor");
	   	
        WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow act0 = getDefActivity("Comprobar respiracion", 0, wg, false);
        ActivityFlow act1 = getDefActivity("Comprobar pulso", 1, wg, false);
        ActivityFlow act2 = getDefActivity("Masaje cardiaco", 2, wg, false);
        
        getDefResource("Doctor 1", rt0);        
        getDefResource("Doctor 2", rt0);        
        
        StructuredDiscriminatorFlow root = new StructuredDiscriminatorFlow(simul);
              
        root.addBranch(act0);
        root.addBranch(act1);
        root.link(act2);
        
        getDefGenerator(getDefElementType("Paciente"), root);
        return simul;

	}

}
