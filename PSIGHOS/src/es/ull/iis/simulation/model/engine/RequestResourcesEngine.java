/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.ArrayDeque;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.QueuedObject;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow.ActivityWorkGroup;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface RequestResourcesEngine extends QueuedObject<ElementInstance> {
	/**
	 * Checks whether there is a combination of available resources that satisties the 
	 * requirements of a workgroup
	 * @param solution Tentative solution with booked resources
	 * @param wg 
	 * @param fe
	 * @return
	 */
	boolean checkWorkGroup(ArrayDeque<Resource> solution, ActivityWorkGroup wg, ElementInstance fe);
}
