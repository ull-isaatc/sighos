/**
 * 
 */
package es.ull.iis.simulation.laundry;

import java.util.ArrayList;
import java.util.EnumMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
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
	public final static boolean SIMPLE = true;
	private final static TimeUnit TIME_UNIT = TimeUnit.MINUTE;
	private final static String STR_DESC = "Hospital Laundry";
	
	private final static int SIMULATED_SHIFTS = 1;
	private final static long END_TS = (SIMPLE ? 1 : 8) * 60 * SIMULATED_SHIFTS;
	
	// Laundry baskets
	private final static String STR_LAUNDRY_BASKET = "Laundry basket";
	private final static long LAUNDRY_BASKET_DELAY = 1;
	
	private final static int N_WASHING_LINES = SIMPLE ? 1 : 2;
	// Washing stage
	private final static String STR_WASHING_STAGE = "Washing stage";
	private final static int WASHING_STAGE_CAPACITY = 1;
	private final static long[] WASHING_STAGE_DELAY = {3, 6};
	private final static int[] N_WASHING_STAGES = {12, 6};
	
	// Dehydrators
	private final static String STR_DEHYDRATOR = "Dehydrator";
	private final static long[] DEHYDRATOR_DELAY = WASHING_STAGE_DELAY;
	private final static int DEHYDRATOR_CAPACITY = 1;
	
	// Waiting units
	private final static String STR_WAIT_UNIT = "Waiting unit";
	private final static int[] WAIT_UNIT_CAPACITY = {1, 2};
	
	// Dryers
	private final static String STR_DRYER = "Dryer";
	private final static int N_DRYERS = 3;
	private final static int DRYER_CAPACITY = 2;
	private final static long[] DRYER_DELAY = {4, 9, 14, 14, 4};
	
	// Dehydratation distribution
	private final static String STR_DEHYDRAT_DIST = "Distribution after dehydration";
	
	// Bags
	public final static int BAG_SIZE = 1;
	
	private final static long INSPECTION_RATE = 10;
	private final LaundryManager manager;
	
	/**
	 * @param id
	 */
	public LaundrySimulation(int id) {
		super(id, STR_DESC, TIME_UNIT, 0, END_TS);
		
		final ProductsType[] products = ProductsType.values();
		for (int i = 0; i < ProductsType.values().length; i++) {			
			products[i].setElementType(new ElementType(this, products[i].name(), products[i].getPriority()));
		}

		final WashingRouter router = new WashingRouter(SIMPLE);

		
		final MoveFlow[] moveToWaitingUnitFlow = new MoveFlow[N_WASHING_LINES];
		final BagsGenerator generator;
		
		if (SIMPLE) {
			moveToWaitingUnitFlow[0] = new MoveFlow(this, "Move through washing line", router.getWaitingUnits(0), router);
			generator = new BagsGenerator(this, moveToWaitingUnitFlow[0], router.getInitBasket());
		}
		else {
			final ExclusiveChoiceFlow washingSelectionFlow = new ExclusiveChoiceFlow(this);
			generator = new BagsGenerator(this, washingSelectionFlow, router.getInitBasket());
			for (int line = 0; line < N_WASHING_LINES; line++) {
				final Node washingLineStart = router.getWashingStage(line)[0];
				final Condition condWashing = new Condition() {
					public boolean check(ElementInstance ei) {
						return(washingLineStart.equals(((Bag)ei.getElement()).getWashingStage()));
					}
				};
				final MoveFlow moveToWashFlow = new MoveFlow(this, "Move to washing line " + (line + 1), washingLineStart, router);
				moveToWaitingUnitFlow[line] = new MoveFlow(this, "Move through washing line", router.getWaitingUnits(line), router);
				washingSelectionFlow.link(moveToWashFlow, condWashing).link(moveToWaitingUnitFlow[line]);
			}
		}
		manager = new LaundryManager(this, generator, INSPECTION_RATE);
		
		final DehydratedDistribution dryerDistributor = new DehydratedDistribution(router.getDryers(), router.getWaitingUnits());
		
		final WaitForSignalFlow waitFlow = new WaitForSignalFlow(this, STR_DEHYDRAT_DIST, dryerDistributor);

		final ExclusiveChoiceFlow dryerSelectionFlow = new ExclusiveChoiceFlow(this);
		final Condition[] condDryer = new Condition[N_DRYERS];
		final MoveFlow[] moveToDryerFlow = new MoveFlow[N_DRYERS];
		final MoveFlow moveToEndFlow = new MoveFlow(this, "Move to end", router.getEndBasket(), router) {
			@Override
			public boolean beforeRequest(ElementInstance ei) {
				dryerDistributor.notifyLeaving(waitFlow, ei);
				return super.beforeRequest(ei);
			}
		};
		for (int i = 0; i < N_DRYERS; i++) {
			final Node dryer = router.getDryers()[i];
			condDryer[i] = new Condition() {
				@Override
				public boolean check(ElementInstance ei) {
					return (dryer.equals(((Bag)ei.getElement()).getDryer()));
				}
			};
			moveToDryerFlow[i] = new MoveFlow(this, "Move to dryer " + i, dryer, router);
			dryerSelectionFlow.link(moveToDryerFlow[i], condDryer[i]).link(moveToEndFlow);
		}

		for (int line = 0; line < N_WASHING_LINES; line++) {
			moveToWaitingUnitFlow[line].link(waitFlow).link(dryerSelectionFlow);
		}
	}
	
	@Override
	public void init() {
		super.init();
		addEvent(manager.onCreate(getTs()));
	}
	
	private class WashingRouter implements Router {
		private final Node initBasket;
		private final Node endBasket; 
		private final Node[][] washingStage;
		private final Node[] dehydrators;
		private final Node[] waitingUnits;
		private final Node[] dryers;
		
		public WashingRouter(boolean simple) {
			initBasket = new Node(STR_LAUNDRY_BASKET, LAUNDRY_BASKET_DELAY);
			endBasket = new Node(STR_LAUNDRY_BASKET + " at end");
			waitingUnits = new Node[N_WASHING_LINES];
			
			if (simple) {
				washingStage = null;
				dehydrators = null;
				waitingUnits[0] = new Node(STR_WAIT_UNIT + "_1", 0, WAIT_UNIT_CAPACITY[0]);
				initBasket.linkTo(waitingUnits[0]);
			}
			else {
				washingStage = new Node[N_WASHING_LINES][];
				dehydrators = new Node[N_WASHING_LINES];
				for (int i = 0; i < N_WASHING_LINES; i++) {
					washingStage[i] = createTunnel(STR_WASHING_STAGE + "_" + (i + 1), N_WASHING_STAGES[i], WASHING_STAGE_DELAY[i], WASHING_STAGE_CAPACITY);		
					waitingUnits[i] = new Node(STR_WAIT_UNIT + "_" + (i+1), 0, WAIT_UNIT_CAPACITY[i]);
					dehydrators[i] = new Node(STR_DEHYDRATOR + "_" + (i+1), DEHYDRATOR_DELAY[i], DEHYDRATOR_CAPACITY);
					initBasket.linkTo(washingStage[i][0]);
					washingStage[i][N_WASHING_STAGES[0] - 1].linkTo(dehydrators[i]).linkTo(waitingUnits[i]);
				}
			}
				

			dryers = new Node[N_DRYERS];
			for (int i = 0; i < N_DRYERS; i++) {
				dryers[i] = new Node(STR_DRYER + "_" + i, new DryTime(), DRYER_CAPACITY);
				for (int line = 0; line < N_WASHING_LINES; line++) {
					waitingUnits[line].linkTo(dryers[i]).linkTo(endBasket);					
				}
			}
		}
		
		
		/**
		 * @return the initBasket
		 */
		public Node getInitBasket() {
			return initBasket;
		}


		/**
		 * @return the endBasket
		 */
		public Node getEndBasket() {
			return endBasket;
		}


		/**
		 * @return the washingStage
		 */
		public Node[] getWashingStage(int line) {
			return washingStage[line];
		}


		/**
		 * @return the dehydrator1
		 */
		public Node getDehydrator(int line) {
			return dehydrators[line];
		}


		/**
		 * @return the waitingUnit1
		 */
		public Node getWaitingUnits(int line) {
			return waitingUnits[line];
		}

		/**
		 * @return the waitingUnit1
		 */
		public Node[] getWaitingUnits() {
			return waitingUnits;
		}


		/**
		 * @return the dryers
		 */
		public Node[] getDryers() {
			return dryers;
		}

		private boolean inWaitingUnit(Location currentLocation) {
			for (final Node waitingUnit : waitingUnits) {
				if (waitingUnit.equals(currentLocation))
					return true;
			}
			return false;
		}
		
		@Override
		public Location getNextLocationTo(Movable entity, Location destination) {
			ArrayList<Location> links = entity.getLocation().getLinkedTo();
			if (links.size() == 1) {
				return links.get(0);
			}
			else if (inWaitingUnit(entity.getLocation())) {
				return ((Bag)entity).getDryer();
			}
			return null;
		}

		private Node[] createTunnel(String stageName, int nStages, long delay, int capacity) {
			final Node[] stages = new Node[nStages];
			stages[0] = new Node(stageName + "_0", delay, capacity);
			for (int i = 1; i < nStages; i++) {
				stages[i] = new Node(stageName + "_" + i, delay, capacity);
				stages[i-1].linkTo(stages[i]);
			}
			return stages;
		}
	}
	
	private class DryTime extends TimeFunction {
		private EnumMap<ProductsType, Long> timeXType;
		
		public DryTime() {
			this.timeXType = new EnumMap<ProductsType, Long>(ProductsType.class);
			for (int i = 0; i < ProductsType.values().length; i++)
				timeXType.put(ProductsType.values()[i], DRYER_DELAY[i]);
		}

		@Override
		public double getValue(TimeFunctionParams params) {
			final Bag bag = (Bag)params;
			return timeXType.get(bag.getProductType());
		}

		@Override
		public void setParameters(Object... params) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
