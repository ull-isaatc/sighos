package es.ull.isaatc.simulation.flow;



/**
 * A {@link ConditionalFlow} which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * @author Yeray Callero
 */
public interface MultiChoiceFlow extends ConditionalFlow {
}
