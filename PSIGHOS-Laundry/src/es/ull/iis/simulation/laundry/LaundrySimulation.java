/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.location.MoveFlow;

/**
 * A model of the HUC's industrial laundry
 * @author Iván Castilla Rodríguez
 *
 */
public class LaundrySimulation extends Simulation {
	public final static boolean SIMPLE = false;
	private final static TimeUnit TIME_UNIT = TimeUnit.SECOND;
	private final static String STR_DESC = "Hospital Laundry";
	
	private final static int SIMULATED_SHIFTS = 1;
	private final static long END_TS = (SIMPLE ? 1 : 8) * 60 * 60 * SIMULATED_SHIFTS;

	// Bags
	public final static int BAG_SIZE = 1;
	
	private final static long INSPECTION_RATE = 10 * 60;
	private final static long INJECTION_RATE = 60 * 60;
	private final LaundryManager manager;
	
	/**
	 * @param id
	 */
	public LaundrySimulation(int id) {
		super(id, STR_DESC, TIME_UNIT, 0, END_TS);
		
		final ProductsType[] products = ProductsType.values();
		for (int i = 0; i < ProductsType.values().length; i++) {			
			products[i].setElementType(new ElementType(this, products[i].name(), products[i].getPriority()));
			products[i].setDryTime(LaundryLayout.DRYER_DELAY[i]);
		}

		final LaundryLayout layout = new LaundryLayout(SIMPLE);
		
		final WashingRouter router = new WashingRouter(layout);
		
		final MoveFlow moveToEndFlow = new MoveFlow(this, "Move to end", layout.getEndBasket(), router);
		
		final BagsGenerator generator = new BagsGenerator(this, moveToEndFlow, layout.getInitBasket());
		
		manager = new LaundryManager(this, generator, INSPECTION_RATE, INJECTION_RATE);
	}
	
	@Override
	public void init() {
		super.init();
		addEvent(manager.onCreate(getTs()));
	}
}
