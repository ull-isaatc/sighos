package es.ull.isaatc.simulation.common.flow;




/**
 * An AND join flow which passes only when all the incoming branches have been activated once. 
 * It is reset when all the incoming branches are activated exactly once (both true and false).
 * Meets the Synchronization pattern (WFP3). 
 * @author ycallero
 */
public interface SynchronizationFlow extends ANDJoinFlow {
}
