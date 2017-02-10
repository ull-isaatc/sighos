package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * An {@link ORJoinFlow} which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming branches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public interface SimpleMergeFlow<WT extends WorkThread<?>> extends ORJoinFlow<WT> {
}
