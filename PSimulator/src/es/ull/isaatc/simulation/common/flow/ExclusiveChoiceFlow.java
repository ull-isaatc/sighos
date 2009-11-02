package es.ull.isaatc.simulation.common.flow;



/**
 * A conditional flow which allows only one of the outgoing branches to be activated.
 * Successors are evaluated in order. When one of the outgoing branches meets its
 * associated condition, a new true work thread continues. The rest of branches produce
 * a false work thread.<p>   
 * Meets the Exclusive Choice pattern (WFP4). 
 * Successors are evaluated in order.
 * @author ycallero
 *
 */
public interface ExclusiveChoiceFlow extends ConditionalFlow {
}
