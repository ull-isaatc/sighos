/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.QueuedObject;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface RequestResourcesEngine extends QueuedObject<ElementInstance> {
	boolean checkWorkGroup(ActivityWorkGroup wg, ElementInstance fe);
}
