package es.ull.isaatc.simulation.test.WFP;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * WFP 19. Cancel Task: Credit card
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP19Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param description
	 * @param detailed
	 */
	public WFP19Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP19: Cancel Task. EjTarjetaCredito", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Cajero");
        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt0}, new int[] {1});
        
    	TimeDrivenActivity act0 = getDefTimeDrivenActivity("Verificar cuenta", wg, false);
    	TimeDrivenActivity act1 = getDefTimeDrivenActivity("Obtener detalles tarjeta", wg, false);
        
        getDefResource("Cajero1", rt0);
        getDefResource("Cajero2", rt0);
        getDefResource("Cajero3", rt0);

        getSimulation().putVar("pass", false);
        SimulationUserCode code1 = new SimulationUserCode();
        // FIXME: NO FUNCIONA!!!
        code1.add(UserMethod.BEFORE_REQUEST, "<%SET(S.pass, !(boolean)<%GET(S.pass)%>)%>;" +
        		"return (boolean)<%GET(S.pass)%> && super.beforeRequest(e);");
        SingleFlow root = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", code1, act0);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance(1, "SingleFlow", act1);
        
        root.link(sin1);

        getDefGenerator(getDefElementType("Cliente"), root);
	}
}
