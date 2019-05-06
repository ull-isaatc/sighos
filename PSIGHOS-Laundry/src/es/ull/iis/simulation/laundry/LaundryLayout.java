package es.ull.iis.simulation.laundry;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Node;

/**
 * The layout of the laundry. Defines all the locations where the clothes are processed.
 * @author Iván Castilla Rodríguez
 *
 */
public class LaundryLayout {
	// Exit
	private final static String STR_EXIT = "EXIT"; 
	// Start
	private final static String STR_START = "START";
	private final static long LAUNDRY_BASKET_DELAY = 1 * 60;
	
	public final static int N_WASHING_LINES = LaundrySimulation.SIMPLE ? 1 : 2;
	// Washing stage
	private final static String STR_WASHING_STAGE = "D";
	private final static int WASHING_STAGE_CAPACITY = 1;
	private final static long[] WASHING_STAGE_DELAY = {3 * 60, 6 * 60};
	private final static int[] N_WASHING_STAGES = {12, 6};
	
	// Dehydrators
	private final static String STR_DEHYDRATOR = "LH";
	private final static long[] DEHYDRATOR_DELAY = WASHING_STAGE_DELAY;
	private final static int DEHYDRATOR_CAPACITY = 1;
	
	// Waiting units
	private final static String STR_WAIT_UNIT = "E";
	private final static int[] WAIT_UNIT_CAPACITY = {LaundrySimulation.SIMPLE ? 2 : 1, 2};
	
	// Dryers
	private final static String STR_DRYER = "LS";
	private final static int N_DRYERS = 3;
	private final static int DRYER_CAPACITY = 2;
	/** Drying time + 30 seconds for loading the dryer */
	final static long[] DRYER_DELAY = {4 * 60 + 30, 18 * 60 + 30, 9 * 60 + 30, 14 * 60 + 30, 4 * 60 + 30};
	
	// Downloaders
	private final static String STR_DOWNLOADER = "G";
	private final static int[] N_DOWNLOADER_STAGES = {6, 6};
	private final static int DOWNLOADER_CAPACITY = 1;
	// The whole downloader delay should be 5 seconds, but the simulation avoids finer grain
	private final static long[] DOWNLOADER_STAGE_DELAY = {1, 1};
	
	// Bag warehouse
	private final static String STR_WAREHOUSE = "S";
	private final static int N_WAREHOUSE_LINES = 11;
	private final static int N_WAREHOUSE_STAGES_PER_LINE = 8;
	private final static int WAREHOUSE_CAPACITY = 1;
	// The whole warehouse delay should be 30 seconds, but the simulation avoids finer grain
	private final static long WAREHOUSE_STAGE_DELAY = 4;
	
	private final Node initBasket;
	private final Node endBasket; 
	private final Node[][] washingStage;
	private final Node[][] downloaderStage;
	private final Node[][] warehouseStage;
	private final Node[] dehydrators;
	private final Node[] waitingUnits;
	private final Node[] dryers;
	private final boolean simple;
	
