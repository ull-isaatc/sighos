package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.StructuredDiscriminatorFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

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
	public WFP09Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP9: Structured Discriminator. EjParoCardiaco", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Doctor");
	   	
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow<?,?> act0 = getDefActivity("Comprobar respiracion", 0, wg, false);
        ActivityFlow<?,?> act1 = getDefActivity("Comprobar pulso", 1, wg, false);
        ActivityFlow<?,?> act2 = getDefActivity("Masaje cardiaco", 2, wg, false);
        
        getDefResource("Doctor 1", rt0);        
        getDefResource("Doctor 2", rt0);        
        
        StructuredDiscriminatorFlow root = (StructuredDiscriminatorFlow)factory.getFlowInstance("StructuredDiscriminatorFlow");
              
        root.addBranch(act0);
        root.addBranch(act1);
        root.link(act2);
        
        getDefGenerator(getDefElementType("Paciente"), root);
		
	}

}
