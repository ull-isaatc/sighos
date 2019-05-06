package es.ull.iis.simulation.laundry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Router;

class WashingRouter implements Router {
	private final LaundryLayout layout;
	private DistributionStrategy dryerStrategy;
	private DistributionStrategy washinglineStrategy;
	private DistributionStrategy warehouseStrategy;
	final int totalWaitingCapacity;

	public WashingRouter(LaundryLayout layout) {
		this.layout = layout;
		this.dryerStrategy = new PreserveTypeDryerDistributionStrategy();
		this.washinglineStrategy = new DummyDistributionStrategy(layout.getDownloaderStages());
		this.warehouseStrategy = new ProductTypeWarehouseDistributionStrategy();
		int total = 0;
		for (Node w : layout.getWaitingUnits()) {
			total += w.getCapacity();
		}
		this.totalWaitingCapacity = total;
	}
	
	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final Bag bag = (Bag)entity;
		final Location currentLocation = entity.getLocation();
		ArrayList<Location> links = currentLocation.getLinkedTo();
		if (layout.inWaitingUnit(currentLocation)) {
			return dryerStrategy.onRequestingLocation(bag);
		}
		else if (layout.inInitBasket(currentLocation)) {
			if (layout.isSimple()) {
				return layout.getWaitingUnits()[0];
			}
			return warehouseStrategy.onRequestingLocation(bag);
		}
		else if (layout.inDryer(currentLocation)) {
			dryerStrategy.onLeavingLocation(bag);
			return links.get(0);
		}
		else if (layout.inWarehouseFinalStage(currentLocation)) {
			return washinglineStrategy.onRequestingLocation(bag);
		}
		else if (links.size() == 1) {
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

	/**
	 * @return the dryerStrategy
	 */
	public DistributionStrategy getDryerStrategy() {
		return dryerStrategy;
	}

	/**
	 * @param dryerStrategy the dryerStrategy to set
	 */
	public void setDryerStrategy(DistributionStrategy dryerStrategy) {
		this.dryerStrategy = dryerStrategy;
	}

	public interface DistributionStrategy {

		Location onRequestingLocation(Bag bag);
		void onLeavingLocation(Bag bag);
	}
	
	private class DummyDistributionStrategy implements DistributionStrategy {
		final private Node[][] destinations;
		final private int n;
		public DummyDistributionStrategy(Node[][] destinations) {
			this.destinations = destinations;
			n = destinations.length;
		}

		@Override
		public Location onRequestingLocation(Bag bag) {
			return destinations[bag.getIdentifier() % n][0];
		}

		@Override
		public void onLeavingLocation(Bag bag) {
		}		
	}
	
	private class ProductTypeWarehouseDistributionStrategy implements DistributionStrategy {
		public ProductTypeWarehouseDistributionStrategy() {
		}

		@Override
		public Location onRequestingLocation(Bag bag) {
			return layout.getWarehouseStage(bag.getProductType().ordinal())[0];
		}

		@Override
		public void onLeavingLocation(Bag bag) {
		}
	}
	
	private class DummyDryerDistributionStrategy implements DistributionStrategy {
		private final long[] lastTs;
		private final ArrayList<Bag> waiting;
		
		public DummyDryerDistributionStrategy() {
			lastTs = new long[layout.getDryers().length];
			Arrays.fill(lastTs, -1);
			waiting = new ArrayList<>(totalWaitingCapacity);
		}

		@Override
		public Location onRequestingLocation(Bag bag) {
			final int index = findSuitableDryer(bag.getTs());
			if (index == -1) {
				waiting.add(bag);
				return Router.COND_WAIT_LOCATION;
			}
			else {
				lastTs[index] = bag.getTs();
				return layout.getDryers()[index];
			}
		}
		
		@Override
		public void onLeavingLocation(Bag bag) {
			if (waiting.size() > 0) {
				final int index = findSuitableDryer(waiting.get(0).getTs());
				if (index != -1) {
					lastTs[index] = bag.getTs();
					final Bag pending = waiting.remove(0);
					pending.notifyLocationAvailable(layout.getDryers()[index]);
				}
			}			
		}
		
		/**
		 * Returns the index of a dryer suitable for drying the bag 
		 * @param ei Element instance belonging to the bag
		 * @return the index of a dryer suitable for drying the bag; -1 in case no dryer is available
		 */
		private int findSuitableDryer(long ts) {
			// First identifies potential dryer
			for (int index = 0; (index < layout.getDryers().length); index++) {
				// The dryer has available space
				if (layout.getDryers()[index].getAvailableCapacity() > 0) {
					// Just assigned a bag to the dryer 
					if (lastTs[index] == ts) {
						return index;
					}
					// ... or the dryer is empty
					if (layout.getDryers()[index].getCapacity() == layout.getDryers()[index].getAvailableCapacity()) {
						return index;
					}
				}
			}
			return -1;
		}
	}
	
	private class PreserveTypeDryerDistributionStrategy implements DistributionStrategy {
		private final long[] lastTs;
		private final EnumMap<ProductsType, ArrayList<Bag>> available;		
		private final ArrayList<Bag> notAssignedQueue;
		private final ArrayList<Node> emptyDryers;
		
		public PreserveTypeDryerDistributionStrategy() {
			lastTs = new long[layout.getDryers().length];
			Arrays.fill(lastTs, -1);
			available = new EnumMap<>(ProductsType.class);
			notAssignedQueue = new ArrayList<>(totalWaitingCapacity);
			for (ProductsType type : ProductsType.values()) {
				available.put(type, new ArrayList<>(totalWaitingCapacity));
			}
			emptyDryers = new ArrayList<>();
			for (Node dryer : layout.getDryers()) {
				emptyDryers.add(dryer);
			}
		}

		@Override
		public Location onRequestingLocation(Bag bag) {
			// Gets the list for the product type of the new bag
			final ArrayList<Bag> list = available.get(bag.getProductType());
			// If there are empty dryers
			if (!emptyDryers.isEmpty()) {
				// If there are empty dryers and the waiting space is full
				if (notAssignedQueue.size() + 1 == totalWaitingCapacity) {
					final Node dryer = emptyDryers.remove(0);
					// Try first to put a full load
					if (list.size() + 1 == dryer.getCapacity()) {
						putOnDryer(dryer, list);
						return dryer;
					}
					if (!notAssignedQueue.isEmpty()) {
						// Otherwise, try with the first bag waiting
						final Bag waitingBag = notAssignedQueue.get(0);
						putOnDryer(dryer, available.get(waitingBag.getProductType()));
					}
				}
				// If the waiting space is not full, only put clothes if there are enough bags as to completely fill the dryer 
				else if (list.size() + 1 == emptyDryers.get(0).getCapacity()) {
					final Node dryer = emptyDryers.remove(0);
					putOnDryer(dryer, list);
					return dryer;
				}
			}
			list.add(bag);
			notAssignedQueue.add(bag);
			return Router.COND_WAIT_LOCATION;			
		}
		
		@Override
		public void onLeavingLocation(Bag bag) {
			final Node dryer = (Node)bag.getLocation(); 			
			// Only if the dryer is empty
			if (dryer.getAvailableCapacity() + 1 == dryer.getCapacity()) {
				emptyDryers.add(dryer);
			}
			// Only if this dryer is the only available
			if (emptyDryers.size() == 1) {
				// Find a group of clothes which completely fills the dryer
				for (final ArrayList<Bag> list : available.values()) {
					if (list.size() >= dryer.getCapacity()) {
						putOnDryer(dryer, list);
						emptyDryers.remove(dryer);
					}
				}
				// If not yet used
				if ((emptyDryers.size() == 1) && (notAssignedQueue.size() == totalWaitingCapacity)) {
					final Bag waitingBag = notAssignedQueue.get(0);
					putOnDryer(dryer, available.get(waitingBag.getProductType()));											
					emptyDryers.remove(dryer);
				}
			}
		}
		
		private void putOnDryer(Node dryer, ArrayList<Bag> list) {
			while (!list.isEmpty() && (dryer.getAvailableCapacity() > 0)) {
				final Bag bag = list.remove(0);
				bag.notifyLocationAvailable(dryer);
				notAssignedQueue.remove(bag);
			}				
		}
	}
	
}