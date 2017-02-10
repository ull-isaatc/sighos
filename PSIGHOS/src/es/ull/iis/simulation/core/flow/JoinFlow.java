/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link FinalizerFlow} which merges several incoming branches into a single one.
 * @author Iv�n Castilla Rodr�guez
 */
public interface JoinFlow<WT extends WorkThread<?>> extends FinalizerFlow<WT> {

}
