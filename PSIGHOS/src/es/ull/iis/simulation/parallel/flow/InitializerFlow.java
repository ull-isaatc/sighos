/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

/**
 * A flow which begins an execution branch. Only initializer flows can be used as the first
 * step in a flow structure. 
 * @author Iván Castilla Rodríguez
 */
public interface InitializerFlow extends Flow, es.ull.iis.simulation.core.flow.InitializerFlow {
}
