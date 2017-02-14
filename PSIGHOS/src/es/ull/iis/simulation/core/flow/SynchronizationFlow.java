package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * An {@link ANDJoinFlow} which passes only when all the incoming branches have been activated once. 
 * It is reset when all the incoming branches are activated exactly once (both true and false).
 * Meets the Synchronization pattern (WFP3). 
 * @author Yeray Callero
 */
public interface SynchronizationFlow extends ANDJoinFlow {
}
