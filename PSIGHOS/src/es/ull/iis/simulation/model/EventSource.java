/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public interface EventSource extends Debuggable {
	
	DiscreteEvent onCreate(long ts);
	DiscreteEvent onDestroy(long ts);

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    void notifyEnd();
    
}
