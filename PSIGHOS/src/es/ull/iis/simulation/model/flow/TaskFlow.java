/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * A {@link Flow} which executes some kind of work. A task flow is both an {@link InitializerFlow}
 * and a {@link FinalizerFlow}. After being requested, it must perform some kind of work, and when 
 * this work is finished, it must notify its end.<p>
 * @author Iván Castilla Rodríguez
 *
 */
public interface TaskFlow extends InitializerFlow, FinalizerFlow {

}