	public LaundryLayout(boolean simple) {
		this.simple = simple;
		initBasket = new Node(STR_START, LAUNDRY_BASKET_DELAY);
		endBasket = new Node(STR_EXIT);
		waitingUnits = new Node[N_WASHING_LINES];
		
		if (simple) {
			washingStage = null;
			dehydrators = null;
			downloaderStage = null;
			warehouseStage = null;
			waitingUnits[0] = new Node(STR_WAIT_UNIT + "_1", 0, WAIT_UNIT_CAPACITY[0]);
			initBasket.linkTo(waitingUnits[0]);
		}
		else {
			warehouseStage = new Node[N_WAREHOUSE_LINES][];
			for (int i = 0; i < N_WAREHOUSE_LINES; i++) {
				warehouseStage[i] = createTunnel(STR_WAREHOUSE + "_" + (i + 1), N_WAREHOUSE_STAGES_PER_LINE, WAREHOUSE_STAGE_DELAY, WAREHOUSE_CAPACITY);
				initBasket.linkTo(warehouseStage[i][0]);
			}
			washingStage = new Node[N_WASHING_LINES][];
			downloaderStage = new Node[N_WASHING_LINES][];
			dehydrators = new Node[N_WASHING_LINES];
			for (int i = 0; i < N_WASHING_LINES; i++) {
				washingStage[i] = createTunnel(STR_WASHING_STAGE + "_" + (i + 1), N_WASHING_STAGES[i], WASHING_STAGE_DELAY[i], WASHING_STAGE_CAPACITY);
				downloaderStage[i] = createTunnel(STR_DOWNLOADER + "_" + (i + 1), N_DOWNLOADER_STAGES[i], DOWNLOADER_STAGE_DELAY[i], DOWNLOADER_CAPACITY);
				waitingUnits[i] = new Node(STR_WAIT_UNIT + "_" + (i+1), 0, WAIT_UNIT_CAPACITY[i]);
				dehydrators[i] = new Node(STR_DEHYDRATOR + "_" + (i+1), DEHYDRATOR_DELAY[i], DEHYDRATOR_CAPACITY);
				for (int j = 0; j < N_WAREHOUSE_LINES; j++) {
					warehouseStage[i][N_WAREHOUSE_STAGES_PER_LINE - 1].linkTo(downloaderStage[i][0]);
				}
				downloaderStage[i][N_DOWNLOADER_STAGES[i] - 1].linkTo(washingStage[i][0]);
				washingStage[i][N_WASHING_STAGES[i] - 1].linkTo(dehydrators[i]).linkTo(waitingUnits[i]);
			}
		}

		dryers = new Node[N_DRYERS];
		for (int i = 0; i < N_DRYERS; i++) {
			dryers[i] = new Node(STR_DRYER + "_" + i, new DryTime(), DRYER_CAPACITY);
			dryers[i].linkTo(endBasket);
			for (int line = 0; line < N_WASHING_LINES; line++) {
				waitingUnits[line].linkTo(dryers[i]);					
			}
		}
	}
	
	
	/**
	 * @return the simple
	 */
	public boolean isSimple() {
		return simple;
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
	 * @return the downloaderStage
	 */
	public Node[][] getDownloaderStages() {
		return downloaderStage;
	}

	/**
	 * @return the downloaderStage
	 */
	public Node[] getDownloaderStage(int line) {
		return downloaderStage[line];
	}


	/**
	 * @return the warehouseStage
	 */
	public Node[][] getWarehouseStages() {
		return warehouseStage;
	}

	/**
	 * @return the warehouseStage
	 */
	public Node[] getWarehouseStage(int line) {
		return warehouseStage[line];
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

	private Node[] createTunnel(String stageName, int nStages, long delay, int capacity) {
		final Node[] stages = new Node[nStages];
		stages[0] = new Node(stageName + "_0", delay, capacity);
		for (int i = 1; i < nStages; i++) {
			stages[i] = new Node(stageName + "_" + i, delay, capacity);
			stages[i-1].linkTo(stages[i]);
		}
		return stages;
	}
	
	public boolean inWaitingUnit(final Location currentLocation) {
		for (final Node waitingUnit : waitingUnits) {
			if (waitingUnit.equals(currentLocation))
				return true;
		}
		return false;
	}
	
	public boolean inInitBasket(final Location currentLocation) {
		return initBasket.equals(currentLocation);
	}
	
	public boolean inDryer(final Location currentLocation) {
		for (final Node dryer : dryers) {
			if (dryer.equals(currentLocation))
				return true;
		}
		return false;
	}
	
	public boolean inWarehouseFinalStage(final Location currentLocation) {
		for (final Node[] warehouseLine : warehouseStage) {
			if (warehouseLine[N_WAREHOUSE_STAGES_PER_LINE - 1].equals(currentLocation))
				return true;
		}
		return false;			
	}
	
	/**
	 * A time function to compute time to dry depending on the product type
	 * @author Iván Castilla Rodríguez
	 *
	 */
	static class DryTime extends TimeFunction {
		
		public DryTime() {
		}

		@Override
		public double getValue(TimeFunctionParams params) {
			final Bag bag = (Bag)params;
			return bag.getProductType().getDryTime();
		}

		@Override
		public void setParameters(Object... params) {
		}
		
	}
}