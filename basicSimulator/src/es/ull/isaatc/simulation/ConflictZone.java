/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

/**
 * A conflict zone serves as a MUTEX region for elements which have booked the same
 * resource. This zone stores a list of elements and builds a stack of semaphores.
 * Another semaphore controls the access to this zone. The MUTEX region is controled
 * by means of the stack of semaphores.
 * @author Iván Castilla Rodríguez
 */
public class ConflictZone {
	/** List of  elements which have a conflict. */
	protected TreeSet<SingleFlow> list;
	/** Stack of semaphores which control a MUTEX region. */
	protected ArrayList<Semaphore> semStack;
	/** A semaphore for accesing this zone. */
	private Semaphore semBook;
	
	public ConflictZone(SingleFlow sf) {
		semBook = new Semaphore(1);
		list = new TreeSet<SingleFlow>();
		semStack = new ArrayList<Semaphore>();
		list.add(sf);
	}
	
	/**
	 * Merges this conflict list with another one. Adds the elements of the other list
	 * to this conflict list, and then assigns this conflict list to the elements in the
	 * other list.
	 * @param other
	 */
	public void merge(ConflictZone other) {
		Semaphore otherSem = other.semBook;
		try {
			// Exclusive access to this conflict list...
			semBook.acquire();
			// ... and to the other one.
			otherSem.acquire();
			list.addAll(other.list);
			semStack.addAll(other.semStack);
			// Updates the conflict lists of any implied element
			for (SingleFlow sf : other.list)
				sf.setConflictZone(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			otherSem.release();
			semBook.release();
		}
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean remove(SingleFlow sf) {
		boolean result = false; 
		try {
			semBook.acquire();
			result = list.remove(sf);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			semBook.release();
		}
		return result;
	}
	
	/**
	 * Creates and returns a stack of semaphores with the same contents than the stack of 
	 * semaphores of this conflict zone. The copy is necesary in case the original stack becomes
	 * modified while it is being used outside this method.<p>A semaphore is created if this list 
	 * is empty.
	 * @return The stack of semaphores.
	 */
	public ArrayList<Semaphore> getSemaphores() {
		ArrayList<Semaphore> stack = null;
		try {
			semBook.acquire();
			if (semStack.isEmpty())
				semStack.add(new Semaphore(1));
			stack = new ArrayList<Semaphore>();
			stack.addAll(semStack);
			for (Semaphore sem : stack)
				sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			semBook.release();
		}
		return stack;
	}
}
