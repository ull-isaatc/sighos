package es.ull.isaatc.simulation.core.flow;



/**
 * An {@link ANDJoinFlow} which allows only the first true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Discriminator pattern (WFP28). 
 * @author Yeray Callero
 */
public interface DiscriminatorFlow extends ANDJoinFlow {
}
