/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * A conflict zone serves as a MUTEX region for elements which have booked the same
 * resource when asking for different activities. This zone stores a list of elements 
 * and builds a semaphore stack.
 * Another semaphore controls the access to this zone. The MUTEX region is controlled
 * by means of the semaphore stack.
 * Conflict zones can be merged. One of the conflict zones absorbs the other. Once this happens,
 * the "absorbed" CZ is nullified and "substituted" by the "absorbing" CZ. 
 * @author Iv�n Castilla Rodr�guez
 */
public class ConflictZone implements Comparable<ConflictZone> {
	/** List of  elements which have a conflict. */
	protected TreeSet<SingleFlow> list;
	/** Stack of semaphores which control a MUTEX region. */
	protected ArrayList<Semaphore> semStack;
	/** A semaphore for accesing this zone. */
	private Semaphore semBook;
	/** If this CZ is absorbed by another one, the "absorbing" CZ */
	protected ConflictZone substitute = null;
	/** The single flow which created this CZ */
	private final SingleFlow owner;
	
	/**
	 * Creates a new conflict zone whose owner is sf
	 * @param sf This conflict zone's owner
	 */
	public ConflictZone(SingleFlow sf) {
		semBook = new Semaphore(1);
		list = new TreeSet<SingleFlow>();
		semStack = new ArrayList<Semaphore>();
		list.add(sf);
		this.owner = sf;
	}
	
	/**
	 * Acquires access to this CZ
	 * @param elem Element requesting access to this CZ
	 */
	private void acquire(Element elem) {
		elem.debug("MUTEX\trequesting\tCZ " + owner + " (" + this + ")");
		try {
			semBook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		elem.debug("MUTEX\tacquired\tCZ " + owner + " (" + this + ")");
	}
	
	/**
	 * Releases this CZ
	 * @param elem Element finishing access to this CZ
	 */
	private void release(Element elem) {
		elem.debug("MUTEX\treleasing\tCZ " + owner + " (" + this + ")");    	
		semBook.release();
		elem.debug("MUTEX\tfreed\tCZ " + owner + " (" + this + ")");
	}

	/**
	 * A complicated double recursive method to merge safely two conflict zones.
	 * To do this, both CZ have to be exclusively accessed. The problem is that
	 * this double acquisition is not an atomic operation, so several tests must 
	 * be performed.
	 * The major problem is caused by "crossing" mergings, when one of the CZ is
	 * merged with a third one during this merging. This problem is solved by
	 * checking repeatedly both CZ after each step.  
	 * @param elem Element requesting the merging
	 * @param mergingCZ The other CZ to be merged
	 */
	protected void safeMerge(Element elem, ConflictZone mergingCZ) {
		// I acquire this CZ
		acquire(elem);
		// While we were waiting for the merged CZ, it was substituted by other one 
		if (substitute != null) {
			elem.debug("MUTEX\t" + "Merged CZ subs by " + substitute);
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

	/**
	 * The second part of the safeMerge method. It's been defined apart for simplification
	 * purposes. Performs the second part of the merging.
	 * @param elem Element requesting the merging
	 * @param mergingCZ The other CZ to be merged
	 * @see safeMerge
	 */
	protected void safeMerge2(Element elem, ConflictZone mergingCZ) {
		// I continue the merge acquiring the merging CZ
		mergingCZ.acquire(elem);
		// If the merging CZ is valid, I can carry out the merge
		if (mergingCZ.substitute == null) {
			merge(mergingCZ);
			// And I release both CZs
			mergingCZ.release(elem);
			release(elem);
		}
		// In case the "merging" CZ was merged into another CZ simultaneously
		else {
			elem.debug("MUTEX\t" + "Merging CZ subs by " + mergingCZ.substitute);
			// I free the old merging CZ
			mergingCZ.release(elem);
			mergingCZ = mergingCZ.substitute;
			// It could happen that the merging CZ was substituted by this CZ
			if (this == mergingCZ) {
				release(elem);
			}
			// Maybe the new merging CZ has lower id than the old merged CZ. Thus, they should be interchanged
			else if (compareTo(mergingCZ) > 0) {
				elem.debug("MUTEX\t" + "Order wrong. Has to interchange");
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
	 * Merges this conflict list with another one. Adds the elements of the other list
	 * to this conflict list, and then assigns this conflict list to the elements in the
	 * other list.
	 * @param other
	 */
	public void merge(ConflictZone other) {
		list.addAll(other.list);
		semStack.addAll(other.semStack);
		// Updates the conflict lists of any implied element
		for (SingleFlow sf : other.list)
			sf.setConflictZone(this);
		other.substitute = this;
	}
	
	/**
	 * Returns true if this CZ contains more than one element. False in other case
	 * @return true if this CZ contains more than one element. False in other case
	 */
	public boolean isConflict() {
		return (list.size() > 1);
	}

	/**
	 * Removes an element from this CZ, thus avoiding unnecessary checks
	 * @param sf Single flow to be removed
	 * @return True if the single flow could be removed
	 */
	public boolean remove(SingleFlow sf) {
		boolean result = false;
		sf.getElement().debug("Removing\t" + sf + "(" + this + ")");
		acquire(sf.getElement());
		result = list.remove(sf);
		release(sf.getElement());
		return result;
	}
	
	/**
	 * Creates and returns a stack of semaphores with the same contents than the stack of 
	 * semaphores of this conflict zone. The copy is necesary in case the original stack becomes
	 * modified while it is being used outside this method.<p>A semaphore is created if this list 
	 * is empty.
	 * @return The stack of semaphores.
	 */
	public ArrayList<Semaphore> getSemaphores(SingleFlow sf) {
		acquire(sf.getElement());
		// Once acquired we need to check the validity of this CZ
		if (substitute != null) {
			release(sf.getElement());
			return substitute.getSemaphores(sf);
		}			
		if (semStack.isEmpty())
			semStack.add(new Semaphore(1));
		ArrayList<Semaphore> stack = new ArrayList<Semaphore>();
		stack.addAll(semStack);
		release(sf.getElement());
		return stack;
	}

	/**
	 * @return the substitute
	 */
	public ConflictZone getSubstitute() {
		return substitute;
	}

	/**
	 * @param substitute the substitute to set
	 */
	public void setSubstitute(ConflictZone substitute) {
		this.substitute = substitute;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ConflictZone arg0) {
		return list.first().compareTo(arg0.list.first());
	}
	
}