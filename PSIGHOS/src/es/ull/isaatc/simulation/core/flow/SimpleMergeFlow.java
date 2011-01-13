package es.ull.isaatc.simulation.core.flow;



/**
 * An {@link ORJoinFlow} which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming branches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public interface SimpleMergeFlow extends ORJoinFlow {
}
