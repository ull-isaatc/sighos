package es.ull.isaatc.simulation.core.flow;



/**
 * A {@link ConditionalFlow} which allows only one of the outgoing branches to be activated.
 * Successors are evaluated in order. When one of the outgoing branches meets its
 * associated condition, a new true work thread continues. The rest of branches produce
 * a false work thread.<p>   
 * Meets the Exclusive Choice pattern (WFP4). 
 * @author Yeray Callero
 *
 */
public interface ExclusiveChoiceFlow extends ConditionalFlow {
}
