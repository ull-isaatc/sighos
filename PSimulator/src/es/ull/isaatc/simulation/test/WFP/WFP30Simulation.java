package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredPartialJoinFlow;

/**
 * WFP 30. Expedición Cheques
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP30Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP30Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP30: EjExpedicionCheques", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Director");
        
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("AprobarCuenta", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);
        
        StructuredPartialJoinFlow root = (StructuredPartialJoinFlow)factory.getFlowInstance(10, "StructuredPartialJoinFlow", 2);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act0);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance(2, "SingleFlow", act0);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance(3, "SingleFlow", act1);
        root.addBranch(sin1);
        root.addBranch(sin2);
        root.addBranch(sin3);
        root.link(sin4);
        
        getDefGenerator(getDefElementType("Peticion de cheque"), root);
	}
}
