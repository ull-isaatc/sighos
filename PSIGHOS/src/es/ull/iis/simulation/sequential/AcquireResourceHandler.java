/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * @author Iv�n Castilla
 *
 */
public interface AcquireResourceHandler extends ResourceHandler {
	boolean acquireResources(WorkThread wThread);
}
