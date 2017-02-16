/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * @author Iván Castilla
 *
 */
public interface AcquireResourceHandler extends ResourceHandler {
	boolean acquireResources(WorkThread wThread);
}
