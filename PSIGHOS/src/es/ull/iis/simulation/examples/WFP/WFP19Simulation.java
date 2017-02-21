package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationUserCode;
import es.ull.iis.simulation.core.factory.UserMethod;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;

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
	 * @param detailed
	 */
	public WFP19Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP19: Cancel Task. EjTarjetaCredito", detailed);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel()
	 */
	@Override
	protected void createModel() {
        ResourceType rt0 = getDefResourceType("Cajero");
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt0}, new int[] {1});
        
        getDefResource("Cajero1", rt0);
        getDefResource("Cajero2", rt0);
        getDefResource("Cajero3", rt0);

        getSimulation().putVar("pass", false);
        SimulationUserCode code1 = new SimulationUserCode();
        // FIXME: NO FUNCIONA!!!
        code1.add(UserMethod.BEFORE_REQUEST, "<%SET(S.pass, !(boolean)<%GET(S.pass)%>)%>;" +
        		"return (boolean)<%GET(S.pass)%> && super.beforeRequest(e);");
    	ActivityFlow act0 = getDefActivity(code1, "Verificar cuenta", wg, false);
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
        
        act0.link(act1);

        getDefGenerator(getDefElementType("Cliente"), act0);
	}
}
