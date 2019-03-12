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

	@Override
	protected void createModel() {
		ResourceType rt0 = getDefResourceType("Bank teller");
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
        
        getDefResource("BankTeller1", rt0);
        getDefResource("BankTeller2", rt0);
        getDefResource("BankTeller3", rt0);

        // FIXME: NO FUNCIONABA!!!
    	final ActivityFlow act0 = new TestActivityFlow("Verify account", 0, wg, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance fe) {
    			switchPass();
    			return isPass() && super.beforeRequest(fe);
    		}
    	};
    	final ActivityFlow act1 = getDefActivity("Obtain card details", wg, false);
        
        
        act0.link(act1);

        getDefGenerator(getDefElementType("Client"), act0);
	}
}
