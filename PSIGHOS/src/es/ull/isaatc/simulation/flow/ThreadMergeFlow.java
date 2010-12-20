/**
 * 
 */
package es.ull.isaatc.simulation.flow;



/**
 * An {@link ANDJoinFlow} which merges a specified amount of work threads. It should be used with
 * its counterpart, the {@link ThreadSplitFlow} (WFP 42).
 * Meets the Thread Merge pattern (WFP 41), but has also extra features. Works as
 * a thread discriminator, if its acceptance value  is set to 1; or as a thread 
 * partial join if any other value greater than one and lower than the amount of instances to 
 * be created is used.
 * @author Iván Castilla Rodríguez
 */
public interface ThreadMergeFlow extends ANDJoinFlow {
}
