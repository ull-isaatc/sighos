/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.sync.Semaphore;
import es.ull.isaatc.util.OrderedList;

/**
 * A conflict zone serves as a MUTEX region for elements which have booked the same
 * resource. This zone stores a list of elements and builds a stack of semaphores.
 * Another semaphore controls the access to this zone. The MUTEX region is controled
 * by means of the stack of semaphores.
 * @author Iván Castilla Rodríguez
 */
public class ConflictZone {
	/** List of  elements which have a conflict. */
	protected OrderedList<Element> list;
	/** Stack of semaphores which control a MUTEX region. */
	protected ArrayList<Semaphore> semStack;
	/** A semaphore for accesing this zone. */
	private Semaphore semBook;
	
	public ConflictZone(Element e) {
		semBook = new Semaphore(1);
		list = new OrderedList<Element>();
		semStack = new ArrayList<Semaphore>();
		list.add(e);
	}
	
	/**
	 * Merges this conflict list with another one. Adds the elements of the other list
	 * to this conflict list, and then assigns this conflict list to the elements in the
	 * other list.
	 * @param other
	 */
	public void merge(ConflictZone other) {
		// Exclusive access to this conflict list...
		semBook.waitSemaphore();
		Semaphore otherSem = other.semBook;
		// ... and to the other one.
		otherSem.waitSemaphore();
		list.addAll(other.list);
		semStack.addAll(other.semStack);
		// Updates the conflict lists of any implied element
		for (int i = 0; i < other.size(); i++)
			other.list.get(i).setConflictZone(this);
		otherSem.signalSemaphore();
		semBook.signalSemaphore();
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean remove(Element e) {
		semBook.waitSemaphore();		
		boolean result = list.remove(e);
		semBook.signalSemaphore();
		return result;
	}
	
	/**
	 * Creates and returns a stack of semaphores with the same contents than the stack of 
	 * semaphores of this conflict zone. A semaphore is created if this list is empty.
	 * @return The stack of semaphores.
	 */
	public ArrayList<Semaphore> getSemaphores() {		
		semBook.waitSemaphore();
		if (semStack.isEmpty())
			semStack.add(new Semaphore(1));
		ArrayList<Semaphore> stack = new ArrayList<Semaphore>();
		stack.addAll(semStack);
		semBook.signalSemaphore();
		return stack;
	}
}
