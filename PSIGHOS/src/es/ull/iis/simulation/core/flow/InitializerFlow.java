/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} which begins an execution branch. Only initializer flows can be used as the first
 * step in a flow structure. 
 * @author Iván Castilla Rodríguez
 */
public interface InitializerFlow<WT extends WorkThread<?>> extends Flow<WT> {
}
