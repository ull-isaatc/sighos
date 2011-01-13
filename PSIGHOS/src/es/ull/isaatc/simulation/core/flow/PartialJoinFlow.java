package es.ull.isaatc.simulation.core.flow;



/**
 * An {@link ANDJoinFlow} which allows only the n-st true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Partial Join pattern (WFP31). 
 * @author Yeray Callero
 */
public interface PartialJoinFlow extends ANDJoinFlow {
}
