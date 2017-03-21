/**
 * 
 */
package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * A conflict zone serves as a MUTEX region for elements which have booked the same
 * resource. This zone stores a list of elements and builds a stack of semaphores.
 * Another semaphore controls the access to this zone. The MUTEX region is controlled
 * by means of the stack of semaphores.
 * Conflict zones can be merged. One of the conflict zones absorbs the other. Once this happens,
 * the "absorbed" CZ is nullified and "substituted" by the "absorbing" CZ. 
 * TODO Comment
 * @author Iván Castilla Rodríguez
 */
final class ConflictZone implements Comparable<ConflictZone> {
	/** List of element work items which have a conflict. */
	private final TreeSet<FlowExecutor> list = new TreeSet<FlowExecutor>();
	/** Stack of semaphores which control a MUTEX region. */
	private final ArrayList<Semaphore> semStack = new ArrayList<Semaphore>();
	/** A semaphore for accesing this zone. */
	private final Semaphore semBook = new Semaphore(1);
	/** If this CZ is absorbed by another one, the "absorbing" CZ */
	private ConflictZone substitute = null;
	
	/**
	 * Creates a new conflict zone containing only one object.
	 * @param fe The current flow executor using this conflict zone
	 */
	protected ConflictZone(FlowExecutor fe) {
		list.add(fe);
	}
	
	private void acquire(ElementEngine elem) {
		try {
			semBook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	private void release(ElementEngine elem) {
		semBook.release();
	}

	protected void safeMerge(ElementEngine elem, ConflictZone mergingCZ) {
		// I acquire this CZ
		acquire(elem);
		// While we were waiting for the merged CZ, it was substituted by other one 
		if (substitute != null) {
			// I free this CZ...
			release(elem);
			// ... and restart the process with the "substitute"
			substitute.safeMerge(elem, mergingCZ);
		}
		// At this point I have acquired the first ("merged") CZ
		else {
			// I recheck if both CZs are different because they could have been merged simultaneously
			if (this == mergingCZ) {
				// If they are equal, I simply free the acquired CZ
				release(elem);
			}
			else {
				safeMerge2(elem, mergingCZ);
			}
		}
	}

	protected void safeMerge2(ElementEngine elem, ConflictZone mergingCZ) {
		// I continue the merge acquiring the merging CZ
		mergingCZ.acquire(elem);
		// If the merging CZ is valid, I can carry out the merge
		if (mergingCZ.substitute == null) {
			merge(mergingCZ);
			mergingCZ.release(elem);
			release(elem);
		}
		// In case the "merging" CZ was merged into another CZ simultaneously
		else {
			// I free the old merging CZ
			mergingCZ.release(elem);
			mergingCZ = mergingCZ.substitute;
			// It could happen that the merging CZ was substituted by this CZ
			if (this == mergingCZ) {
				release(elem);
			}
			// Maybe the new merging CZ has lower id than the old merged CZ. Thus, they should be interchanged
			else if (compareTo(mergingCZ) > 0) {
				// I free this CZ...
				release(elem);
				// ... and restart the process with the "substitute"
				mergingCZ.safeMerge(elem, this);
			}
			// If not, I simply re-acquire the new merging CZ...
			else {
				//... and carry out the merge
				safeMerge2(elem, mergingCZ);
			}					
		}
	}

	/**
	 * Merges this conflict list with another one. Adds the items of the other list
	 * to this conflict list, and then assigns this conflict list to the items in the
	 * other list.
	 * @param other Another conflict zone
	 */
	protected void merge(ConflictZone other) {
		list.addAll(other.list);
		semStack.addAll(other.semStack);
		// Updates the conflict lists of any implied element
		for (FlowExecutor fe : other.list)
			fe.setConflictZone(this);
		other.substitute = this;
	}
	
	/**
	 * Returns the number of conflicting items handled by this conflict zone.
	 * @return the number of conflicting items handled by this conflict zone
	 */
	protected int size() {
		return list.size();
	}
	
	/**
	 * Removes an item from this conflict zone, thus indicating that this item is
	 * no longer in conflict. 
	 * @param fe Work item to be removed
	 * @return True if the item existed in the list; false in other case.
	 */
	protected boolean remove(FlowExecutor fe) {
		final ElementEngine elem = fe.getElement();
		boolean result = false; 
		elem.getModelElem().debug("Removing\t" + fe + "(" + this + ")");
		acquire(elem);
		result = list.remove(fe);
		release(elem);
		return result;
	}
	
	/**
	 * Creates and returns a stack of semaphores with the same contents than the stack of 
	 * semaphores of this conflict zone. The copy is needed in case the original stack becomes
	 * modified while it is being used outside this method.<p>A semaphore is created if this list 
	 * is empty.
	 * @return The stack of semaphores.
	 */
	protected ArrayList<Semaphore> getSemaphores(FlowExecutor fe) {
		final ElementEngine elem = fe.getElement();
		acquire(elem);
		// Once acquired we need to check the validity of this CZ
		if (substitute != null) {
			release(elem);
			return substitute.getSemaphores(fe);
		}			
		if (semStack.isEmpty())
			semStack.add(new Semaphore(1));
		final ArrayList<Semaphore> stack = new ArrayList<Semaphore>();
		stack.addAll(semStack);
		release(elem);
		return stack;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ConflictZone arg0) {
		return list.first().compareTo(arg0.list.first());
	}
	
}
