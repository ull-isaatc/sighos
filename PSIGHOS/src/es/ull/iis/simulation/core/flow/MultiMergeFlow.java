/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * An {@link ORJoinFlow}Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public interface MultiMergeFlow<WT extends WorkThread<?>> extends ORJoinFlow<WT> {
}
