package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
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
	public WFP19Simulation(int id, boolean detailed) {
		super(id, "WFP19: Cancel Task. EjTarjetaCredito", detailed);
	}

	static class WFP19Model extends Simulation {
		private boolean pass;
		public WFP19Model(int id, String description, TimeUnit unit, long startTs, long endTs) {
			super(id, description, unit, startTs, endTs);
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
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new WFP19Model(id, description, SIMUNIT, SIMSTART, SIMEND);        
		ResourceType rt0 = getDefResourceType("Cajero");
        WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
        
        getDefResource("Cajero1", rt0);
        getDefResource("Cajero2", rt0);
        getDefResource("Cajero3", rt0);

        // FIXME: NO FUNCIONABA!!!
    	ActivityFlow act0 = new ActivityFlow(simul, "Verificar cuenta", false, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance fe) {
    			((WFP19Model)simul).switchPass();
    			return ((WFP19Model)simul).isPass() && super.beforeRequest(fe);
    		}
    	};
    	act0.addWorkGroup(0, wg, DEFACTDURATION[0]);
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
        
        act0.link(act1);

        getDefGenerator(getDefElementType("Cliente"), act0);
    	return simul;
	}
}
