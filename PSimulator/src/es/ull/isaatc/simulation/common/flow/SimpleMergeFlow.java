package es.ull.isaatc.simulation.common.flow;



/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public interface SimpleMergeFlow extends ORJoinFlow {
}
