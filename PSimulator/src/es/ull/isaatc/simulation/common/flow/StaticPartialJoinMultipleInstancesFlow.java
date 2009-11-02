/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;


/**
 * Meets the Static Partial Join for Multiple Instances pattern (WFP34) if 
 * <code>acceptValue > 1</code> and <code>acceptValue < nInstances</code>.
 * If <code>nInstances = acceptValue</code> is equivalent to the <code>
 * SynchronizedMultipleInstanceFlow</code>.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public interface StaticPartialJoinMultipleInstancesFlow extends PredefinedStructuredFlow {
	/**	The number of thread instances this flow creates */
	int getNInstances();
	/** The number of threads which must finish to pass the control */
	int getAcceptValue();
}
