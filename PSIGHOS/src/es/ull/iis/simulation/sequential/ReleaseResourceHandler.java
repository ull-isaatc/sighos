/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * @author Iv�n Castilla
 *
 */
public interface ReleaseResourceHandler extends ResourceHandler {
	boolean releaseResources(WorkThread wThread);
}
