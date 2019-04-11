/**
 * 
 */
package es.ull.iis.simulation.laundry;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.MoveFlow;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Router;

/**
 * @author icasrod
 *
 */
public class LaundrySimulation extends Simulation {
	private final static TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	private final static String STR_DESC = "Hospital Laundry";
	private final static String STR_HANGER = "Hanger";
	
	private final static int SIMULATED_SHIFTS = 1;
	private final static long END_TS = 8 * 60 * SIMULATED_SHIFTS;
	
	// Laundry baskets
	private final static String STR_LAUNDRY_BASKET = "Laundry basket";
	private final static long LAUNDRY_BASKET_DELAY = 1;
	
	// Washing stage
	private final static String STR_WASHING_STAGE = "Washing stage";
	private final static long WASHING_STAGE_DELAY = 3;
	private final static int WASHING_STAGE_CAPACITY = 1;
	private final static int N_WASHING_STAGES = 12;
	
	// Bags
	private final static String STR_LAUNDRY_BAG = "Laundry bag";
	private final static int BAG_SIZE = 1;
	private final static int N_BAGS_PER_HOUR = 50;
	/**
	 * @param id
	 */
	public LaundrySimulation(int id) {
		super(id, STR_DESC, TIME_UNIT, 0, END_TS);
		final ElementType etBag = new ElementType(this, STR_LAUNDRY_BAG);
		final ResourceType rtHanger = new ResourceType(this, STR_HANGER); 
		
		final Node initBasket = new Node(STR_LAUNDRY_BASKET, LAUNDRY_BASKET_DELAY);
		final Node[] washingStage = new Node[N_WASHING_STAGES];
		washingStage[0] = new Node(STR_WASHING_STAGE + "0", WASHING_STAGE_DELAY, WASHING_STAGE_CAPACITY);
		initBasket.linkTo(washingStage[0]);
		for (int i = 1; i < N_WASHING_STAGES; i++) {
			washingStage[i] = new Node(STR_WASHING_STAGE + i, WASHING_STAGE_DELAY, WASHING_STAGE_CAPACITY);
			washingStage[i-1].linkTo(washingStage[i]);
		}
		final Node endBasket = new Node(STR_LAUNDRY_BASKET + " at end");
		washingStage[N_WASHING_STAGES - 1].linkTo(endBasket);
		final InitializerFlow f = new MoveFlow(this, "Move through washing line", endBasket, new WashingRouter());
		
		new TimeDrivenElementGenerator(this, N_BAGS_PER_HOUR, etBag, f, BAG_SIZE, initBasket, SimulationPeriodicCycle.newHourlyCycle(unit));
	}

	private class WashingRouter implements Router {
		public WashingRouter() {
		}
		
		@Override
		public Location getNextLocationTo(Movable entity, Location destination) {
			ArrayList<Location> links = entity.getLocation().getLinkedTo();
			if (links.size() > 0)
				return links.get(0);
			return null;
		}
		
	}
}
