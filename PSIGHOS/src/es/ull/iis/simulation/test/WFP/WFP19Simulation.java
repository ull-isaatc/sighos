package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * WFP 19. Cancel Task: Credit card
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP19Simulation extends WFPTestSimulation {
	private boolean pass;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP19Simulation(int id) {
		super(id, "WFP19: Cancel Task. EjTarjetaCredito");
		pass = false;
	}

	/**
	 * @return the pass
	 */
	public boolean isPass() {
		return pass;
	}
	/**
	 * 
	 */
	public void switchPass() {
		pass = !pass;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Cajero");
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
        
        getDefResource("Cajero1", rt0);
        getDefResource("Cajero2", rt0);
        getDefResource("Cajero3", rt0);

        // FIXME: NO FUNCIONABA!!!
    	ActivityFlow act0 = new ActivityFlow(this, "Verificar cuenta", false, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance fe) {
    			switchPass();
    			return isPass() && super.beforeRequest(fe);
    		}
    	};
    	act0.newWorkGroupAdder(wg).withDelay(DEFACTDURATION[0]).add();
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
        
        act0.link(act1);

        getDefGenerator(getDefElementType("Cliente"), act0);
	}
}
