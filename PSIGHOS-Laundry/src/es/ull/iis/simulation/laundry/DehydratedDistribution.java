/**
 * 
 */
package es.ull.iis.simulation.laundry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.WaitForSignalFlow;
import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla
 *
 */
public class DehydratedDistribution implements WaitForSignalFlow.Listener {
	private DryerDistributionStrategy dryerStrategy;
	final private Node[] dryers;
	final int totalWaitingCapacity;

	/**
	 * 
	 */
	public DehydratedDistribution(Node[] dryers, Node[] waitingArea) {
		this.dryers = dryers;
		this.dryerStrategy = new PreserveTypeDryerDistributionStrategy();
		int total = 0;
		for (Node w : waitingArea) {
			total += w.getCapacity();
		}
		this.totalWaitingCapacity = total;
	}

	/**
	 * @return the dryerStrategy
	 */
	public DryerDistributionStrategy getDryerStrategy() {
		return dryerStrategy;
	}

	/**
	 * @param dryerStrategy the dryerStrategy to set
	 */
	public void setDryerStrategy(DryerDistributionStrategy dryerStrategy) {
		this.dryerStrategy = dryerStrategy;
	}

	@Override
	public void register(WaitForSignalFlow flow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyArrival(WaitForSignalFlow flow, ElementInstance ei) {
		dryerStrategy.onRequestingDryer(flow, ei);
	}
	
	public void notifyLeaving(WaitForSignalFlow waitingArea, ElementInstance ei) {
		dryerStrategy.onLeavingDryer(waitingArea, ei);
	}
	
	public interface DryerDistributionStrategy {

		void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei);
		void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei);
	}
	
	public class DummyDryerDistributionStrategy implements DryerDistributionStrategy {
		private final long[] lastTs;
		private final ArrayList<ElementInstance> waiting;
		
		public DummyDryerDistributionStrategy() {
			lastTs = new long[dryers.length];
			Arrays.fill(lastTs, -1);
			waiting = new ArrayList<>(totalWaitingCapacity);
		}

		@Override
		public void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			final int index = findSuitableDryer(ei.getElement().getTs());
			if (index == -1) {
				waiting.add(ei);
			}
			else {
				lastTs[index] = ei.getElement().getTs();
				((Bag)ei.getElement()).setDryer(dryers[index]);
				waitingArea.signal(ei);						
			}
		}
		
		@Override
		public void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			if (waiting.size() > 0) {
				final int index = findSuitableDryer(waiting.get(0).getElement().getTs());
				if (index != -1) {
					lastTs[index] = ei.getElement().getTs();
					final ElementInstance pending = waiting.remove(0); 
					((Bag)pending.getElement()).setDryer(dryers[index]);
					waitingArea.signal(pending);						
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
			for (int index = 0; (index < dryers.length); index++) {
				// The dryer has available space
				if (dryers[index].getAvailableCapacity() > 0) {
					// Just assigned a bag to the dryer 
					if (lastTs[index] == ts) {
						return index;
					}
					// ... or the dryer is empty
					if (dryers[index].getCapacity() == dryers[index].getAvailableCapacity()) {
						return index;
					}
				}
			}
			return -1;
		}
	}
	
	public class PreserveTypeDryerDistributionStrategy implements DryerDistributionStrategy {
		private final long[] lastTs;
		private final EnumMap<ProductsType, ArrayList<ElementInstance>> available;		
		private final ArrayList<ElementInstance> notAssignedQueue;
		private final ArrayList<Node> emptyDryers;
		
		public PreserveTypeDryerDistributionStrategy() {
			lastTs = new long[dryers.length];
			Arrays.fill(lastTs, -1);
			available = new EnumMap<>(ProductsType.class);
			notAssignedQueue = new ArrayList<>(totalWaitingCapacity);
			for (ProductsType type : ProductsType.values()) {
				available.put(type, new ArrayList<>(totalWaitingCapacity));
			}
			emptyDryers = new ArrayList<>();
			for (Node dryer : dryers) {
				emptyDryers.add(dryer);
			}
		}

		@Override
		public void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			final Bag bag = (Bag)ei.getElement();
			final ArrayList<ElementInstance> list = available.get(bag.getProductType());
			list.add(ei);
			notAssignedQueue.add(ei);

			// If there is at least one empty dryer
			if (emptyDryers.size() > 0) {
				// If the waiting space is full
				if (notAssignedQueue.size() == totalWaitingCapacity) {
					final Node dryer = emptyDryers.remove(0);
					// Try first to put a full load
					if (list.size() == dryer.getCapacity()) {
						putOnDryer(dryer, waitingArea, list);
					}
					// Otherwise, try with the first bag waiting
					else {
						final ElementInstance bagInstance = notAssignedQueue.get(0);
						final Bag waitingBag = (Bag)bagInstance.getElement();
						putOnDryer(dryer, waitingArea, available.get(waitingBag.getProductType()));						
					}
				}
				// If the waiting space is not full, only put clothes if there are enough bags as to completely fill the dryer 
				else if (list.size() == emptyDryers.get(0).getCapacity()) {
					final Node dryer = emptyDryers.remove(0);
					putOnDryer(dryer, waitingArea, list);
				}				
			}
		}
		
		@Override
		public void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			final Bag bag = (Bag)ei.getElement();
			final Node dryer = bag.getDryer(); 			
			// Only if the dryer is empty
			if (dryer.getAvailableCapacity() == dryer.getCapacity()) {
				emptyDryers.add(dryer);
			}
			// Only if this dryer is the only available
			if (emptyDryers.size() == 1) {
				// Find a group of clothes which completely fills the dryer
				for (final ArrayList<ElementInstance> list : available.values()) {
					if (list.size() >= dryer.getCapacity()) {
						putOnDryer(dryer, waitingArea, list);
						emptyDryers.remove(dryer);
					}
				}
				// If not yet used
				if ((emptyDryers.size() == 1) && (notAssignedQueue.size() == totalWaitingCapacity)) {
					final ElementInstance bagInstance = notAssignedQueue.get(0);
					final Bag waitingBag = (Bag)bagInstance.getElement();
					putOnDryer(dryer, waitingArea, available.get(waitingBag.getProductType()));											
					emptyDryers.remove(dryer);
				}
			}
		}
		
		private void putOnDryer(Node dryer, WaitForSignalFlow waitingArea, ArrayList<ElementInstance> list) {
			while (!list.isEmpty() && (dryer.getAvailableCapacity() > 0)) {
				final ElementInstance bagInstance = list.remove(0);
				((Bag)bagInstance.getElement()).setDryer(dryer);
				waitingArea.signal(bagInstance);
				notAssignedQueue.remove(bagInstance);
			}				
		}
		
		/**
		 * Returns the index of a dryer suitable for drying the bag 
		 * @param ei Element instance belonging to the bag
		 * @return the index of a dryer suitable for drying the bag; -1 in case no dryer is available
		 */
		private int findSuitableDryer(long ts, ProductsType type) {
			// First identifies potential dryer
			for (int index = 0; (index < dryers.length); index++) {
				// The dryer has available space
				if (dryers[index].getAvailableCapacity() > 0) {
					// Just assigned a bag to the dryer
					
					if ((lastTs[index] == ts) && type.equals(((Bag)dryers[index].getEntitiesIn().get(0)).getProductType())) {
						return index;
					}
					// ... or the dryer is empty
					if (dryers[index].getCapacity() == dryers[index].getAvailableCapacity()) {
						return index;
					}
				}
			}
			return -1;
		}
	}
	
	public class BetterMaxOfTheSameDryerDistributionStrategy implements DryerDistributionStrategy {
		private int counter;
		private EnumMap<ProductsType, ArrayList<ElementInstance>> available;
		private ArrayList<ElementInstance> notAssignedQueue;
		
		public BetterMaxOfTheSameDryerDistributionStrategy() {
			available = new EnumMap<>(ProductsType.class);
			notAssignedQueue = new ArrayList<>(totalWaitingCapacity);
			for (ProductsType type : ProductsType.values()) {
				available.put(type, new ArrayList<>(totalWaitingCapacity));
			}
		}
		
		@Override
		public void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			final Bag bag = ((Bag)ei.getElement()); 
			final ArrayList<ElementInstance> list = available.get(bag.getProductType());
			list.add(ei);
			notAssignedQueue.add(ei);
			if (list.size() == dryers[counter].getCapacity()) {
				while (!list.isEmpty()) {
					final ElementInstance bagInstance = list.remove(0);
					((Bag)bagInstance.getElement()).setDryer(dryers[counter]);
					waitingArea.signal(bagInstance);
					notAssignedQueue.remove(bagInstance);
				}				
				counter = (counter + 1) % dryers.length;
			}
			else if (notAssignedQueue.size() == totalWaitingCapacity) {
				// Removes the first bag
				final ElementInstance bagInstance = notAssignedQueue.remove(0);
				// Removes the bag from the specific product type's list 
				available.get(((Bag)bagInstance.getElement()).getProductType()).remove(bagInstance);
				((Bag)bagInstance.getElement()).setDryer(dryers[counter]);			
				waitingArea.signal(bagInstance);
				counter = (counter + 1) % dryers.length;
			}
		}

		@Override
		public void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			// TODO Auto-generated method stub
			
		}
	}
}
