/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * Meets the Static Partial Join for Multiple Instances pattern (WFP34) if 
 * <tt>acceptValue &gt 1</tt> and <tt>acceptValue &lt nInstances</tt>.
 * If <tt>nInstances = acceptValue</tt> is equivalent to the 
 * {@link SynchronizedMultipleInstanceFlow}.
 * @author Iván Castilla Rodríguez
 */
public interface StaticPartialJoinMultipleInstancesFlow<WT extends WorkThread<?>> extends PredefinedStructuredFlow<WT> {
	/**
	 * Returns the number of thread instances created in this flow.
	 * @return The number of thread instances created in this flow
	 */
	int getNInstances();

	/**
	 * Returns the number of threads which must finish to pass the control.
	 * @return The number of threads which must finish to pass the control
	 */
	int getAcceptValue();
}
