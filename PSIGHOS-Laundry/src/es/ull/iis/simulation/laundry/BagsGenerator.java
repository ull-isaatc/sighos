/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.SimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla
 *
 */
public class BagsGenerator extends SimulationObject {
	final static private String OBJ_ID = "GEN";
	final private int[]stored;
	final private InitializerFlow flow;
	final private Node initBasket;
	
	public BagsGenerator(LaundrySimulation simul, InitializerFlow flow, Node initBasket) {
		super(simul, 0, OBJ_ID);
		this.flow = flow;
		this.initBasket = initBasket;
		stored = new int[ProductsType.values().length];
	}

	public void addProducts(ProductsType type, int quantity) {
		stored[type.ordinal()] += quantity;
		
	}

	public void createBag(ProductsType type) {
		final Bag elem = new Bag(simul, type, flow, LaundrySimulation.BAG_SIZE, initBasket);
		simul.addEvent(elem.onCreate(simul.getTs()));
	}
	
	public void inject(int[] orders) {
		for (int i = 0; i < orders.length; i++) {
			final ProductsType type = ProductsType.values()[i];
			for (int j = 0; j < orders[i]; j++) {
				createBag(type);
			}
		}
	}
	
	@Override
	protected void assignSimulation(SimulationEngine engine) {
		// Nothing to do		
	}

}
