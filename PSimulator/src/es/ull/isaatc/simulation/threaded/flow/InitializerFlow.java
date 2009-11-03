/**
 * 
 */
package es.ull.isaatc.simulation.threaded.flow;

/**
 * A flow which begins an execution branch. Only initializer flows can be used as the first
 * step in a flow structure. 
 * @author Iván Castilla Rodríguez
 */
public interface InitializerFlow extends Flow, es.ull.isaatc.simulation.common.flow.InitializerFlow {
}
