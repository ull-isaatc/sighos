package es.ull.iis.simulation.examples.WFP;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;
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
	public WFP19Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP19: Cancel Task. EjTarjetaCredito", detailed);
	}

	static class WFP19Model extends Model {
		private boolean pass;
		public WFP19Model(TimeUnit unit) {
			super(unit);
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
	protected Model createModel() {
		model = new WFP19Model(SIMUNIT);        
		ResourceType rt0 = getDefResourceType("Cajero");
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt0}, new int[] {1});
        
        getDefResource("Cajero1", rt0);
        getDefResource("Cajero2", rt0);
        getDefResource("Cajero3", rt0);

        // FIXME: NO FUNCIONABA!!!
    	ActivityFlow act0 = new ActivityFlow(model, "Verificar cuenta", false, false) {
    		@Override
    		public boolean beforeRequest(FlowExecutor fe) {
    			((WFP19Model)model).switchPass();
    			return ((WFP19Model)model).isPass() && super.beforeRequest(fe);
    		}
    	};
    	act0.addWorkGroup(0, wg, DEFACTDURATION[0]);
    	ActivityFlow act1 = getDefActivity("Obtener detalles tarjeta", wg, false);
        
        
        act0.link(act1);

        getDefGenerator(getDefElementType("Cliente"), act0);
    	return model;
	}
}
