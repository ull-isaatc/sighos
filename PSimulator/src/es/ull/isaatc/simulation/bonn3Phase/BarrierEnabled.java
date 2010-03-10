/**
 * 
 */
package es.ull.isaatc.simulation.bonn3Phase;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines an object which is capable of using a barrier
 * @author Iv�n Castilla
 *
 */
public interface BarrierEnabled {
	void await();
	AtomicBoolean getTourFlag(int ind);
}
