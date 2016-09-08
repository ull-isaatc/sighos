package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.core.flow.StructuredPartialJoinFlow;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 30. Expedici�n Cheques
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
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

        TimeDrivenActivity act0 = getDefTimeDrivenActivity("AprobarCuenta", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);
        
        StructuredPartialJoinFlow root = (StructuredPartialJoinFlow)factory.getFlowInstance("StructuredPartialJoinFlow", 2);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin3 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin4 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        root.addBranch(sin1);
        root.addBranch(sin2);
        root.addBranch(sin3);
        root.link(sin4);
        
        getDefGenerator(getDefElementType("Peticion de cheque"), root);
	}
}