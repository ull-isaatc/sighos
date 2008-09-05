/**
 * Provides the classes necessary to store and/or recover the simulation state.
 * All the simulation objects implementing {@link es.ull.isaatc.simulation.state.RecoverableState}
 * have an associated {@link es.ull.isaatc.simulation.state.State} class. Thus, 
 * {@link es.ull.isaatc.simulation.Activity} has an {@link es.ull.isaatc.simulation.state.ActivityState} 
 * class.
 * {@link es.ull.isaatc.simulation.state.State} classes are {@link java.io.Serializable} 
 * so they can be used in distributed environments. 
 */
package es.ull.isaatc.simulation.state;