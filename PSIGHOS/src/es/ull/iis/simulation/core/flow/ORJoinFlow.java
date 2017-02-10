/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link MergeFlow} flow which allows all the true incoming branches to pass.
 * @author Iván Castilla Rodríguez
 */
public interface ORJoinFlow<WT extends WorkThread<?>> extends MergeFlow<WT> {
}
