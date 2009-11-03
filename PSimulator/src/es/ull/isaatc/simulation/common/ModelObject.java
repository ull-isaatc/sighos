/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Identifiable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ModelObject extends Identifiable, Comparable<ModelObject> {
	Model getModel();
}
