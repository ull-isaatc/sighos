package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationUserCode;
import es.ull.iis.simulation.factory.UserMethod;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

/**
 * WFP 19. Cancel Task: Credit card
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP19Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
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
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        
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
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", code1, act0);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        
        root.link(sin1);

        getDefGenerator(getDefElementType("Cliente"), root);
	}
}
