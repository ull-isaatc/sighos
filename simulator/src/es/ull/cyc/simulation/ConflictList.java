/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.sync.Semaphore;
import es.ull.cyc.util.OrderedList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ConflictList {
	protected OrderedList<Element> list;
	private Semaphore semBook;
	private Semaphore semSafe;
	
	public ConflictList(Element e) {
		semBook = new Semaphore(1);
		semSafe = new Semaphore(1);
		list = new OrderedList<Element>();
		list.add(e);
	}
	
	/**
	 * Merges this conflict list with another one. Adds the elements of the other list
	 * to this conflict list, and then assigns this conflict list to the elements in the
	 * other list.
	 * @param other
	 */
	public void merge(ConflictList other) {
		// Exclusive access to this conflict list...
		waitSemaphore(false);
		Semaphore otherSem = other.semBook;
		// ... and to the other one.
		otherSem.waitSemaphore();
		list.addAll(other.list);
		for (int i = 0; i < other.size(); i++)
			other.get(i).setConflictList(this);
		otherSem.signalSemaphore();
		signalSemaphore(false);
	}
	
	public int size() {
		return list.size();
	}
	
	public Element get(int index) {
		return list.get(index);
	}
	
	public boolean remove(Element e) {
		waitSemaphore(false);		
		boolean result = list.remove(e);
		signalSemaphore(false);
		return result;
	}
	
	/** 
	 * Resumes the semaphore. The value of "safe" indicates the semaphore 
	 * which is affected.
	 * @param safe If True, it affects the "safe semaphore"; if false, it affects 
	 * the "book semaphore" 
	 */
	public void signalSemaphore(boolean safe) {
		if (safe)
			semSafe.signalSemaphore();
		else
			semBook.signalSemaphore();
	}

	/** 
	 * Stos the semaphore. The value of "safe" indicates the semaphore 
	 * which is affected.
	 * @param safe If True, it affects the "safe semaphore"; if false, it affects 
	 * the "book semaphore" 
	 */
	public void waitSemaphore(boolean safe) {
		if (safe)
			semSafe.waitSemaphore();
		else
			semBook.waitSemaphore();
	}
}
