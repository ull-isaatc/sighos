package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.StructuredPartialJoinFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 30. Expedición Cheques
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
// TODO: Check carefully
public class WFP30Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP30Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP30: EjExpedicionCheques", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Director");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow<?,?> act0_0 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow<?,?> act0_1 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow<?,?> act0_2 = getDefActivity("AprobarCuenta", wg, false);
    	ActivityFlow<?,?> act1 = getDefActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);
        
        StructuredPartialJoinFlow root = (StructuredPartialJoinFlow)factory.getFlowInstance("StructuredPartialJoinFlow", 2);
        root.addBranch(act0_0);
        root.addBranch(act0_1);
        root.addBranch(act0_2);
        root.link(act1);
        
        getDefGenerator(getDefElementType("Peticion de cheque"), root);
	}
}
