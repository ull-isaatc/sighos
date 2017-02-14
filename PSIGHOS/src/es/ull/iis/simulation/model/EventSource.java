/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public interface EventSource {
	DiscreteEvent onCreate(long ts);
	DiscreteEvent onDestroy();
}
