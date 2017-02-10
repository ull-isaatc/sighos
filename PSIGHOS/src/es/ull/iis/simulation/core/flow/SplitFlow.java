/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * An {@link InitializerFlow} which can create several outgoing branches.
 * @author Iván Castilla Rodríguez
 */
public interface SplitFlow<WT extends WorkThread<?>> extends InitializerFlow<WT> {
}
