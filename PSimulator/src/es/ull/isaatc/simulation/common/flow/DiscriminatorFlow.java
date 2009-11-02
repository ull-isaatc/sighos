package es.ull.isaatc.simulation.common.flow;



/**
 * An AND join flow which allows only the first true incoming branch to pass. It is
 * reset when all the incoming branches are activated exactly once.
 * Meets the Blocking Discriminator pattern (WFP28). 
 * @author ycallero
 */
public interface DiscriminatorFlow extends ANDJoinFlow {
}
