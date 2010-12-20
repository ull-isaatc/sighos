package es.ull.isaatc.simulation.parallel.flow;

/**
 * A flow which finishes an execution branch. Only finalizer flows can be used as the last
 * step in a flow structure. 
 * @author Iv�n Castilla Rodr�guez
 */
public interface FinalizerFlow extends Flow, es.ull.isaatc.simulation.flow.FinalizerFlow {

}
