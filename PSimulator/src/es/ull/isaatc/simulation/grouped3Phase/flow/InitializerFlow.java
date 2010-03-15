/**
 * 
 */
package es.ull.isaatc.simulation.grouped3Phase.flow;

/**
 * A flow which begins an execution branch. Only initializer flows can be used as the first
 * step in a flow structure. 
 * @author Iv�n Castilla Rodr�guez
 */
public interface InitializerFlow extends Flow, es.ull.isaatc.simulation.common.flow.InitializerFlow {
}
