/**
 * 
 */
package es.ull.iis.simulation.laundry;

import java.util.ArrayList;
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
		this.dryerStrategy = new BetterMaxOfTheSameDryerDistributionStrategy();
		this.dryers = dryers;
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
	
	public class DirectDryerDistributionStrategy implements DryerDistributionStrategy {
		private int counter;
		// TODO: incluir estrategia para no enviar otra bolsa a una secadora ya ocupada (siempre que no acabe de ocuparse en el mismo instante de tiempo)
		// TODO: Incluir estrategia para reactivar bolsas pendientes una vez la secadora se vacía
		public DirectDryerDistributionStrategy() {
		}

		@Override
		public void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			((Bag)ei.getElement()).setDryer(dryers[counter]);
			counter = (counter + 1) % dryers.length;
			waitingArea.signal(ei);
		}
		
		@Override
		public void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class AlwaysWaitToFillDryerDistributionStrategy implements DryerDistributionStrategy {
		private int counter;
		private final ArrayList<ElementInstance> assigned;
		
		public AlwaysWaitToFillDryerDistributionStrategy() {
			this.assigned = new ArrayList<>();
		}

		@Override
		public void onRequestingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			((Bag)ei.getElement()).setDryer(dryers[counter]);
			assigned.add(ei);
			if (dryers[counter].getCapacity() == assigned.size()) {
				counter = (counter + 1) % dryers.length;
				while(!assigned.isEmpty()) {
					waitingArea.signal(assigned.remove(0));
				}
			}			
		}

		@Override
		public void onLeavingDryer(WaitForSignalFlow waitingArea, ElementInstance ei) {
			// TODO Auto-generated method stub
			
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
