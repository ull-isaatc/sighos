/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Identifiable;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ModelObject extends Identifiable, Comparable<ModelObject> {
	Model getModel();
}
